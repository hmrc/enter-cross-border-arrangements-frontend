/*
 * Copyright 2022 HM Revenue & Customs
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

package controllers.individual

import controllers.actions._
import controllers.mixins.{CheckRoute, RoutingSupport}
import forms.individual.IsIndividualResidentForTaxOtherCountriesFormProvider
import helpers.JourneyHelpers.{checkLoopDetailsContainsCountry, currentIndexInsideLoop, getIndividualName}
import models.{CheckMode, LoopDetails, Mode, NormalMode}
import navigation.NavigatorForIndividual
import pages.individual.{IndividualLoopPage, IsIndividualResidentForTaxOtherCountriesPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class IsIndividualResidentForTaxOtherCountriesController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: NavigatorForIndividual,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: IsIndividualResidentForTaxOtherCountriesFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport
    with RoutingSupport {

  private val form = formProvider()

  def onPageLoad(id: Int, mode: Mode, index: Int): Action[AnyContent] = (identify andThen getData.apply() andThen requireData).async {
    implicit request =>
      checkLoopDetailsContainsCountry(request.userAnswers, id, IndividualLoopPage)

      val preparedForm = request.userAnswers.get(IndividualLoopPage, id) match {
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
        "form"           -> preparedForm,
        "id"             -> id,
        "mode"           -> mode,
        "individualName" -> getIndividualName(request.userAnswers, id),
        "radios"         -> Radios.yesNo(preparedForm("confirm")),
        "index"          -> index
      )

      renderer.render("individual/isIndividualResidentForTaxOtherCountries.njk", json).map(Ok(_))
  }

  def redirect(id: Int, checkRoute: CheckRoute, value: Option[Boolean], index: Int): Call =
    navigator.routeMap(IsIndividualResidentForTaxOtherCountriesPage)(checkRoute)(id)(value)(index)

  def onSubmit(id: Int, mode: Mode, index: Int): Action[AnyContent] = (identify andThen getData.apply() andThen requireData).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => {

            val json = Json.obj(
              "form"           -> formWithErrors,
              "id"             -> id,
              "mode"           -> mode,
              "individualName" -> getIndividualName(request.userAnswers, id),
              "radios"         -> Radios.yesNo(formWithErrors("confirm")),
              "index"          -> index
            )

            renderer.render("individual/isIndividualResidentForTaxOtherCountries.njk", json).map(BadRequest(_))
          },
          value => {

            val individualLoopList = (request.userAnswers.get(IndividualLoopPage, id), mode) match {
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
              updatedAnswers                <- Future.fromTry(request.userAnswers.set(IsIndividualResidentForTaxOtherCountriesPage, id, value))
              updatedAnswersWithLoopDetails <- Future.fromTry(updatedAnswers.set(IndividualLoopPage, id, individualLoopList))
              _                             <- sessionRepository.set(updatedAnswersWithLoopDetails)
              checkRoute = toCheckRoute(mode, updatedAnswersWithLoopDetails, id)
            } yield Redirect(redirect(id, checkRoute, Some(value), index))
          }
        )
  }
}
