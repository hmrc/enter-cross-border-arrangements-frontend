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

import config.FrontendAppConfig
import controllers.actions._
import controllers.mixins.{CheckRoute, RoutingSupport}
import forms.enterprises.YouHaveNotAddedAnyAssociatedEnterprisesFormProvider
import models.enterprises.YouHaveNotAddedAnyAssociatedEnterprises
import models.hallmarks.JourneyStatus
import models.{ItemList, Mode, UserAnswers}
import navigation.NavigatorForEnterprises
import pages.enterprises.{AssociatedEnterpriseLoopPage, AssociatedEnterpriseStatusPage, YouHaveNotAddedAnyAssociatedEnterprisesPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class YouHaveNotAddedAnyAssociatedEnterprisesController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: NavigatorForEnterprises,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: YouHaveNotAddedAnyAssociatedEnterprisesFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer,
  frontendAppConfig: FrontendAppConfig
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport
    with RoutingSupport {

  private val form = formProvider()

  def onPageLoad(id: Int, mode: Mode): Action[AnyContent] = (identify andThen getData.apply() andThen requireData).async {
    implicit request =>
      val preparedForm = request.userAnswers.get(YouHaveNotAddedAnyAssociatedEnterprisesPage, id) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      val json = Json.obj(
        "form"                     -> preparedForm,
        "id"                       -> id,
        "mode"                     -> mode,
        "radios"                   -> YouHaveNotAddedAnyAssociatedEnterprises.radios(preparedForm),
        "associatedEnterpriseList" -> Json.toJson(toItemList(request.userAnswers, id))
      )

      renderer.render("enterprises/youHaveNotAddedAnyAssociatedEnterprises.njk", json).map(Ok(_))
  }

  def redirect(id: Int, checkRoute: CheckRoute, value: Option[YouHaveNotAddedAnyAssociatedEnterprises]): Call =
    navigator.routeMap(YouHaveNotAddedAnyAssociatedEnterprisesPage)(checkRoute)(id)(value)(0)

  private[enterprises] def toItemList(userAnswers: UserAnswers, id: Int): IndexedSeq[ItemList] = userAnswers.get(AssociatedEnterpriseLoopPage, id) match {
    case Some(list) =>
      for {
        enterprise <- list
      } yield {
        val changeUrl = routes.AssociatedEnterpriseCheckYourAnswersController.onPageLoad(id, Option(enterprise.enterpriseId)).url
        val removeUrl = routes.AreYouSureYouWantToRemoveEnterpriseController.onPageLoad(id, enterprise.enterpriseId).url
        ItemList(enterprise.nameAsString, changeUrl, removeUrl)
      }
    case None => IndexedSeq.empty
  }

  def onSubmit(id: Int, mode: Mode): Action[AnyContent] = (identify andThen getData.apply() andThen requireData).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => {

            val json = Json.obj(
              "form"                     -> formWithErrors,
              "id"                       -> id,
              "mode"                     -> mode,
              "radios"                   -> YouHaveNotAddedAnyAssociatedEnterprises.radios(formWithErrors),
              "associatedEnterpriseList" -> Json.toJson(toItemList(request.userAnswers, id))
            )

            renderer.render("enterprises/youHaveNotAddedAnyAssociatedEnterprises.njk", json).map(BadRequest(_))
          },
          value =>
            for {
              updatedAnswers           <- Future.fromTry(request.userAnswers.set(YouHaveNotAddedAnyAssociatedEnterprisesPage, id, value))
              updatedAnswersWithStatus <- Future.fromTry(updatedAnswers.set(AssociatedEnterpriseStatusPage, id, setStatus(value)))
              _                        <- sessionRepository.set(updatedAnswersWithStatus)
              checkRoute = toCheckRoute(mode, updatedAnswersWithStatus, id)
            } yield Redirect(redirect(id, checkRoute, Some(value)))
        )
  }

  private def setStatus(selectedAnswer: YouHaveNotAddedAnyAssociatedEnterprises): JourneyStatus =
    selectedAnswer match {
      case YouHaveNotAddedAnyAssociatedEnterprises.YesAddLater => JourneyStatus.InProgress
      case YouHaveNotAddedAnyAssociatedEnterprises.No          => JourneyStatus.Completed
      case _                                                   => JourneyStatus.NotStarted
    }

}
