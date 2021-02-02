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

package controllers.intermediaries

import controllers.actions._
import controllers.exceptions.UnsupportedRouteException
import controllers.mixins.{CheckRoute, RoutingSupport}
import models.intermediaries.Intermediary
import models.{Mode, NormalMode, SelectType, UserAnswers}
import navigation.NavigatorForIntermediaries
import pages.intermediaries.{IntermediariesCheckYourAnswersPage, IntermediariesTypePage, IntermediaryLoopPage, YouHaveNotAddedAnyIntermediariesPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.SummaryList
import utils.CheckYourAnswersHelper

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class IntermediariesCheckYourAnswersController @Inject()(
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  sessionRepository: SessionRepository,
  navigator: NavigatorForIntermediaries,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with RoutingSupport {

  def onPageLoad(id: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      val helper = new CheckYourAnswersHelper(request.userAnswers)

      val (intermediarySummary: Seq[SummaryList.Row], tinCountrySummary: Seq[SummaryList.Row], intermediarySummary2: Seq[SummaryList.Row]) =

        request.userAnswers.get(IntermediariesTypePage, id) match {

          case Some(SelectType.Organisation) =>
            (helper.intermediariesType(id).toSeq ++
              helper.organisationName(id).toSeq ++
              helper.buildOrganisationAddressGroup(id) ++
              helper.buildOrganisationEmailAddressGroup(id),

              helper.buildTaxResidencySummaryForOrganisation(id),

              Seq(helper.whatTypeofIntermediary(id) ++
              helper.isExemptionKnown(id) ++
              helper.isExemptionCountryKnown(id)).flatten ++
              helper.exemptCountries(id).toSeq
              )

          case Some(SelectType.Individual) =>
            (Seq(helper.intermediariesType(id) ++
              helper.individualName(id)).flatten ++
              helper.buildIndividualDateOfBirthGroup(id) ++
              helper.buildIndividualPlaceOfBirthGroup(id) ++
              helper.buildIndividualAddressGroup(id) ++
              helper.buildIndividualEmailAddressGroup(id),

              helper.buildTaxResidencySummaryForIndividuals(id),

              Seq(helper.whatTypeofIntermediary(id) ++
              helper.isExemptionKnown(id) ++
              helper.isExemptionCountryKnown(id)).flatten ++
              helper.exemptCountries(id).toSeq
            )

          case _ => throw new UnsupportedRouteException(id)
      }

      renderer.render(
        "intermediaries/intermediariesCheckYourAnswers.njk",
        Json.obj(
          "intermediarySummary" -> intermediarySummary,
          "tinCountrySummary"          -> tinCountrySummary,
          "intermediarySummary2"      -> intermediarySummary2,
          "id"                        -> id,
          "mode"       -> NormalMode
        )).map(Ok(_))
  }

  def redirect(id: Int, checkRoute: CheckRoute): Call =
    navigator.routeMap(IntermediariesCheckYourAnswersPage)(checkRoute)(id)(None)(0)

  def onSubmit(id: Int, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      for {
        userAnswers                     <- Future.fromTry(request.userAnswers.remove(YouHaveNotAddedAnyIntermediariesPage, id))
        userAnswersWithIntermediaryLoop <- Future.fromTry(userAnswers.set(IntermediaryLoopPage, id, updatedLoopList(request.userAnswers, id)))
        _                               <- sessionRepository.set(userAnswersWithIntermediaryLoop)
        checkRoute                      =  toCheckRoute(mode, userAnswersWithIntermediaryLoop, id)
      } yield {
        Redirect(redirect(id, checkRoute))
      }
  }

  private[intermediaries] def updatedLoopList(userAnswers: UserAnswers, id: Int): IndexedSeq[Intermediary] = {
    val intermediary: Intermediary = Intermediary.buildIntermediaryDetails(userAnswers, id)
    userAnswers.get(IntermediaryLoopPage, id) match {
      case Some(list) => // append to existing list without duplication
        list.filterNot(_.nameAsString == intermediary.nameAsString) :+ intermediary
      case None =>       // start new list
        IndexedSeq[Intermediary](intermediary)
    }
  }
}

