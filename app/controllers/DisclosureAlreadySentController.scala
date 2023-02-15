/*
 * Copyright 2023 HM Revenue & Customs
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

import config.FrontendAppConfig
import controllers.actions._
import controllers.mixins.DefaultRouting
import helpers.JourneyHelpers.linkToHomePageText
import models.NormalMode
import models.disclosure.DisclosureType
import navigation.NavigatorForConfirmation
import pages.disclosure.DisclosureDetailsPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.util.Try

class DisclosureAlreadySentController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  frontendAppConfig: FrontendAppConfig,
  navigator: NavigatorForConfirmation,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onDeleted(): Action[AnyContent] = (identify andThen getData.apply() andThen requireData).async {
    implicit request =>
      val backLinkUrl: String = navigator.routeMap(DisclosureDetailsPage)(DefaultRouting(NormalMode))(0)(Some(DisclosureType.Dac6del))(0).url

      renderer
        .render(
          "informationSent.njk",
          Json.obj(
            "homePageLink" -> linkToHomePageText(frontendAppConfig.discloseArrangeLink, "site.homePageLink.text"),
            "option"       -> "deleted",
            "backLinkUrl"  -> backLinkUrl
          )
        )
        .map(Ok(_))
  }

  def onSent(id: Int): Action[AnyContent] = (identify andThen getData.apply() andThen requireData).async {
    implicit request =>
      val disclosureType = Try(request.userAnswers.get(DisclosureDetailsPage, id).map(_.disclosureType)).getOrElse(None)

      val backLinkUrl: String = navigator.routeMap(DisclosureDetailsPage)(DefaultRouting(NormalMode))(id)(disclosureType)(0).url

      renderer
        .render(
          "informationSent.njk",
          Json.obj(
            "homePageLink" -> linkToHomePageText(frontendAppConfig.discloseArrangeLink, "site.homePageLink.text"),
            "option"       -> "sent",
            "backLinkUrl"  -> backLinkUrl
          )
        )
        .map(Ok(_))
  }
}
