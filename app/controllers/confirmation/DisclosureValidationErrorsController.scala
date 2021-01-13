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

package controllers.confirmation

import controllers.actions._
import controllers.confirmation.DisclosureValidationErrorsController.mapErrorsToTableRows
import pages.ValidationErrorsPage
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.Table.Cell

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class DisclosureValidationErrorsController @Inject()(
    override val messagesApi: MessagesApi,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val errorList = request.userAnswers.get(ValidationErrorsPage) match {
        case Some(errors) => errors
        case _ => Seq( // TODO remove test errors and throw exception
          "As this arrangement is not marketable, it must have at least one relevant taxpayer. If you are a relevant taxpayer, confirm this in your reporter’s details. If you are not, add at least one relevant taxpayer."
          , "As this arrangement is marketable, all relevant taxpayers disclosed must have implementing dates."
        )
      }

      import DisclosureValidationErrorsController._
      val json = Json.obj(
        "errorList" -> mapErrorsToTableRows(errorList)
      )

      renderer.render("confirmation/validationErrors.njk", json).map(Ok(_))
  }
}

object DisclosureValidationErrorsController {

  import uk.gov.hmrc.viewmodels._

  def mapErrorsToTableRows(errors: Seq[String])(implicit messages: Messages) : Seq[Seq[JsValue]] = {

    for {
      error <- errors.sorted.zipWithIndex
    } yield {
      Seq(
        Json.toJson(Cell(
          msg"global.error.validation.section.taxpayerorreporter"
          , classes = Seq("govuk-table__cell")
          , attributes = Map("id" -> s"lineNumber_${error._2}")
        )),
        Json.toJson(Cell(
          Html(error._1)
          , classes = Seq("govuk-table__cell")
          , attributes = Map("id" -> s"errorMessage_${error._2}")
        ))
      )
    }
  }


}


