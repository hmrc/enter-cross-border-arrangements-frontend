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
import controllers.actions._
import controllers.exceptions.DiscloseDetailsAlreadySentException
import controllers.mixins.DefaultRouting
import helpers.TaskListHelper._
import models.hallmarks.JourneyStatus
import models.hallmarks.JourneyStatus.{Completed, InProgress, NotStarted, Restricted}
import models.reporter.RoleInArrangement.Taxpayer
import models.{NormalMode, Submission, UserAnswers, WithName}
import navigation.NavigatorForDisclosure
import pages.affected.AffectedStatusPage
import pages.arrangement.ArrangementStatusPage
import pages.disclosure.{DisclosureDetailsPage, DisclosureStatusPage}
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
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.Radios.MessageInterpolators

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Try}

class DisclosureDetailsController @Inject() (
  override val messagesApi: MessagesApi,
  xmlGenerationService: XMLGenerationService,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  contactRetrievalAction: ContactRetrievalAction,
  frontendAppConfig: FrontendAppConfig,
  val controllerComponents: MessagesControllerComponents,
  navigator: NavigatorForDisclosure,
  renderer: Renderer,
  sessionRepository: SessionRepository
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(id: Int): Action[AnyContent] = (identify andThen getData.apply() andThen requireData).async {
    implicit request =>
      val disclosureDetails = request.userAnswers.get(DisclosureDetailsPage, id).filterNot(_.sent).getOrElse(throw new DiscloseDetailsAlreadySentException(id))

      val arrangementMessage: String = disclosureDetails.arrangementID.fold("")(msg"disclosureDetails.heading.forArrangement".withArgs(_).resolve)

      val summaryLink = controllers.routes.SummaryController.onPageLoad(id).url

      val json = Json.obj(
        "id"                    -> id,
        "arrangementID"         -> arrangementMessage,
        "hallmarksTaskListItem" -> hallmarksItem(request.userAnswers, HallmarkStatusPage, id),
        "arrangementDetailsTaskListItem" -> arrangementsItem(request.userAnswers,
                                                             ArrangementStatusPage,
                                                             id,
                                                             disclosureDetails.firstInitialDisclosureMA.getOrElse(false)
        ),
        "reporterDetailsTaskListItem"      -> reporterDetailsItem(request.userAnswers, ReporterStatusPage, id),
        "relevantTaxpayerTaskListItem"     -> relevantTaxpayersItem(request.userAnswers, RelevantTaxpayerStatusPage, id),
        "associatedEnterpriseTaskListItem" -> associatedEnterpriseItem(request.userAnswers, AssociatedEnterpriseStatusPage, id),
        "intermediariesTaskListItem"       -> intermediariesItem(request.userAnswers, IntermediariesStatusPage, id),
        "othersAffectedTaskListItem"       -> othersAffectedItem(request.userAnswers, AffectedStatusPage, id),
        "disclosureTaskListItem"           -> disclosureTypeItem(request.userAnswers, DisclosureStatusPage, id),
        "userCanSubmit"                    -> userCanSubmit(request.userAnswers, id, disclosureDetails),
        "displaySectionOptional"           -> displaySectionOptional(disclosureDetails),
        "backLink"                         -> backLink,
        "summaryLink"                      -> summaryLink
      )
      renderer.render("disclosure/disclosureDetails.njk", json).map(Ok(_))

  }

  def onSubmit(id: Int): Action[AnyContent] = (identify andThen getData.apply() andThen requireData andThen contactRetrievalAction.apply).async {
    implicit request =>
      val submission = Submission(request.userAnswers, id, request.enrolmentID)

      xmlGenerationService.createAndValidateXmlSubmission(submission).flatMap {
        _.fold(
          errors =>
            for {
              updatedAnswersWithError <- Future.fromTry(request.userAnswers.set(ValidationErrorsPage, id, errors))
              _                       <- sessionRepository.set(updatedAnswersWithError)
            } yield Redirect(controllers.confirmation.routes.DisclosureValidationErrorsController.onPageLoad(id).url),
          updatedIds =>
            for {
              updatedUserAnswersWithSubmission <- Future.fromTry(request.userAnswers.set(GeneratedIDPage, id, updatedIds))
              updatedUserAnswersWithFlags      <- Future.fromTry(updateFlags(updatedUserAnswersWithSubmission, id))
              _                                <- sessionRepository.set(updatedUserAnswersWithFlags)
            } yield Redirect(controllers.confirmation.routes.FileTypeGatewayController.onRouting(id).url)
        )
      }
  }

  private[controllers] def updateFlags(userAnswers: UserAnswers, id: Int): Try[UserAnswers] =
    (userAnswers.getBase(UnsubmittedDisclosurePage) map {
      unsubmittedDisclosures =>
        val unsubmittedDisclosure         = UnsubmittedDisclosurePage.fromIndex(id)(userAnswers)
        val updatedUnsubmittedDisclosures = unsubmittedDisclosures.zipWithIndex.filterNot(_._2 == id).map(_._1)
        userAnswers.setBase(UnsubmittedDisclosurePage, updatedUnsubmittedDisclosures :+ unsubmittedDisclosure.copy(submitted = true))
    }).getOrElse(Failure(new IllegalArgumentException("Unable to update unsubmitted disclosure.")))

  private def backLink: String =
    navigator.routeMap(DisclosureDetailsPage)(DefaultRouting(NormalMode))(None)(None)(0).url

  private def disclosureTypeItem(ua: UserAnswers, page: QuestionPage[JourneyStatus], index: Int)(implicit messages: Messages) =
    retrieveRowWithStatus(
      getJourneyStatus(ua, page, index),
      None,
      linkContent = "disclosureDetails.disclosureTypeLink",
      id = "disclosure",
      ariaLabel = "disclosure-details",
      rowStyle = "item"
    )

  private def hallmarksItem(ua: UserAnswers, page: QuestionPage[JourneyStatus], index: Int)(implicit messages: Messages) = {

    val dynamicLink = hrefToStartJourneyOrCya(ua, page, s"${frontendAppConfig.hallmarksUrl}/$index", s"${frontendAppConfig.hallmarksCYAUrl}/$index", index)

    retrieveRowWithStatus(
      getJourneyStatus(ua, page, index),
      Some(dynamicLink),
      linkContent = "disclosureDetails.hallmarksLink",
      id = "hallmarks",
      ariaLabel = "arrangementDetails",
      rowStyle = "item"
    )
  }

  private def arrangementsItem(ua: UserAnswers, page: QuestionPage[JourneyStatus], index: Int, isInitialDisclosureMarketable: Boolean)(implicit
    messages: Messages
  ) = {

    val dynamicLink =
      hrefToStartJourneyOrCya(ua, page, s"${frontendAppConfig.arrangementsUrl}/$index", s"${frontendAppConfig.arrangementsCYAUrl}/$index", index)

    val isHallmarkSectionComplete = ua.get(HallmarkStatusPage, index) match {
      case None if isInitialDisclosureMarketable => Restricted
      case _                                     => getJourneyStatus(ua, page, index)
    }

    retrieveRowWithStatus(
      isHallmarkSectionComplete,
      Some(dynamicLink),
      linkContent = "disclosureDetails.arrangementDetailsLink",
      id = "arrangementDetails",
      ariaLabel = "arrangementDetails",
      rowStyle = "item"
    )
  }

  private def reporterDetailsItem(ua: UserAnswers, page: QuestionPage[JourneyStatus], index: Int)(implicit messages: Messages) = {

    val dynamicLink = hrefToStartJourneyOrCya(ua, page, s"${frontendAppConfig.reportersUrl}/$index", s"${frontendAppConfig.reportersCYAUrl}/$index", index)

    retrieveRowWithStatus(
      getJourneyStatus(ua, page, index),
      Some(dynamicLink),
      linkContent = "disclosureDetails.reporterDetailsLink",
      id = "reporter",
      ariaLabel = "reporterDetails",
      rowStyle = "item"
    )
  }

  private def relevantTaxpayersItem(ua: UserAnswers, page: QuestionPage[JourneyStatus], index: Int)(implicit messages: Messages) = {

    val isReporterSectionComplete = ua.get(ReporterStatusPage, index) match {
      case Some(Completed) => getJourneyStatus(ua, page, index)
      case _               => Restricted
    }

    retrieveRowWithStatus(
      isReporterSectionComplete,
      Some(s"${frontendAppConfig.taxpayersUrl}/$index"),
      linkContent = "disclosureDetails.relevantTaxpayersLink",
      id = "taxpayers",
      ariaLabel = "connected-parties",
      rowStyle = "bottomless-item"
    )
  }

  private def associatedEnterpriseItem(ua: UserAnswers, page: QuestionPage[JourneyStatus], index: Int)(implicit messages: Messages) = {

    val isTaxpayerSectionComplete =
      (ua.get(RoleInArrangementPage, index), ua.get(TaxpayerLoopPage, index), ua.get(RelevantTaxpayerStatusPage, index)) match {
        case (Some(Taxpayer), _, _)              => getJourneyStatus(ua, page, index)
        case (Some(_), Some(_), Some(Completed)) => getJourneyStatus(ua, page, index)
        case _                                   => Restricted
      }

    retrieveRowWithStatus(
      isTaxpayerSectionComplete,
      Some(s"${frontendAppConfig.associatedEnterpriseUrl}/$index"),
      linkContent = "disclosureDetails.associatedEnterpriseLink",
      id = "associatedEnterprise",
      ariaLabel = "connected-parties",
      rowStyle = "item"
    )
  }

  private def othersAffectedItem(ua: UserAnswers, page: QuestionPage[JourneyStatus], index: Int)(implicit messages: Messages) =
    retrieveRowWithStatus(
      getJourneyStatus(ua, page, index),
      Some(s"${frontendAppConfig.othersAffectedUrl}/$index"),
      linkContent = "disclosureDetails.othersAffectedLink",
      id = "othersAffected",
      ariaLabel = "connected-parties",
      rowStyle = "item"
    )

  private def intermediariesItem(ua: UserAnswers, page: QuestionPage[JourneyStatus], index: Int)(implicit messages: Messages) = {

    val isReporterSectionComplete = ua.get(ReporterStatusPage, index) match {
      case Some(Completed) => getJourneyStatus(ua, page, index)
      case _               => Restricted
    }

    retrieveRowWithStatus(
      isReporterSectionComplete,
      Some(s"${frontendAppConfig.intermediariesUrl}/$index"),
      linkContent = "disclosureDetails.intermediariesLink",
      id = "intermediaries",
      ariaLabel = "connected-parties",
      rowStyle = "item",
      extraStyle = Some("padding-top: 1.5em;")
    )
  }

  private def getJourneyStatus(ua: UserAnswers, page: QuestionPage[JourneyStatus], index: Int): WithName with JourneyStatus =
    ua.get(page, index) match {
      case Some(Completed)  => Completed
      case Some(InProgress) => InProgress
      case _                => NotStarted
    }
}
