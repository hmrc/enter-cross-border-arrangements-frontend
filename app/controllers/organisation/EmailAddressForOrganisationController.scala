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

package controllers.organisation

import controllers.actions._
import forms.organisation.EmailAddressForOrganisationFormProvider
import helpers.JourneyHelpers.getOrganisationName
import models.{Mode, UserAnswers}
import navigation.NavigatorForOrganisation
import pages.organisation.EmailAddressForOrganisationPage
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{Call, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import utils.controllers.OnSubmitMixIn

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class EmailAddressForOrganisationController @Inject()(
    override val messagesApi: MessagesApi,
    val sessionRepository: SessionRepository,
    val identify: IdentifierAction,
    val getData: DataRetrievalAction,
    val requireData: DataRequiredAction,
    formProvider: EmailAddressForOrganisationFormProvider,
    val controllerComponents: MessagesControllerComponents,
    val renderer: Renderer
)(implicit val ec: ExecutionContext) extends OnSubmitMixIn[String] {

  val template: String = "organisation/emailAddressForOrganisation.njk"

  val form: Form[String] = formProvider()

  override def pageData(form: Form[String], userAnswers: Option[UserAnswers]): JsObject = Json.obj(
    "organisationName" -> getOrganisationName(userAnswers.get)
  )

  val getPage = ua => ua.get(EmailAddressForOrganisationPage)

  val setPage = ua => value => ua.set(EmailAddressForOrganisationPage, value)

  override def redirect(mode: Mode, value: Option[String], index: Int, alternative: Boolean): Call = {
    NavigatorForOrganisation.nextPage(EmailAddressForOrganisationPage, mode, value, 0, alternative)
  }

}
