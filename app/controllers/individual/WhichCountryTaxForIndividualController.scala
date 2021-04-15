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

package controllers.individual

import controllers.actions._
import controllers.mixins.{CheckRoute, CountrySupport, RoutingSupport}
import forms.individual.WhichCountryTaxForIndividualFormProvider
import models.{Country, LoopDetails, Mode, UserAnswers}
import navigation.NavigatorForIndividual
import pages.individual.{IndividualLoopPage, IndividualNamePage, WhichCountryTaxForIndividualPage}
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

class WhichCountryTaxForIndividualController @Inject()(
    override val messagesApi: MessagesApi,
    countryListFactory: CountryListFactory,
    sessionRepository: SessionRepository,
    navigator: NavigatorForIndividual,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    formProvider: WhichCountryTaxForIndividualFormProvider,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport with RoutingSupport with CountrySupport {

  val countries: Seq[Country] = countryListFactory.getCountryList().getOrElse(throw new Exception("Cannot retrieve country list"))

  private val form = formProvider(countries)

  def onPageLoad(id: Int, mode: Mode, index: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val preparedForm = getCountry(request.userAnswers, id, IndividualLoopPage, index) match {
        case Some(value) => form.fill(value)
        case _ => form
      }

      val json = Json.obj(
        "form" -> preparedForm,
        "id" -> id,
        "mode" -> mode,
        "name" -> getIndividualName(request.userAnswers, id),
        "countries" -> countryJsonList(preparedForm.data, countries),
        "index" -> index,
        "pageTitle"   -> "whichCountryTaxForIndividual.title",
        "pageHeading" -> "whichCountryTaxForIndividual.heading",
        "dynamicAlso" -> dynamicAlso(index),
        "guidance"    -> dynamicGuidance(index)
      )

      renderer.render("individual/whichCountryTaxForIndividual.njk", json).map(Ok(_))
  }

  def redirect(id: Int, checkRoute: CheckRoute, value: Option[Country], index: Int): Call =
    navigator.routeMap(WhichCountryTaxForIndividualPage)(checkRoute)(id)(value)(index)

  def onSubmit(id: Int, mode: Mode, index: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors => {

          val json = Json.obj(
            "form" -> formWithErrors,
            "id" -> id,
            "mode" -> mode,
            "name" -> getIndividualName(request.userAnswers, id),
            "countries" -> countryJsonList(formWithErrors.data, countries),
            "index" -> index,
            "pageTitle"   -> "whichCountryTaxForIndividual.title",
            "pageHeading" -> "whichCountryTaxForIndividual.heading",
            "dynamicAlso" -> dynamicAlso(index),
            "guidance"    -> dynamicGuidance(index)
          )

          renderer.render("individual/whichCountryTaxForIndividual.njk", json).map(BadRequest(_))
        },
        value => {

          val individualLoopDetails = getIndividualLoopDetails(value, request.userAnswers, id, index)

          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(WhichCountryTaxForIndividualPage, id, value))
            updatedAnswersWithLoopDetails <- Future.fromTry(updatedAnswers.set(IndividualLoopPage, id, individualLoopDetails))
            _                             <- sessionRepository.set(updatedAnswersWithLoopDetails)
            checkRoute                    =  toCheckRoute(mode, updatedAnswersWithLoopDetails, id)
          } yield Redirect(redirect(id, checkRoute, Some(value), index))
        }
      )
  }

  private def getIndividualName(userAnswers: UserAnswers, id: Int): String = {
    userAnswers.get(IndividualNamePage, id) match {
      case Some(name) => s"${"is " + name.firstName + " " + name.secondName}"
      case None => "are they"
    }
  }

  private def dynamicGuidance(index: Int): String =
    if (index >= 1) "whichCountryTaxForIndividual.moreThanOne.hint" else "whichCountryTaxForIndividual.hint"

  private def dynamicAlso(index: Int): String =
    if (index >= 1) "also" else ""

  def getIndividualLoopDetails(value: Country, userAnswers: UserAnswers, id: Int,  index: Int): IndexedSeq[LoopDetails] =
      userAnswers.get(IndividualLoopPage, id) match {
    case None =>
      val newIndividualLoop = LoopDetails(None, whichCountry = Some(value), None, None, None, None)
      IndexedSeq(newIndividualLoop)
    case Some(list) =>
      if (list.lift(index).isDefined) {
        //Update value
        val updatedLoop = list.lift(index).get.copy(whichCountry = Some(value))
        list.updated(index, updatedLoop)
      } else {
        //Add to loop
        val newIndividualLoop = LoopDetails(None, whichCountry = Some(value), None, None, None, None)
        list :+ newIndividualLoop
      }
  }
}
