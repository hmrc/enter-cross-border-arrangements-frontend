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

import config.FrontendAppConfig
import controllers.actions._
import controllers.mixins.{CheckRoute, RoutingSupport}
import forms.taxpayer.UpdateTaxpayerFormProvider
import helpers.StatusHelper.checkTaxpayerStatusConditions
import models.hallmarks.JourneyStatus
import models.taxpayer.UpdateTaxpayer
import models.{ItemList, Mode, UserAnswers}
import navigation.NavigatorForTaxpayer
import pages.taxpayer.{RelevantTaxpayerStatusPage, TaxpayerLoopPage, UpdateTaxpayerPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport

import javax.inject.Inject
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
  renderer: Renderer,
  frontendAppConfig: FrontendAppConfig
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport  with RoutingSupport {

  private val form = formProvider()

  def onPageLoad(id: Int, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val preparedForm =  request.userAnswers.get(UpdateTaxpayerPage, id) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      val json = Json.obj(
        "form"   -> preparedForm,
        "taxpayerList" -> Json.toJson(toItemList(request.userAnswers, id)),
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
            "taxpayerList" -> Json.toJson(toItemList(request.userAnswers, id)),
            "mode"   -> mode,
            "radios" -> UpdateTaxpayer.radios(formWithErrors)
          )

          renderer.render("taxpayer/updateTaxpayer.njk", json).map(BadRequest(_))
        },
        (value: UpdateTaxpayer) => {

          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(UpdateTaxpayerPage, id, value))
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
      case UpdateTaxpayer.No => checkTaxpayerStatusConditions(ua, id)
      case _ => JourneyStatus.NotStarted
    }
  }

  private[taxpayer] def toItemList(ua: UserAnswers, id: Int) = ua.get(TaxpayerLoopPage, id) match {

    case Some(list) =>
      for {
        taxpayer <- list
      } yield {
        val changeUrl = if (frontendAppConfig.changeLinkToggle) routes.TaxpayersCheckYourAnswersController.onPageLoad(id, Option(taxpayer.taxpayerId)).url else "#"
        val removeUrl = routes.RemoveTaxpayerController.onPageLoad(id, taxpayer.taxpayerId).url
        ItemList(taxpayer.nameAsString, changeUrl, removeUrl)
      }
    case None => IndexedSeq.empty
  }
}
