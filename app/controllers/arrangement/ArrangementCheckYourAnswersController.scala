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

package controllers.arrangement

import controllers.actions._
import javax.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.SummaryList
import utils.CheckYourAnswersHelper

import scala.concurrent.ExecutionContext

class ArrangementCheckYourAnswersController @Inject()(
    override val messagesApi: MessagesApi,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      val helper = new CheckYourAnswersHelper(request.userAnswers)

      val list: Seq[SummaryList.Row] =
        Seq(helper.whatIsThisArrangementCalledPage
          , helper.whatIsTheImplementationDatePage).flatten ++
          helper.buildReportingThisArrangement ++
          Seq(helper.whichExpectedInvolvedCountriesArrangement
          , helper.whatIsTheExpectedValueOfThisArrangement
          , helper.whichNationalProvisionsIsThisArrangementBasedOn
          , helper.giveDetailsOfThisArrangement).flatten

      renderer.render("arrangement/check-your-answers-arrangement.njk",
        Json.obj("list" -> list)
      ).map(Ok(_))
  }
}
