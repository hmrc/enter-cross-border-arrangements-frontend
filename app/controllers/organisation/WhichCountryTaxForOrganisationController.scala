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
import forms.organisation.WhichCountryTaxForOrganisationFormProvider
import helpers.JourneyHelpers.{countryJsonList, currentIndexInsideLoop, getOrganisationName}
import models.{Country, LoopDetails, Mode}
import navigation.NavigatorForOrganisation
import pages.organisation.{OrganisationLoopPage, WhichCountryTaxForOrganisationPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport
import utils.CountryListFactory

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class WhichCountryTaxForOrganisationController @Inject()(
    override val messagesApi: MessagesApi,
    countryListFactory: CountryListFactory,
    sessionRepository: SessionRepository,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    formProvider: WhichCountryTaxForOrganisationFormProvider,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport {

  val countries: Seq[Country] = countryListFactory.getCountryList().getOrElse(throw new Exception("Cannot retrieve country list"))
  private val form = formProvider(countries)

  def onPageLoad(mode: Mode, index: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val preparedForm = request.userAnswers.get(OrganisationLoopPage) match {
        case None => form
        case Some(value) if value.lift(index).isDefined =>
          val country = value.lift(index).get.whichCountry
          if (country.isDefined) {
            form.fill(country.get)
          } else {
            form
          }
        case Some(_) => form
      }

      val json = Json.obj(
        "form" -> preparedForm,
        "mode" -> mode,
        "organisationName" -> getOrganisationName(request.userAnswers),
        "countries" -> countryJsonList(preparedForm.data, countries),
         "index" -> index
      )

      renderer.render("organisation/whichCountryTaxForOrganisation.njk", json).map(Ok(_))
  }

  def redirect(mode: Mode, value: Option[Country], index: Int = 0, alternative: Boolean = false): Call =
    NavigatorForOrganisation.nextPage(WhichCountryTaxForOrganisationPage, mode, value, index, alternative)

  def onSubmit(mode: Mode, index: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors => {

          val json = Json.obj(
            "form" -> formWithErrors,
            "mode" -> mode,
            "organisationName" -> getOrganisationName(request.userAnswers),
            "countries" -> countryJsonList(formWithErrors.data, countries),
            "index" -> index
          )

          renderer.render("organisation/whichCountryTaxForOrganisation.njk", json).map(BadRequest(_))
        },
        value => {
          val organisationLoopList = request.userAnswers.get(OrganisationLoopPage) match {
            case None =>
              val newOrganisationLoop = LoopDetails(None, whichCountry = Some(value), None, None, None, None)
              IndexedSeq(newOrganisationLoop)
            case Some(list) =>
              if (list.lift(index).isDefined) {
                //Update value
                val updatedLoop = list.lift(index).get.copy(whichCountry = Some(value))
                list.updated(index, updatedLoop)
              } else {
                //Add to loop
                val newOrganisationLoop = LoopDetails(None, whichCountry = Some(value), None, None, None, None)
                list :+ newOrganisationLoop
              }
          }

          for {
            updatedAnswers                <- Future.fromTry(request.userAnswers.set(WhichCountryTaxForOrganisationPage, value))
            updatedAnswersWithLoopDetails <- Future.fromTry(updatedAnswers.set(OrganisationLoopPage, organisationLoopList))
            _                             <- sessionRepository.set(updatedAnswersWithLoopDetails)
          } yield Redirect(redirect(mode, Some(value), currentIndexInsideLoop(request)))
        }
      )
  }
}
