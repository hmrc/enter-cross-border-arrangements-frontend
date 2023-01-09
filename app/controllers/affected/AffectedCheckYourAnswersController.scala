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

package controllers.affected

import controllers.actions._
import controllers.exceptions.UnsupportedRouteException
import controllers.mixins.{CheckRoute, RoutingSupport}
import models.{NormalMode, SelectType, UserAnswers}
import navigation.NavigatorForAffected
import pages.affected.{AffectedCheckYourAnswersPage, AffectedLoopPage, AffectedTypePage, YouHaveNotAddedAnyAffectedPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.CheckYourAnswersHelper

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AffectedCheckYourAnswersController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  sessionRepository: SessionRepository,
  navigator: NavigatorForAffected,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with RoutingSupport {

  def onPageLoad(id: Int, itemId: Option[String] = None): Action[AnyContent] = (identify andThen getData.apply() andThen requireData).async {
    implicit request =>
      val restoredUserAnswers: UserAnswers = request.userAnswers.restoreFromLoop(AffectedLoopPage, id, itemId)
      sessionRepository.set(restoredUserAnswers)

      val helper = new CheckYourAnswersHelper(restoredUserAnswers)

      val (affectedSummary, countrySummary) =
        restoredUserAnswers.get(AffectedTypePage, id) match {

          case Some(SelectType.Organisation) =>
            (
              helper.affectedType(id).toSeq ++ helper.buildOrganisationRows(id),
              helper.buildTaxResidencySummaryForOrganisation(id)
            )
          case Some(SelectType.Individual) =>
            (
              helper.affectedType(id).toSeq ++ helper.buildIndividualRows(id),
              helper.buildTaxResidencySummaryForIndividuals(id)
            )

          case _ => throw new UnsupportedRouteException(id)
        }

      renderer
        .render("affected/affectedCheckYourAnswers.njk",
                Json.obj(
                  "id"              -> id,
                  "affectedSummary" -> affectedSummary,
                  "countrySummary"  -> countrySummary
                )
        )
        .map(Ok(_))
  }

  def redirect(id: Int, checkRoute: CheckRoute): Call =
    navigator.routeMap(AffectedCheckYourAnswersPage)(checkRoute)(id)(None)(0)

  def onSubmit(id: Int): Action[AnyContent] = (identify andThen getData.apply() andThen requireData).async {
    implicit request =>
      for {
        userAnswers                 <- Future.fromTry(request.userAnswers.set(AffectedLoopPage, id))
        userAnswersWithAffectedLoop <- Future.fromTry(userAnswers.remove(YouHaveNotAddedAnyAffectedPage, id))
        _                           <- sessionRepository.set(userAnswersWithAffectedLoop)
        checkRoute = toCheckRoute(NormalMode, userAnswersWithAffectedLoop)
      } yield Redirect(redirect(id, checkRoute))
  }
}
