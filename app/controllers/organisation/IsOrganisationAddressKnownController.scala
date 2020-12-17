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

package controllers.organisation

import controllers.actions._
import forms.organisation.IsOrganisationAddressKnownFormProvider
import helpers.JourneyHelpers.getOrganisationName
import models.{CheckMode, Mode}
import navigation.NavigatorForOrganisation
import pages.organisation.IsOrganisationAddressKnownPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class IsOrganisationAddressKnownController @Inject()(
    override val messagesApi: MessagesApi,
    sessionRepository: SessionRepository,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    formProvider: IsOrganisationAddressKnownFormProvider,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport {

  private val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val preparedForm = request.userAnswers.get(IsOrganisationAddressKnownPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      val json = Json.obj(
        "form"   -> preparedForm,
        "mode"   -> mode,
        "radios" -> Radios.yesNo(preparedForm("value")),
        "organisationName" -> getOrganisationName(request.userAnswers)
      )

      renderer.render("organisation/isOrganisationAddressKnown.njk", json).map(Ok(_))
  }

  def redirect(mode: Mode, value: Option[Boolean], index: Int = 0, alternative: Boolean = false): Call =
    NavigatorForOrganisation.nextPage(IsOrganisationAddressKnownPage, mode, value, index, alternative)

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      form.bindFromRequest().fold(
        formWithErrors => {

          val json = Json.obj(
            "form"   -> formWithErrors,
            "mode"   -> mode,
            "radios" -> Radios.yesNo(formWithErrors("value")),
            "organisationName" -> getOrganisationName(request.userAnswers)
          )

          renderer.render("organisation/isOrganisationAddressKnown.njk", json).map(BadRequest(_))
        },
        value => {

          val determineRoute = (value, mode) match {
            case (false, CheckMode) => true
            case  _ => false
          }

          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(IsOrganisationAddressKnownPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(redirect(mode, Some(value), 0, determineRoute))
        }
      )
  }
}
