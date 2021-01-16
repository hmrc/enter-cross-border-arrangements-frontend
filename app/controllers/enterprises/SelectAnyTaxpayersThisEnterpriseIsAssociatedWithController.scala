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

package controllers.enterprises

import controllers.actions._
import controllers.mixins.{CheckRoute, RoutingSupport}
import forms.enterprises.SelectAnyTaxpayersThisEnterpriseIsAssociatedWithFormProvider
import models.{Mode, UserAnswers}
import navigation.NavigatorForEnterprises
import pages.enterprises.SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage
import pages.taxpayer.TaxpayerLoopPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.Text.Literal
import uk.gov.hmrc.viewmodels.{Checkboxes, NunjucksSupport}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SelectAnyTaxpayersThisEnterpriseIsAssociatedWithController @Inject()(
    override val messagesApi: MessagesApi,
    sessionRepository: SessionRepository,
    navigator: NavigatorForEnterprises,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    formProvider: SelectAnyTaxpayersThisEnterpriseIsAssociatedWithFormProvider,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport with RoutingSupport {

  private val form = formProvider()

  def onPageLoad(id: Int, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

          val preparedForm = request.userAnswers.get(SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage, id) match {
            case None => form
            case Some(value) => form.fill(value)
          }

          val json = Json.obj(
            "form" -> preparedForm,
            "mode" -> mode,
            "checkboxes" -> taxpayerCheckboxes(preparedForm, request.userAnswers, id)
          )
          renderer.render("enterprises/selectAnyTaxpayersThisEnterpriseIsAssociatedWith.njk", json).map(Ok(_))
  }

  def redirect(checkRoute: CheckRoute, value: Option[List[String]]): Call =
    navigator.routeMap(SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage)(checkRoute)(value)(0)

  def onSubmit(id: Int, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors => {

          val json = Json.obj(
            "form"       -> formWithErrors,
            "mode"       -> mode,
            "checkboxes" -> taxpayerCheckboxes(formWithErrors, request.userAnswers, id)
          )

          renderer.render("enterprises/selectAnyTaxpayersThisEnterpriseIsAssociatedWith.njk", json).map(BadRequest(_))
        },
        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage, id, value))
            _              <- sessionRepository.set(updatedAnswers)
            checkRoute     =  toCheckRoute(mode, updatedAnswers)
          } yield Redirect(redirect(checkRoute, Some(value)))
      )
  }

  private def taxpayerCheckboxes(form: Form[_], ua: UserAnswers, id: Int): Seq[Checkboxes.Item] = {
    ua.get(TaxpayerLoopPage, id) match {
      case Some(taxpayersList) =>
        val field = form("value")
        val items: Seq[Checkboxes.Checkbox] = taxpayersList.map { taxpayer =>
          Checkboxes.Checkbox(label = Literal(taxpayer.nameAsString), value = s"${taxpayer.taxpayerId}")
        }
        Checkboxes.set(field, items)

      case _ => throw new RuntimeException("Unable to retrieve list of Taxpayers")
    }
  }

}