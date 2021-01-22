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

import javax.inject.Inject
import models.UserAnswers
import models.hallmarks.JourneyStatus.{Completed, InProgress, NotStarted}
import pages.arrangement.ArrangementStatusPage
import pages.disclosure.DisclosureIdentifyArrangementPage
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
        "hallmarksTaskListItem" -> hallmarksTaskListItem(request.userAnswers.get),
        "arrangementDetailsTaskListItem" -> arrangementsTaskListItem(request.userAnswers.get),
        "reporterDetailsTaskListItem" -> reporterDetailsTaskListItem(request.userAnswers.get),
        "relevantTaxpayerTaskListItem" -> relevantTaxpayerTaskListItem(request.userAnswers.get),
        "intermediariesTaskListItem" -> intermediariesTaskListItem(request.userAnswers.get),
        "disclosureUrl" -> frontendAppConfig.disclosureUrl
      )


      renderer.render("disclosureDetails.njk", json).map(Ok(_))
  }

  private def hallmarksTaskListItem(ua: UserAnswers)(implicit messages: Messages) = {

    val url = frontendAppConfig.hallmarksUrl
    val linkContent = "disclosureDetails.hallmarksLink"
    val aria = "arrangementDetails"

    ua.get(HallmarkStatusPage) match {
      case Some(Completed) => taskListHtmlProvider(url, Completed.toString, linkContent, "disclosure-type-complete", aria)
      case Some(InProgress) => taskListHtmlProvider(url, InProgress.toString, linkContent, "disclosure-type-inProgress", aria)
      case _ => taskListHtmlProvider(url, NotStarted.toString, linkContent, "disclosure-type-notStarted", aria)
    }
  }

  private def arrangementsTaskListItem(ua: UserAnswers)(implicit messages: Messages) = {

    val url = frontendAppConfig.arrangementsUrl
    val linkContent = "disclosureDetails.arrangementDetailsLink"
    val aria = "arrangementDetails"

    ua.get(ArrangementStatusPage) match {
      case Some(Completed) => taskListHtmlProvider(url, Completed.toString, linkContent, "arrangement-complete", aria)
      case Some(InProgress) => taskListHtmlProvider(url, InProgress.toString, linkContent, "arrangement-inProgress", aria)
      case _ => taskListHtmlProvider(url, NotStarted.toString, linkContent, "arrangement-notStarted", aria)
    }
  }

  private def relevantTaxpayerTaskListItem(ua: UserAnswers)(implicit messages: Messages) = {

    val url = frontendAppConfig.taxpayersUrl
    val linkContent = "disclosureDetails.relevantTaxpayersLink"
    val aria = "connected-parties"

    ua.get(RelevantTaxpayerStatusPage) match {
      case Some(Completed) => taskListHtmlProvider(url, Completed.toString, linkContent, "taxpayers-complete", aria)
      case Some(InProgress) => taskListHtmlProvider(url, InProgress.toString, linkContent, "taxpayers-inProgress", aria)
      case _ => taskListHtmlProvider(url, NotStarted.toString, linkContent, "taxpayers-notStarted", aria)
    }
  }

  private def intermediariesTaskListItem(ua: UserAnswers)(implicit messages: Messages) = {

    val url = frontendAppConfig.intermediariesUrl
    val linkContent = "disclosureDetails.intermediariesLink"
    val aria = "connected-parties"

    ua.get(IntermediariesStatusPage) match {
      case Some(Completed) => taskListHtmlProvider(url, Completed.toString, linkContent, "intermediaries-complete", aria)
      case Some(InProgress) => taskListHtmlProvider(url, InProgress.toString, linkContent, "intermediaries-inProgress", aria)
      case _ => taskListHtmlProvider(url, NotStarted.toString, linkContent, "intermediaries-notStarted", aria)
    }
  }

  private def reporterDetailsTaskListItem(ua: UserAnswers)(implicit messages: Messages) = {

    val url = frontendAppConfig.intermediariesUrl
    val linkContent = "disclosureDetails.reporterDetailsLink"
    val aria = "reporterDetails"

    ua.get(ReporterStatusPage) match {
      case Some(Completed) => taskListHtmlProvider(url, Completed.toString, linkContent, "reporter-complete", aria)
      case Some(InProgress) => taskListHtmlProvider(url, InProgress.toString, linkContent, "reporter-inProgress", aria)
      case _ => taskListHtmlProvider(url, NotStarted.toString, linkContent, "reporter-notStarted", aria)
    }
  }

}
