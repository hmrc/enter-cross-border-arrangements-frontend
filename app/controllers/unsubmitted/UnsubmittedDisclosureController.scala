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

package controllers.unsubmitted

import controllers.actions.{DataRetrievalAction, IdentifierAction}
import models.NormalMode
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UnsubmittedDisclosureController  @Inject()(
                                                  override val messagesApi: MessagesApi,
                                                  identify: IdentifierAction,
                                                  getData: DataRetrievalAction,
                                                  val controllerComponents: MessagesControllerComponents,
                                                  renderer: Renderer
                                                )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = (identify andThen getData).async {
    implicit request =>

      val disclosureNameUrl = controllers.disclosure.routes.DisclosureNameController.onPageLoad(NormalMode).url

      request.userAnswers.flatMap(_.get(UnsubmittedDisclosurePage)) match {
        case Some(unsubmittedDisclosures) if unsubmittedDisclosures.nonEmpty =>

          val json = Json.obj(
            "url" -> disclosureNameUrl,
            "unsubmittedDisclosures" -> unsubmittedDisclosures,
            "plural" -> (if(unsubmittedDisclosures.length > 1) "s" else "")
          )

          renderer.render("unsubmitted/unsubmitted.njk", json).map(Ok(_))

        case _ =>  Future.successful(Redirect(disclosureNameUrl))
      }
  }

}
