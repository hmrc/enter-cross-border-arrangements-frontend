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
import forms.enterprises.YouHaveNotAddedAnyAssociatedEnterprisesFormProvider
import models.Mode
import models.enterprises.YouHaveNotAddedAnyAssociatedEnterprises
import navigation.NavigatorForEnterprises
import pages.enterprises.{AssociatedEnterpriseLoopPage, YouHaveNotAddedAnyAssociatedEnterprisesPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class YouHaveNotAddedAnyAssociatedEnterprisesController @Inject()(
    override val messagesApi: MessagesApi,
    sessionRepository: SessionRepository,
    navigator: NavigatorForEnterprises,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    formProvider: YouHaveNotAddedAnyAssociatedEnterprisesFormProvider,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport with RoutingSupport {

  private val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val namesOfAssociatedEnterprises: IndexedSeq[String] = request.userAnswers.get(AssociatedEnterpriseLoopPage) match {
        case Some(list) =>
          list.map(_.nameAsString)
        case None => IndexedSeq.empty
      }

      val preparedForm = request.userAnswers.get(YouHaveNotAddedAnyAssociatedEnterprisesPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      val json = Json.obj(
        "form"   -> preparedForm,
        "mode"   -> mode,
        "radios"  -> YouHaveNotAddedAnyAssociatedEnterprises.radios(preparedForm),
        "associatedEnterpriseList" -> namesOfAssociatedEnterprises
      )

      renderer.render("enterprises/youHaveNotAddedAnyAssociatedEnterprises.njk", json).map(Ok(_))
  }

  def redirect(checkRoute: CheckRoute, value: Option[YouHaveNotAddedAnyAssociatedEnterprises]): Call =
    navigator.routeMap(YouHaveNotAddedAnyAssociatedEnterprisesPage)(checkRoute)(value)(0)

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors => {

          val namesOfAssociatedEnterprises: IndexedSeq[String] = request.userAnswers.get(AssociatedEnterpriseLoopPage) match {
            case Some(list) =>
              list.map(_.nameAsString)
            case None => IndexedSeq.empty
          }

          val json = Json.obj(
            "form"   -> formWithErrors,
            "mode"   -> mode,
            "radios" -> YouHaveNotAddedAnyAssociatedEnterprises.radios(formWithErrors),
            "associatedEnterpriseList" -> namesOfAssociatedEnterprises
          )

          renderer.render("enterprises/youHaveNotAddedAnyAssociatedEnterprises.njk", json).map(BadRequest(_))
        },
        value => {

          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(YouHaveNotAddedAnyAssociatedEnterprisesPage, value))
            _              <- sessionRepository.set(updatedAnswers)
            checkRoute     =  toCheckRoute(mode, updatedAnswers)
          } yield Redirect(redirect(checkRoute, Some(value)))
        }
      )
  }
}
