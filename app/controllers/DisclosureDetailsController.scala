/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import config.FrontendAppConfig
import connectors.HistoryConnector
import controllers.actions._
import controllers.mixins.DefaultRouting
import helpers.TaskListHelper._
import models.disclosure.DisclosureType
import models.disclosure.DisclosureType.Dac6rep
import models.hallmarks.JourneyStatus
import models.hallmarks.JourneyStatus.{Completed, NotStarted}
import models.{NormalMode, Submission, UserAnswers}
import navigation.NavigatorForDisclosure
import pages.affected.AffectedStatusPage
import pages.arrangement.ArrangementStatusPage
import pages.disclosure.{DisclosureDetailsPage, DisclosureStatusPage, FirstInitialDisclosureMAPage}
import pages.enterprises.AssociatedEnterpriseStatusPage
import pages.hallmarks.HallmarkStatusPage
import pages.intermediaries.IntermediariesStatusPage
import pages.reporter.{ReporterStatusPage, RoleInArrangementPage}
import pages.taxpayer.{RelevantTaxpayerStatusPage, TaxpayerLoopPage}
import pages.unsubmitted.UnsubmittedDisclosurePage
import pages.{GeneratedIDPage, QuestionPage, ValidationErrorsPage}
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import services.XMLGenerationService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.Radios.MessageInterpolators

import javax.inject.Inject
import models.reporter.RoleInArrangement.Taxpayer

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Try}

class DisclosureDetailsController @Inject()(
    override val messagesApi: MessagesApi,
    xmlGenerationService: XMLGenerationService,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    contactRetrievalAction: ContactRetrievalAction,
    historyConnector: HistoryConnector,
    frontendAppConfig: FrontendAppConfig,
    val controllerComponents: MessagesControllerComponents,
    navigator: NavigatorForDisclosure,
    renderer: Renderer,
    sessionRepository: SessionRepository
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(id: Int): Action[AnyContent] = (identify andThen getData).async {
    implicit request =>

      val arrangementMessage: String = request.userAnswers.fold("") {
        value => value.get(DisclosureDetailsPage, id).flatMap(_.arrangementID)
          .map(msg"disclosureDetails.heading.forArrangement".withArgs(_).resolve)
          .getOrElse("")
      }

      isInitialDisclosureMarketable(request.userAnswers.get, id).flatMap { isInitialDisclosureMarketable =>

        val json = Json.obj(
          "id" -> id,
          "arrangementID" -> arrangementMessage,
          "hallmarksTaskListItem" -> hallmarksItem(request.userAnswers.get, HallmarkStatusPage, id),
          "arrangementDetailsTaskListItem" -> arrangementsItem(request.userAnswers.get, ArrangementStatusPage, id, isInitialDisclosureMarketable),
          "reporterDetailsTaskListItem" -> reporterDetailsItem(request.userAnswers.get, ReporterStatusPage, id),
          "relevantTaxpayerTaskListItem" -> relevantTaxpayersItem(request.userAnswers.get, RelevantTaxpayerStatusPage, id),
          "associatedEnterpriseTaskListItem" -> associatedEnterpriseItem(request.userAnswers.get, AssociatedEnterpriseStatusPage, id),
          "intermediariesTaskListItem" -> intermediariesItem(request.userAnswers.get, IntermediariesStatusPage, id),
          "othersAffectedTaskListItem" -> othersAffectedItem(request.userAnswers.get, AffectedStatusPage, id),
          "disclosureTaskListItem" -> disclosureTypeItem(request.userAnswers.get, DisclosureStatusPage, id),
          "userCanSubmit" -> userCanSubmit(request.userAnswers.get, id, isInitialDisclosureMarketable),
          "displaySectionOptional" -> displaySectionOptional(request.userAnswers.get, id, isInitialDisclosureMarketable),
          "backLink" -> backLink
        )
        renderer.render("disclosure/disclosureDetails.njk", json).map(Ok(_))
      }
  }

  def onSubmit(id: Int): Action[AnyContent] = (identify andThen getData andThen requireData andThen contactRetrievalAction).async {
    implicit request =>

      val submission = Submission(request.userAnswers, id, request.enrolmentID)

      xmlGenerationService.createAndValidateXmlSubmission(submission).flatMap {
        _.fold (
          errors =>
            for {
              updatedAnswersWithError <- Future.fromTry(request.userAnswers.set(ValidationErrorsPage, id, errors))
              _                       <- sessionRepository.set(updatedAnswersWithError)
            } yield Redirect(controllers.confirmation.routes.DisclosureValidationErrorsController.onPageLoad(id).url)
          ,
          updatedIds =>
            for {
              updatedUserAnswersWithSubmission <- Future.fromTry(request.userAnswers.set(GeneratedIDPage, id, updatedIds))
              updatedUserAnswersWithFlags      <- Future.fromTry(updateFlags(updatedUserAnswersWithSubmission, id))
              _                                <- sessionRepository.set(updatedUserAnswersWithFlags)
            } yield Redirect(controllers.confirmation.routes.FileTypeGatewayController.onRouting(id).url)
        )
      }
  }

  private def isInitialDisclosureMarketable(userAnswers: UserAnswers, id: Int)
                                           (implicit hc: HeaderCarrier): Future[Boolean] = {

    val disclosureDetails = userAnswers.get(DisclosureDetailsPage, id) match {
      case Some(details) => details
      case None => throw new Exception("Missing disclosure details")
    }

    disclosureDetails.disclosureType match {
      case DisclosureType.Dac6add =>
        historyConnector.retrieveFirstDisclosureForArrangementID(disclosureDetails.arrangementID.getOrElse("")).map {
          firstDisclosureDetails =>
            firstDisclosureDetails.initialDisclosureMA
        }
      case DisclosureType.Dac6rep =>
        historyConnector.retrieveFirstDisclosureForArrangementID(disclosureDetails.arrangementID.getOrElse("")).flatMap {
          firstDisclosureDetails =>
            historyConnector.searchDisclosures(disclosureDetails.disclosureID.getOrElse("")).flatMap {
              submissionHistory =>
                for {
                  userAnswers <- Future.fromTry(userAnswers.setBase(FirstInitialDisclosureMAPage, firstDisclosureDetails.initialDisclosureMA))
                  _           <- sessionRepository.set(userAnswers)
                } yield {
                  if (submissionHistory.details.nonEmpty &&
                    submissionHistory.details.head.importInstruction == "Add" &&
                    firstDisclosureDetails.initialDisclosureMA) {
                    //Note: There should only be one submission returned with an ADD instruction for the given disclosure ID
                    true
                  } else {
                    false
                  }
                }
            }
        }
      case _ => Future.successful(false)
    }
  }

  private def isReplacingAMarketableAddDisclosure(userAnswers: UserAnswers, id: Int)
                                                 (implicit hc: HeaderCarrier): Future[Boolean] = {

    val disclosureDetails = userAnswers.get(DisclosureDetailsPage, id) match {
      case Some(details) => details
      case None => throw new Exception("Missing disclosure details")
    }

    if (disclosureDetails.disclosureType == Dac6rep) {
      historyConnector.retrieveFirstDisclosureForArrangementID(disclosureDetails.arrangementID.getOrElse("")).flatMap {
        firstDisclosureDetails =>
          historyConnector.searchDisclosures(disclosureDetails.disclosureID.getOrElse("")).flatMap {
            submissionHistory =>
              for {
                userAnswers <- Future.fromTry(userAnswers.setBase(FirstInitialDisclosureMAPage, firstDisclosureDetails.initialDisclosureMA))
                _           <- sessionRepository.set(userAnswers)
              } yield {
                if (submissionHistory.details.nonEmpty &&
                  submissionHistory.details.head.importInstruction == "Add" &&
                  firstDisclosureDetails.initialDisclosureMA) {
                  //Note: There should only be one submission returned with an ADD instruction for the given disclosure ID
                  true
                } else {
                  false
                }
              }
          }
      }
    } else {
      Future.successful(false)
    }
  }

  private[controllers] def updateFlags(userAnswers: UserAnswers, id: Int): Try[UserAnswers] = {
    (userAnswers.getBase(UnsubmittedDisclosurePage) map { unsubmittedDisclosures =>
      val unsubmittedDisclosure = UnsubmittedDisclosurePage.fromIndex(id)(userAnswers)
      val updatedUnsubmittedDisclosures = unsubmittedDisclosures.zipWithIndex.filterNot { _._2 == id }.map { _._1 }
      userAnswers.setBase(UnsubmittedDisclosurePage, updatedUnsubmittedDisclosures :+ unsubmittedDisclosure.copy(submitted = true))
    }).getOrElse(Failure(new IllegalArgumentException("Unable to update unsubmitted disclosure.")))
  }

  private def backLink: String =
    navigator.routeMap(DisclosureDetailsPage)(DefaultRouting(NormalMode))(None)(None)(0).url

  private def disclosureTypeItem(ua: UserAnswers,
                                 page: QuestionPage[JourneyStatus], index: Int)(implicit messages: Messages) = {

    ua.get(DisclosureStatusPage, index) match {
      case Some(Completed) =>
        taskListItemNotLinkedProvider(JourneyStatus.Completed.toString, "disclosureDetails.disclosureTypeLink", "disclosure", "disclosure-details", "item")

      case _ =>
        retrieveRowWithStatus(ua,
          page,
          "",
          linkContent = "disclosureDetails.disclosureTypeLink",
          id = "disclosure",
          ariaLabel = "disclosure-details",
          rowStyle = "item",
          index
        )
    }
  }

  private def hallmarksItem(ua: UserAnswers,
                            page: QuestionPage[JourneyStatus], index: Int)(implicit messages: Messages) = {

    val dynamicLink = startJourneyOrCya(ua, page, s"${frontendAppConfig.hallmarksUrl}/$index", s"${frontendAppConfig.hallmarksCYAUrl}/$index", index)

    retrieveRowWithStatus(ua: UserAnswers,
      page,
      dynamicLink,
      linkContent = "disclosureDetails.hallmarksLink",
      id = "hallmarks",
      ariaLabel = "arrangementDetails",
      rowStyle = "item",
      index
    )
  }

  private def arrangementsItem(ua: UserAnswers,
                               page: QuestionPage[JourneyStatus],
                               index: Int,
                               isInitialDisclosureMarketable: Boolean)(implicit messages: Messages) = {

    val dynamicLink = startJourneyOrCya(ua, page, s"${frontendAppConfig.arrangementsUrl}/$index", s"${frontendAppConfig.arrangementsCYAUrl}/$index", index)

    ua.get(HallmarkStatusPage, index) match {
      case None if isInitialDisclosureMarketable =>
        taskListItemRestricted(
          "disclosureDetails.arrangementDetailsLink", "arrangementDetails", "item")
      case _ =>
        retrieveRowWithStatus(ua: UserAnswers,
          page,
          dynamicLink,
          linkContent = "disclosureDetails.arrangementDetailsLink",
          id = "arrangementDetails",
          ariaLabel = "arrangementDetails",
          rowStyle = "item",
          index
        )
    }
  }

  private def reporterDetailsItem(ua: UserAnswers,
                                  page: QuestionPage[JourneyStatus], index: Int)(implicit messages: Messages) = {

    val dynamicLink = startJourneyOrCya(ua, page, s"${frontendAppConfig.reportersUrl}/$index", s"${frontendAppConfig.reportersCYAUrl}/$index", index)

    retrieveRowWithStatus(ua: UserAnswers,
      page,
      dynamicLink,
      linkContent = "disclosureDetails.reporterDetailsLink",
      id = "reporter",
      ariaLabel = "reporterDetails",
      rowStyle = "item",
      index
    )
  }

  private def relevantTaxpayersItem(ua: UserAnswers,
                                    page: QuestionPage[JourneyStatus], index: Int)(implicit messages: Messages) = {
      ua.get(ReporterStatusPage, index) match {
        case Some(Completed) =>
          retrieveRowWithStatus(ua: UserAnswers,
            page,
            s"${frontendAppConfig.taxpayersUrl}/$index",
            linkContent = "disclosureDetails.relevantTaxpayersLink",
            id = "taxpayers",
            ariaLabel = "connected-parties",
            rowStyle = "bottomless-item",
            index
          )

        case _ => taskListItemRestricted(
          "disclosureDetails.relevantTaxpayersLink", "connected-parties", "bottomless-item")
      }

    }

  private def associatedEnterpriseItem(ua: UserAnswers,
                                       page: QuestionPage[JourneyStatus], index: Int)(implicit messages: Messages) = {

    val rowWithStatus =
      retrieveRowWithStatus(ua: UserAnswers,
      page,
      s"${frontendAppConfig.associatedEnterpriseUrl}/$index",
      linkContent = "disclosureDetails.associatedEnterpriseLink",
      id = "associatedEnterprise",
      ariaLabel = "connected-parties",
      rowStyle = "item",
      index
    )

    (ua.get(RoleInArrangementPage, index), ua.get(TaxpayerLoopPage, index), ua.get(RelevantTaxpayerStatusPage, index))  match {
      case (Some(Taxpayer), _, _) =>
        rowWithStatus

      case (Some(_), Some(_), Some(Completed)) =>
        rowWithStatus

      case _ => taskListItemRestricted(
        "disclosureDetails.associatedEnterpriseLink", "connected-parties", "item")
    }
  }

  private def othersAffectedItem(ua: UserAnswers,
                                 page: QuestionPage[JourneyStatus], index: Int)(implicit messages: Messages) = {

    retrieveRowWithStatus(ua: UserAnswers,
      page,
      s"${frontendAppConfig.othersAffectedUrl}/$index",
      linkContent = "disclosureDetails.othersAffectedLink",
      id = "othersAffected",
      ariaLabel = "connected-parties",
      rowStyle = "item",
      index
    )
  }

  private def intermediariesItem(ua: UserAnswers,
                                 page: QuestionPage[JourneyStatus], index: Int)(implicit messages: Messages) = {

    ua.get(ReporterStatusPage, index) match {
      case Some(Completed) =>
        retrieveRowWithStatus(ua: UserAnswers,
          page,
          s"${frontendAppConfig.intermediariesUrl}/$index",
          linkContent = "disclosureDetails.intermediariesLink",
          id = "intermediaries",
          ariaLabel = "connected-parties",
          rowStyle = "item",
          index
        )

      case _ => taskListItemRestricted(
        "disclosureDetails.intermediariesLink", "connected-parties", "item")
    }
  }
}
