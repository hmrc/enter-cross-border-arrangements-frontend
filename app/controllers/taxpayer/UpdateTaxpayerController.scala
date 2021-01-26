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

package controllers.taxpayer

import controllers.actions._
import controllers.mixins.{CheckRoute, RoutingSupport}
import forms.taxpayer.UpdateTaxpayerFormProvider
import javax.inject.Inject
import models.disclosure.DisclosureType
import models.disclosure.DisclosureType.{Dac6add, Dac6new}
import models.hallmarks.JourneyStatus
import models.reporter.RoleInArrangement.{Intermediary, Taxpayer}
import models.taxpayer.UpdateTaxpayer
import models.{Mode, UserAnswers}
import navigation.NavigatorForTaxpayer
import pages.disclosure.DisclosureDetailsPage
import pages.reporter.RoleInArrangementPage
import pages.taxpayer.{RelevantTaxpayerStatusPage, TaxpayerLoopPage, UpdateTaxpayerPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.{ExecutionContext, Future}

class UpdateTaxpayerController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: NavigatorForTaxpayer,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: UpdateTaxpayerFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport  with RoutingSupport {

  private val form = formProvider()

  def onPageLoad(id: Int, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val namesOfTaxpayers: IndexedSeq[String] = request.userAnswers.get(TaxpayerLoopPage, id) match {
        case Some(list) =>
          for {
            taxpayer <- list
          } yield {
            taxpayer.nameAsString
          }
        case None => IndexedSeq.empty
      }

      val preparedForm =  request.userAnswers.get(UpdateTaxpayerPage, id) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      val json = Json.obj(
        "form"   -> preparedForm,
        "taxpayerList" -> namesOfTaxpayers,
        "id" -> id,
        "mode"   -> mode,
        "radios"  -> UpdateTaxpayer.radios(preparedForm)
      )

      renderer.render("taxpayer/updateTaxpayer.njk", json).map(Ok(_))
  }

  def redirect(id: Int, checkRoute: CheckRoute, value: Option[UpdateTaxpayer]): Call =
    navigator.routeMap(UpdateTaxpayerPage)(checkRoute)(id)(value)(0)

  def onSubmit(id: Int, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors => {

          val json = Json.obj(
            "form"   -> formWithErrors,
            "id" -> id,
            "mode"   -> mode,
            "radios" -> UpdateTaxpayer.radios(formWithErrors)
          )

          renderer.render("taxpayer/updateTaxpayer.njk", json).map(BadRequest(_))
        },
        (value: UpdateTaxpayer) => {

          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(UpdateTaxpayerPage, id, value))
//            cleanAnswers   <- Future.fromTry(updatedAnswers.remove(WhatIsTaxpayersStartDateForImplementingArrangementPage, id)) // TODO test when userAnswers are properly supplied
            updatedAnswersWithStatus <- Future.fromTry(updatedAnswers.set(RelevantTaxpayerStatusPage, id, setStatus(value, updatedAnswers, id)))
            _              <- sessionRepository.set(updatedAnswersWithStatus)
            checkRoute     =  toCheckRoute(mode, updatedAnswersWithStatus, id)
           } yield Redirect(redirect(id, checkRoute, Some(value)))
        }
      )
  }

  private def setStatus(selectedAnswer: UpdateTaxpayer, ua: UserAnswers, id: Int): JourneyStatus = {
    selectedAnswer match {
      case UpdateTaxpayer.Later => JourneyStatus.InProgress
      case UpdateTaxpayer.No => checkStatusConditions(ua, id)
      case _ => JourneyStatus.NotStarted
    }
  }

  private def checkStatusConditions(ua: UserAnswers, id: Int): JourneyStatus = {

    val oneRelevantTaxpayerAdded: Boolean = ua.get(TaxpayerLoopPage, id).exists(list => list.nonEmpty)
    val getMarketableFlag: Boolean = ua.get(DisclosureDetailsPage, id).exists(_.initialDisclosureMA)
    val getDisclosureType: Option[DisclosureType] = ua.get(DisclosureDetailsPage, id).map(_.disclosureType)

    (getDisclosureType, getMarketableFlag, ua.get(RoleInArrangementPage, id)) match {

        case (Some(Dac6new), true, _) => JourneyStatus.Completed //new & marketable

        case (Some(Dac6new), false, Some(Taxpayer)) => JourneyStatus.Completed //new & non marketable & Reporter is Taxpayer

        case (Some(Dac6add), _, Some(Taxpayer)) => JourneyStatus.Completed // add & Reporter is taxpayer

        case (_, false, Some(Intermediary)) if oneRelevantTaxpayerAdded => JourneyStatus.Completed  //non marketable & Reporter is Intermediary but has added a taxpayer

        case _ => JourneyStatus.NotStarted
    }
  }
}
