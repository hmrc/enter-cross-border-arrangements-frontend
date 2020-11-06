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
import forms.DoYouKnowTINForNonUKOrganisationFormProvider
import helpers.JourneyHelpers.{currentIndexInsideLoop, getOrganisationName}
import javax.inject.Inject
import models.{Mode, OrganisationLoopDetails, UserAnswers}
import navigation.Navigator
import pages.{DoYouKnowTINForNonUKOrganisationPage, OrganisationLoopPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Request}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}

import scala.concurrent.{ExecutionContext, Future}

class DoYouKnowTINForNonUKOrganisationController @Inject()(
    override val messagesApi: MessagesApi,
    sessionRepository: SessionRepository,
    navigator: Navigator,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    formProvider: DoYouKnowTINForNonUKOrganisationFormProvider,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport {

  def onPageLoad(mode: Mode, index: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val country = getCountry(request.userAnswers)
      val form = formProvider(country)

      val preparedForm = request.userAnswers.get(OrganisationLoopPage) match {
        case None => form
        case Some(value) if value.lift(index).isDefined =>
          val doYouKnowTIN = value.lift(index).get.doYouKnowTIN
          if (doYouKnowTIN.isDefined) {
            form.fill(doYouKnowTIN.get)
          } else {
            form
          }
        case Some(_) => form
      }

      val json = Json.obj(
        "form"   -> preparedForm,
        "mode"   -> mode,
        "radios" -> Radios.yesNo(preparedForm("confirm")),
        "organisationName" -> getOrganisationName(request.userAnswers),
        "country" -> country,
        "index" -> index
      )

      renderer.render("doYouKnowTINForNonUKOrganisation.njk", json).map(Ok(_))
  }

  def onSubmit(mode: Mode, index: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val country = getCountry(request.userAnswers)
      val form = formProvider(country)

      form.bindFromRequest().fold(
        formWithErrors => {

          val json = Json.obj(
            "form"   -> formWithErrors,
            "mode"   -> mode,
            "radios" -> Radios.yesNo(formWithErrors("confirm")),
            "organisationName" -> getOrganisationName(request.userAnswers),
            "country" -> country,
            "index" -> index
          )

          renderer.render("doYouKnowTINForNonUKOrganisation.njk", json).map(BadRequest(_))
        },
        value => {
          val organisationLoopList = request.userAnswers.get(OrganisationLoopPage) match {
            case None =>
              val newOrganisationLoop = OrganisationLoopDetails(None, None, doYouKnowTIN = Some(value), None)
              IndexedSeq[OrganisationLoopDetails](newOrganisationLoop)
            case Some(list) =>
              if (list.lift(index).isDefined) {
                val updatedLoop = list.lift(index).get.copy(doYouKnowTIN = Some(value))
                list.updated(index, updatedLoop)
              } else {
                list
              }
          }

          for {
            updatedAnswers                <- Future.fromTry(request.userAnswers.set(DoYouKnowTINForNonUKOrganisationPage, value))
            updatedAnswersWithLoopDetails <- Future.fromTry(updatedAnswers.set(OrganisationLoopPage, organisationLoopList))
            _                             <- sessionRepository.set(updatedAnswersWithLoopDetails)
          } yield Redirect(navigator.nextPage(DoYouKnowTINForNonUKOrganisationPage, mode, updatedAnswersWithLoopDetails))
        }
      )
  }

  private def getCountry(userAnswers: UserAnswers)(implicit request: Request[AnyContent]): String = {
    userAnswers.get(OrganisationLoopPage) match {
      case Some(loopDetailsSeq) =>
        val whichCountry = loopDetailsSeq(currentIndexInsideLoop(request)).whichCountry
        if (whichCountry.isDefined) {
          whichCountry.get.description
        } else {
          "the country"
        }
      case None => "the country"
    }
  }
}