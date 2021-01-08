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

import com.google.inject.Inject
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import controllers.mixins.RoutingSupport
import models.disclosure.{DisclosureDetails, DisclosureType}
import models.{NormalMode, UserAnswers}
import navigation.NavigatorForDisclosure
import pages.disclosure._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, SummaryList}
import utils.CheckYourAnswersHelper

import scala.concurrent.{ExecutionContext, Future}

class DisclosureCheckYourAnswersController @Inject()(
    override val messagesApi: MessagesApi,
    sessionRepository: SessionRepository,
    navigator: NavigatorForDisclosure,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport with RoutingSupport {

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val helper = new CheckYourAnswersHelper(request.userAnswers)

      val disclosureDetails: Seq[SummaryList.Row] =
        helper.disclosureNamePage.toSeq ++
        helper.disclosureTypePage.toSeq ++
        helper.buildDisclosureSummaryDetails

      renderer.render(
        "disclosure/check-your-answers-disclosure.njk",
        Json.obj("disclosureSummary" -> disclosureDetails
        )
      ).map(Ok(_))
    }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val disclosureDetails: DisclosureDetails = buildDisclosureDetails(request.userAnswers)
      for {
        updatedAnswers <- Future.fromTry(request.userAnswers.set(DisclosureDetailsPage, disclosureDetails))
        _              <- sessionRepository.set(updatedAnswers)
        checkRoute     =  toCheckRoute(NormalMode, updatedAnswers)
      } yield Redirect(navigator.routeMap(DisclosureDetailsPage)(checkRoute)(Some(disclosureDetails))(0))
  }

  def buildDisclosureDetails(userAnswers: UserAnswers): DisclosureDetails = {

    userAnswers.get(DisclosureDetailsPage)
      .flatMap { details =>
        userAnswers.get(DisclosureNamePage).map { disclosureName => details.copy(disclosureName = disclosureName) }
      }
      .flatMap { details =>
        userAnswers.get(DisclosureTypePage).flatMap {
        case disclosureType@DisclosureType.Dac6new =>
          userAnswers.get(DisclosureIdentifyArrangementPage).map { arrangementID =>
            details.copy(disclosureType = disclosureType, arrangementID = Some(arrangementID))
          }
        case disclosureType@DisclosureType.Dac6add =>
          userAnswers.get(DisclosureMarketablePage).map { initialDisclosureMA =>
            details.copy(disclosureType = disclosureType, initialDisclosureMA = initialDisclosureMA)
          }
        case disclosureType@(DisclosureType.Dac6rep | DisclosureType.Dac6del) => // TODO implement DisclosureType.Dac6rep | DisclosureType.Dac6del cases
          throw new UnsupportedOperationException(s"Not yet implemented: $disclosureType")
      }
    }
    .getOrElse(throw new IllegalStateException("Unable to build disclose details"))
  }

}

