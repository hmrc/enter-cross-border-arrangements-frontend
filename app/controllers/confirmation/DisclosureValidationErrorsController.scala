/*
 * Copyright 2023 HM Revenue & Customs
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
import org.slf4j.LoggerFactory
import pages.ValidationErrorsPage
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.Table.Cell
import uk.gov.hmrc.viewmodels._

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class DisclosureValidationErrorsController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private val logger = LoggerFactory.getLogger(getClass)

  def onPageLoad(id: Int): Action[AnyContent] = (identify andThen getData.apply() andThen requireData).async {
    implicit request =>
      val keyList: Seq[String] = request.userAnswers
        .get(ValidationErrorsPage, id)
        .filter(_.nonEmpty)
        .getOrElse(throw new IllegalStateException("Unable to retrieve validation errors."))

      val json = Json.obj(
        "id"        -> id,
        "errorRows" -> toTableRows(keyList)
      )

      renderer.render("confirmation/validationErrors.njk", json).map(Ok(_))
  }

  def toTableRows(keys: Seq[String], mapKey: String => Option[String] = keyMapper)(implicit messages: Messages): Seq[Seq[JsValue]] =
    for {
      (key, index) <- keys.zipWithIndex
      error        <- mapKey(key)
    } yield Seq(
      Json.toJson(
        Cell(
          msg"global.error.validation.section.taxpayerorreporter",
          classes = Seq("govuk-table__cell"),
          attributes = Map("id" -> s"lineNumber_$index")
        )
      ),
      Json.toJson(
        Cell(
          Html(error),
          classes = Seq("govuk-table__cell"),
          attributes = Map("id" -> s"errorMessage_$index")
        )
      )
    )

  val keyMapper: String => Option[String] =
    Option(_).map {
      case "businessrules.initialDisclosure.needRelevantTaxPayer" =>
        """As this arrangement is not marketable, it must have at least one relevant taxpayer.
          |If you are a relevant taxpayer, confirm this in your reporter’s details.
          |If you are not, add at least one relevant taxpayer.""".stripMargin
      case "businessrules.initialDisclosureMA.missingRelevantTaxPayerDates" | "businessrules.initialDisclosureMA.firstDisclosureHasInitialDisclosureMAAsTrue" =>
        """As this arrangement is marketable, all relevant taxpayers disclosed must have implementing dates."""
      case key =>
        logger.error(s"Unmapped error key: $key.")
        throw new IllegalStateException(s"Unmapped error key: $key.")
    }

}
