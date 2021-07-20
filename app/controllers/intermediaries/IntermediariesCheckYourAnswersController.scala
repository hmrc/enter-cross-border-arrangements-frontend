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
import controllers.exceptions.SomeInformationIsMissingException
import controllers.mixins.{CheckRoute, RoutingSupport}
import models.{NormalMode, SelectType, UserAnswers}
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

class IntermediariesCheckYourAnswersController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  sessionRepository: SessionRepository,
  navigator: NavigatorForIntermediaries,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with RoutingSupport {

  def onPageLoad(id: Int, itemId: Option[String] = None): Action[AnyContent] = (identify andThen getData.apply() andThen requireData).async {
    implicit request =>
      val restoredUserAnswers: UserAnswers = request.userAnswers.restoreFromLoop(IntermediaryLoopPage, id, itemId)
      sessionRepository.set(restoredUserAnswers)

      val helper = new CheckYourAnswersHelper(restoredUserAnswers)

      val (intermediarySummary: Seq[SummaryList.Row], tinCountrySummary: Seq[SummaryList.Row]) =
        restoredUserAnswers.get(IntermediariesTypePage, id) match {

          case Some(SelectType.Organisation) =>
            (helper.intermediariesType(id).toSeq ++ helper.buildOrganisationRows(id), helper.buildTaxResidencySummaryForOrganisation(id))

          case Some(SelectType.Individual) =>
            (helper.intermediariesType(id).toSeq ++ helper.buildIndividualRows(id), helper.buildTaxResidencySummaryForIndividuals(id))

          case _ => throw new SomeInformationIsMissingException(id, Some("Intermediary type not selected."))
        }

      renderer
        .render(
          "intermediaries/intermediariesCheckYourAnswers.njk",
          Json.obj(
            "intermediarySummary"  -> intermediarySummary,
            "tinCountrySummary"    -> tinCountrySummary,
            "intermediarySummary2" -> helper.buildIntermediaries(id),
            "id"                   -> id,
            "mode"                 -> NormalMode
          )
        )
        .map(Ok(_))
  }

  def redirect(id: Int, checkRoute: CheckRoute): Call =
    navigator.routeMap(IntermediariesCheckYourAnswersPage)(checkRoute)(id)(None)(0)

  def onSubmit(id: Int): Action[AnyContent] = (identify andThen getData.apply() andThen requireData).async {
    implicit request =>
      for {
        userAnswers                     <- Future.fromTry(request.userAnswers.set(IntermediaryLoopPage, id))
        userAnswersWithIntermediaryLoop <- Future.fromTry(userAnswers.remove(YouHaveNotAddedAnyIntermediariesPage, id))
        _                               <- sessionRepository.set(userAnswersWithIntermediaryLoop)
        checkRoute = toCheckRoute(NormalMode, userAnswersWithIntermediaryLoop, id)
      } yield Redirect(redirect(id, checkRoute))
  }
}
