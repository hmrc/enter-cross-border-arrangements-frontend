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
import models.UserAnswers
import models.disclosure.{DisclosureDetails, DisclosureType}
import pages.disclosure._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, SummaryList}
import utils.CheckYourAnswersHelper

import scala.concurrent.{ExecutionContext, Future}

class DisclosureCheckYourAnswersController @Inject()(
    override val messagesApi: MessagesApi,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport {

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      // TODO build rows from the disclosure details model
      val helper = new CheckYourAnswersHelper(request.userAnswers)

      val disclosureSummary: Seq[SummaryList.Row] =
        helper.disclosureNamePage.toSeq ++
        helper.disclosureTypePage.toSeq ++
        helper.buildDisclosureSummaryDetails

      // TODO build the disclosure details model from pages
//      val disclosureDetails: DisclosureDetails = buildDisclosureDetails(request.userAnswers)
//
//      for {
//        updatedAnswers <- Future.fromTry(request.userAnswers.set(DisclosureDetailsPage, disclosureDetails))
//      } yield sessionRepository.set(updatedAnswers)

      renderer.render(
        "disclosure/check-your-answers-disclosure.njk",
        Json.obj("disclosureSummary" -> disclosureSummary
        )
      ).map(Ok(_))
    }

  def buildDisclosureDetails(userAnswers: UserAnswers): DisclosureDetails = {

    def getDisclosureDetails = userAnswers.get(DisclosureDetailsPage)
      .orElse(Some(DisclosureDetails("")))
    def getDisclosureName = userAnswers.get(DisclosureNamePage)
    def getDisclosureType = userAnswers.get(DisclosureTypePage)
    def getDisclosureMarketable = userAnswers.get(DisclosureMarketablePage).orElse(Some(false))
    def getDisclosureIdentifyArrangement = userAnswers.get(DisclosureIdentifyArrangementPage)
      .orElse(throw new UnsupportedOperationException(s"Additional Arrangement must be identified"))

    getDisclosureDetails
      .flatMap { details =>
        getDisclosureName.map { disclosureName => details.copy(disclosureName = disclosureName) }
      }
      .flatMap { details =>
        getDisclosureType.flatMap {
        case disclosureType@DisclosureType.Dac6new =>
          getDisclosureMarketable.map { initialDisclosureMA =>
            details.copy(disclosureType = disclosureType, initialDisclosureMA = initialDisclosureMA)
          }
        case disclosureType@DisclosureType.Dac6add =>
          getDisclosureIdentifyArrangement.flatMap { arrangementID =>
            getDisclosureMarketable.map { initialDisclosureMA =>
              details.copy(disclosureType = disclosureType, arrangementID = Some(arrangementID), initialDisclosureMA = initialDisclosureMA)
            }
          }
        case disclosureType@(DisclosureType.Dac6rep | DisclosureType.Dac6del) => // TODO implement DisclosureType.Dac6rep | DisclosureType.Dac6del cases
          throw new UnsupportedOperationException(s"Not yet implemented: $disclosureType")
      }
    }
    .getOrElse(throw new IllegalStateException("Unable to build disclose details"))
  }

}

