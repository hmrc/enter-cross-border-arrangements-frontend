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

import config.FrontendAppConfig
import controllers.actions.{DataRetrievalAction, IdentifierAction}
import models.{NormalMode, UnsubmittedDisclosure}
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import javax.inject.Inject

import scala.concurrent.{ExecutionContext, Future}

class UnsubmittedDisclosureController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer,
  appConfig: FrontendAppConfig
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def removeFromList(zipped: (UnsubmittedDisclosure, Int)): Boolean =
    zipped._1.deleted || zipped._1.submitted

  def onPageLoad: Action[AnyContent] = (identify andThen getData.apply()).async {
    implicit request =>
      val disclosureNameUrl = controllers.disclosure.routes.DisclosureNameController.onPageLoad(NormalMode).url

      val unsubmittedDisclosuresWithIndex: Option[Seq[(UnsubmittedDisclosure, Int)]] = for {
        userAnswers            <- request.userAnswers
        unsubmittedDisclosures <- userAnswers.getBase(UnsubmittedDisclosurePage)
      } yield unsubmittedDisclosures.zipWithIndex.filterNot(removeFromList)

      unsubmittedDisclosuresWithIndex match {

        case Some(list) if list.nonEmpty =>
          val json = Json.obj(
            "url" -> disclosureNameUrl,
            "unsubmittedDisclosures" -> list.map {
              case (unsubmittedDisclosure, id) =>
                Json.obj(
                  "name"      -> unsubmittedDisclosure.name,
                  "changeUrl" -> controllers.routes.DisclosureDetailsController.onPageLoad(id).url,
                  "removeUrl" -> controllers.disclosure.routes.RemoveDisclosureController.onPageLoad(id).url
                )
            },
            "plural" -> (if (list.length > 1) "s" else "")
          )

          renderer.render("unsubmitted/unsubmitted.njk", json).map(Ok(_))

        case _ => Future.successful(Redirect(appConfig.discloseArrangeLink))
      }
  }

}
