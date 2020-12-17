/*
 * Copyright 2020 HM Revenue & Customs
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

package controllers.enterprises

import controllers.actions._
import forms.enterprises.SelectAnyTaxpayersThisEnterpriseIsAssociatedWithFormProvider
import handlers.ErrorHandler
import javax.inject.Inject
import models.Mode
import models.taxpayer.Taxpayer
import navigation.Navigator
import pages.enterprises.SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage
import pages.taxpayer.TaxpayerLoopPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.Text.Literal
import uk.gov.hmrc.viewmodels.{Checkboxes, NunjucksSupport}

import scala.concurrent.{ExecutionContext, Future}

class SelectAnyTaxpayersThisEnterpriseIsAssociatedWithController @Inject()(
    override val messagesApi: MessagesApi,
    sessionRepository: SessionRepository,
    navigator: Navigator,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    errorHandler: ErrorHandler,
    formProvider: SelectAnyTaxpayersThisEnterpriseIsAssociatedWithFormProvider,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport {

  private val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      request.userAnswers.get(TaxpayerLoopPage) match {
        case Some(listOfTaxpayers) =>

          val preparedForm = request.userAnswers.get(SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage) match {
            case None => form
            case Some(value) => form.fill(value)
          }

          val items: Seq[Checkboxes.Checkbox] = listOfTaxpayers.map { taxpayer =>
            Checkboxes.Checkbox(label = Literal(taxpayer.nameAsString), value = s"${taxpayer.taxpayerId}")
          }
          val checkboxes = Checkboxes.set(field = preparedForm("value"), items = items)

          val json = Json.obj(
            "form" -> preparedForm,
            "mode" -> mode,
            "checkboxes" -> checkboxes
          )
          renderer.render("enterprises/selectAnyTaxpayersThisEnterpriseIsAssociatedWith.njk", json).map(Ok(_))

        case _ => errorHandler.onServerError(request, throw new Exception("Unable to retrieve List of relevant Taxpayers"))
      }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors => {

          val json = Json.obj(
            "form"       -> formWithErrors,
            "mode"       -> mode
          )

          renderer.render("enterprises/selectAnyTaxpayersThisEnterpriseIsAssociatedWith.njk", json).map(BadRequest(_))
        },
        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage, mode, updatedAnswers))
      )
  }

  def toCheckboxes(taxpayers: Set[JsObject]): Seq[Checkboxes.Checkbox] = taxpayers.map { json =>
    val name = json.validate[Taxpayer].getOrElse(throw new IllegalArgumentException("")).nameAsString
    Checkboxes.Checkbox(label = Literal(name), value = json.toString())
  }.toSeq

}