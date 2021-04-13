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

import controllers.actions._
import models.Submission
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.rows.SummaryListDisplay
import utils.{SummaryImplicits, SummaryListGenerator}

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class SummaryController @Inject()(
    override val messagesApi: MessagesApi,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with SummaryImplicits {

  def onPageLoad(id: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val summaryListGenerator = new SummaryListGenerator()
      val submission = Submission(request.userAnswers, id, request.enrolmentID)

      val disclosureList: Seq[SummaryListDisplay.DisplayRow] = summaryListGenerator.generateSummaryList(id, submission.disclosureDetails)

      //ToDo refactor to remove get on option
      val arrangementList =  summaryListGenerator.generateSummaryList(id, submission.arrangementDetails.get)

      renderer.render("summary.njk",
        Json.obj(
          "disclosureList" -> disclosureList,
                 "arrangementList" -> arrangementList
      )
      ).map(Ok(_))
  }
}
