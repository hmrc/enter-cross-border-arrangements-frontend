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
import forms.organisation.DoYouKnowAnyTINForUKOrganisationFormProvider
import helpers.JourneyHelpers.getOrganisationName
import models.{LoopDetails, Mode, UserAnswers}
import navigation.NavigatorForOrganisation
import pages.organisation.{DoYouKnowAnyTINForUKOrganisationPage, OrganisationLoopPage}
import play.api.data.Form
import play.api.i18n.{Messages, MessagesApi}
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{Call, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.viewmodels.Radios
import utils.controllers.OnSubmitIndexMixIn

import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.util.Try

class DoYouKnowAnyTINForUKOrganisationController @Inject()(
                                                            override val messagesApi: MessagesApi,
                                                            val sessionRepository: SessionRepository,
                                                            val identify: IdentifierAction,
                                                            val getData: DataRetrievalAction,
                                                            val requireData: DataRequiredAction,
                                                            formProvider: DoYouKnowAnyTINForUKOrganisationFormProvider,
                                                            val controllerComponents: MessagesControllerComponents,
                                                            val renderer: Renderer
)(implicit val ec: ExecutionContext) extends OnSubmitIndexMixIn[Boolean, LoopDetails] {

  val template: String = "organisation/doYouKnowAnyTINForUKOrganisation.njk"

  val setPage = ua => value => ua.set(DoYouKnowAnyTINForUKOrganisationPage, value)

  val form = formProvider()

  val getLoopPage: UserAnswers => Option[IndexedSeq[LoopDetails]] = _.get(OrganisationLoopPage)

  val setLoopPage: UserAnswers => IndexedSeq[LoopDetails] => Try[UserAnswers] = ua => loopDetails => ??? //getLoopPage(ua).copy(doYouKnowUTR = )

  override val toValue: LoopDetails => Boolean = _.doYouKnowTIN.getOrElse(false)

  def pageData(form: Form[Boolean], userAnswers: Option[UserAnswers])(implicit messages: Messages): JsObject = Json.obj(
      "radios" -> Radios.yesNo(form("confirm")),
      "organisationName" -> getOrganisationName(userAnswers.get)
    )

  def redirect(mode: Mode, value: Option[Boolean], index: Int = 0, alternative: Boolean = false): Call =
    NavigatorForOrganisation.nextPage(DoYouKnowAnyTINForUKOrganisationPage, mode, value, index)

  val toDetail = value => LoopDetails(None, None, None, None, doYouKnowUTR = Some(value), None)

  def updatedLoop(details: LoopDetails, value: Boolean): LoopDetails = details.copy(doYouKnowUTR = Some(value))

  override val updatedLoop: (LoopDetails, Boolean) => LoopDetails = ???
}
