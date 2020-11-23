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

package controllers

import controllers.actions._
import forms.IsOrganisationResidentForTaxOtherCountriesFormProvider
import helpers.JourneyHelpers._
import javax.inject.Inject
import models.{CheckMode, Mode, NormalMode, LoopDetails}
import navigation.Navigator
import pages.{IsOrganisationResidentForTaxOtherCountriesPage, OrganisationLoopPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}

import scala.concurrent.{ExecutionContext, Future}

class IsOrganisationResidentForTaxOtherCountriesController @Inject()(
    override val messagesApi: MessagesApi,
    sessionRepository: SessionRepository,
    navigator: Navigator,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    formProvider: IsOrganisationResidentForTaxOtherCountriesFormProvider,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport {

  private val form = formProvider()

  def onPageLoad(mode: Mode, index: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val preparedForm = request.userAnswers.get(OrganisationLoopPage) match {
        case None => form
        case Some(value) if value.lift(index).isDefined =>
          val taxResidentOtherCountries = value.lift(index).get.taxResidentOtherCountries
          if (taxResidentOtherCountries.isDefined) {
            form.fill(taxResidentOtherCountries.get)
          } else {
            form
          }
        case Some(_) => form
      }

      val json = Json.obj(
        "form"   -> preparedForm,
        "mode"   -> mode,
        "organisationName" -> getOrganisationName(request.userAnswers),
        "radios" -> Radios.yesNo(preparedForm("confirm")),
        "index" -> index
      )

      renderer.render("isOrganisationResidentForTaxOtherCountries.njk", json).map(Ok(_))
  }

  def onSubmit(mode: Mode, index: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors => {

          val json = Json.obj(
            "form"   -> formWithErrors,
            "mode"   -> mode,
            "organisationName" -> getOrganisationName(request.userAnswers),
            "radios" -> Radios.yesNo(formWithErrors("confirm")),
            "index" -> index
          )

          renderer.render("isOrganisationResidentForTaxOtherCountries.njk", json).map(BadRequest(_))
        },
        value => {
          val organisationLoopList = (request.userAnswers.get(OrganisationLoopPage), mode) match {
            case (Some(list), NormalMode) => // Add to Loop in NormalMode
                list :+ LoopDetails(taxResidentOtherCountries = Some(value), None, None, None, None, None)
            case (Some(list), CheckMode) =>
                if (value.equals(false)) {
                  list.slice(0, currentIndexInsideLoop(request)) // Remove from loop in CheckMode
                } else {
                  list :+ LoopDetails(taxResidentOtherCountries = Some(value), None, None, None, None, None) // Add to loop in CheckMode
                }
            case (None, _) => // Start new Loop in Normal Mode
              IndexedSeq[LoopDetails](LoopDetails(taxResidentOtherCountries = Some(value), None, None, None, None, None))
          }

          for {
            updatedAnswers                <- Future.fromTry(request.userAnswers.set(IsOrganisationResidentForTaxOtherCountriesPage, value))
            updatedAnswersWithLoopDetails <- Future.fromTry(updatedAnswers.set(OrganisationLoopPage, organisationLoopList))
            _                             <- sessionRepository.set(updatedAnswersWithLoopDetails)
          } yield Redirect(navigator.nextPage(IsOrganisationResidentForTaxOtherCountriesPage, mode, updatedAnswersWithLoopDetails))
        }
      )
  }
}
