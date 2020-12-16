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

package utils.controllers

import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import play.api.i18n.I18nSupport
import play.api.mvc.MessagesControllerComponents
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.ExecutionContext

trait PageControllerComponents extends FrontendBaseController with I18nSupport with NunjucksSupport {

  implicit val ec: ExecutionContext
  val sessionRepository: SessionRepository
  val identify: IdentifierAction
  val getData: DataRetrievalAction
  val requireData: DataRequiredAction
  val controllerComponents: MessagesControllerComponents
  val renderer: Renderer

}
