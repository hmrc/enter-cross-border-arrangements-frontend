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

package controllers.disclosure

import controllers.actions._
import controllers.mixins.{CheckRoute, RoutingSupport}
import forms.RemoveDisclosureFormProvider
import javax.inject.Inject
import models.disclosure.DisclosureDetails
import models.{NormalMode, UserAnswers}
import navigation.NavigatorForDisclosure
import pages.disclosure.{DisclosureDetailsPage, RemoveDisclosurePage}
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Try}

class RemoveDisclosureController @Inject()(
    override val messagesApi: MessagesApi,
    sessionRepository: SessionRepository,
    navigator: NavigatorForDisclosure,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    formProvider: RemoveDisclosureFormProvider,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport with RoutingSupport {

  private val form = formProvider()

  def onPageLoad(id: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val preparedForm = request.userAnswers.get(RemoveDisclosurePage, id) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      val disclosureDetails : DisclosureDetails =
        request.userAnswers.get(DisclosureDetailsPage, id).getOrElse(throw new RuntimeException("Disclosure details not available"))

      val json = Json.obj(
        "form"   -> preparedForm,
        "id" -> id,
        "radios" -> Radios.yesNo(preparedForm("value")),
        "disclosureName" -> disclosureDetails.disclosureName
      )

      renderer.render("removeDisclosure.njk", json).map(Ok(_))
  }

  def redirect(checkRoute: CheckRoute, value: Option[Boolean], id: Int): Call =
    navigator.routeMap(RemoveDisclosurePage)(checkRoute)(Some(id))(value)(0)

  def onSubmit(id: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors => {

          val json = Json.obj(
            "form"   -> formWithErrors,
            "radios" -> Radios.yesNo(formWithErrors("value")),
            "id" -> id,
          )
          renderer.render("removeDisclosure.njk", json).map(BadRequest(_))
        },
        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(RemoveDisclosurePage, id, value))
            updatedUserAnswersWithFlags <- Future.fromTry(updateFlags(updatedAnswers, id))
            _              <- sessionRepository.set(updatedUserAnswersWithFlags)
            checkRoute     =  toCheckRoute(NormalMode, updatedAnswers)
          } yield Redirect(redirect(checkRoute, Some(value), id))
      )
  }

  private[controllers] def updateFlags(userAnswers: UserAnswers, id: Int): Try[UserAnswers] = {
    (userAnswers.getBase(UnsubmittedDisclosurePage) map { unsubmittedDisclosures =>
      val unsubmittedDisclosure = UnsubmittedDisclosurePage.fromIndex(id)(userAnswers)
      val updatedUnsubmittedDisclosures = unsubmittedDisclosures.zipWithIndex.filterNot { _._2 == id }.map { _._1 }
      userAnswers.setBase(UnsubmittedDisclosurePage, updatedUnsubmittedDisclosures :+ unsubmittedDisclosure.copy(deleted = true))
    }).getOrElse(Failure(new IllegalArgumentException("Unable to update deleted disclosure.")))
  }
}
