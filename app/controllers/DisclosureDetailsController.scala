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
import helpers.TaskListHelper._
import javax.inject.Inject
import models.UserAnswers
import models.hallmarks.JourneyStatus
import models.hallmarks.JourneyStatus.Completed
import pages.QuestionPage
import pages.arrangement.ArrangementStatusPage
import pages.disclosure.{DisclosureIdentifyArrangementPage, DisclosureStatusPage}
import pages.hallmarks.HallmarkStatusPage
import pages.intermediaries.IntermediariesStatusPage
import pages.reporter.ReporterStatusPage
import pages.taxpayer.RelevantTaxpayerStatusPage
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import pages.disclosure.{DisclosureDetailsPage, DisclosureIdentifyArrangementPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.Radios.MessageInterpolators

import scala.concurrent.ExecutionContext

class DisclosureDetailsController @Inject()(
    override val messagesApi: MessagesApi,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    frontendAppConfig: FrontendAppConfig,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(id: Int): Action[AnyContent] = (identify andThen getData).async {
    implicit request =>

      val arrangementMessage: String = request.userAnswers.fold("") {
        value => value.get(DisclosureDetailsPage, id).flatMap(_.arrangementID)
          .map(msg"disclosureDetails.heading.forArrangement".withArgs(_).resolve)
          .getOrElse("")
      }


      val json = Json.obj(
        "arrangementID" -> arrangementMessage,
        "hallmarksTaskListItem" -> hallmarksItem(request.userAnswers.get, HallmarkStatusPage),
        "arrangementDetailsTaskListItem" -> arrangementsItem(request.userAnswers.get, ArrangementStatusPage),
        "reporterDetailsTaskListItem" -> reporterDetailsItem(request.userAnswers.get, ReporterStatusPage),
        "relevantTaxpayerTaskListItem" -> relevantTaxpayersItem(request.userAnswers.get, RelevantTaxpayerStatusPage),
        "intermediariesTaskListItem" -> intermediariesItem(request.userAnswers.get, IntermediariesStatusPage),
        "disclosureTaskListItem" -> disclosureTypeItem(request.userAnswers.get, DisclosureStatusPage),
        "userCanSubmit" -> userCanSubmit(request.userAnswers.get),
        "displaySectionOptional" -> displaySectionOptional(request.userAnswers.get)
      )
      renderer.render("disclosureDetails.njk", json).map(Ok(_))
  }


  private def disclosureTypeItem(ua: UserAnswers,
                                 page: QuestionPage[JourneyStatus])(implicit messages: Messages) = {

    val dynamicLink = startJourneyOrCya(ua, page, frontendAppConfig.disclosureStartUrl, frontendAppConfig.disclosureCYAUrl)

    retrieveRowWithStatus(ua,
      page,
      dynamicLink,
      linkContent = "disclosureDetails.disclosureTypeLink",
      id = "disclosure",
      ariaLabel = "disclosure-details"
    )
  }

  private def hallmarksItem(ua: UserAnswers,
                            page: QuestionPage[JourneyStatus])(implicit messages: Messages) = {

    val dynamicLink = startJourneyOrCya(ua, page, frontendAppConfig.hallmarksUrl, frontendAppConfig.hallmarksCYAUrl)

    retrieveRowWithStatus(ua: UserAnswers,
      page,
      dynamicLink,
      linkContent = "disclosureDetails.hallmarksLink",
      id = "hallmarks",
      ariaLabel = "arrangementDetails"
    )
  }

  private def arrangementsItem(ua: UserAnswers,
                               page: QuestionPage[JourneyStatus])(implicit messages: Messages) = {

    val dynamicLink = startJourneyOrCya(ua, page, frontendAppConfig.arrangementsUrl, frontendAppConfig.arrangementsCYAUrl)

    retrieveRowWithStatus(ua: UserAnswers,
      page,
      dynamicLink,
      linkContent = "disclosureDetails.arrangementDetailsLink",
      id = "arrangementDetails",
      ariaLabel = "arrangementDetails"
    )
  }

  private def reporterDetailsItem(ua: UserAnswers,
                                  page: QuestionPage[JourneyStatus])(implicit messages: Messages) = {

    val dynamicLink = startJourneyOrCya(ua, page, frontendAppConfig.reportersUrl, frontendAppConfig.reportersCYAUrl)

    retrieveRowWithStatus(ua: UserAnswers,
      page,
      dynamicLink,
      linkContent = "disclosureDetails.reporterDetailsLink",
      id = "reporter",
      ariaLabel = "reporterDetails"
    )
  }

  private def relevantTaxpayersItem(ua: UserAnswers,
                                    page: QuestionPage[JourneyStatus])(implicit messages: Messages) = {

    ua.get(ReporterStatusPage) match {
      case Some(Completed) =>
        retrieveRowWithStatus(ua: UserAnswers,
          page,
          frontendAppConfig.taxpayersUrl,
          linkContent = "disclosureDetails.relevantTaxpayersLink",
          id = "taxpayers",
          ariaLabel = "connected-parties"
        )

      case _ => taskListItemRestricted(
        "disclosureDetails.relevantTaxpayersLink", "connected-parties")
    }
  }

  private def intermediariesItem(ua: UserAnswers,
                                 page: QuestionPage[JourneyStatus])(implicit messages: Messages) = {

    ua.get(ReporterStatusPage) match {
      case Some(Completed) =>
        retrieveRowWithStatus(ua: UserAnswers,
          page,
          frontendAppConfig.intermediariesUrl,
          linkContent = "disclosureDetails.intermediariesLink",
          id = "intermediaries",
          ariaLabel = "connected-parties"
        )

      case _ => taskListItemRestricted(
        "disclosureDetails.intermediariesLink", "connected-parties")
    }
  }
}
