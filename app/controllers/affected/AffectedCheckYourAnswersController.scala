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

package controllers.affected

import controllers.actions._
import controllers.mixins.{CheckRoute, RoutingSupport}
import models.affected.Affected
import models.{Mode, SelectType}
import navigation.NavigatorForAffected
import pages.affected.{AffectedCheckYourAnswersPage, AffectedLoopPage, AffectedTypePage}
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

class AffectedCheckYourAnswersController @Inject()(
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  sessionRepository: SessionRepository,
  navigator: NavigatorForAffected,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with RoutingSupport {

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      val helper = new CheckYourAnswersHelper(request.userAnswers)

      val affectedSummary: Seq[SummaryList.Row] =

        request.userAnswers.get(AffectedTypePage) match {

          case Some(SelectType.Organisation) =>
            helper.affectedType.toSeq ++
              helper.organisationName.toSeq ++
              helper.buildOrganisationAddressGroup ++
              helper.buildOrganisationEmailAddressGroup

          case Some(SelectType.Individual) =>
            Seq(helper.affectedType ++
              helper.individualName).flatten ++
              helper.buildIndividualDateOfBirthGroup ++
              helper.buildIndividualPlaceOfBirthGroup ++
              helper.buildIndividualAddressGroup ++
              helper.buildIndividualEmailAddressGroup

          case _ => throw new RuntimeException("Unable to retrieve select type for other parties affected")
      }

      renderer.render(
        "affected/affectedCheckYourAnswers.njk",
        Json.obj(
          "affectedSummary" -> affectedSummary
        )).map(Ok(_))
  }

  def redirect(checkRoute: CheckRoute): Call =
    navigator.routeMap(AffectedCheckYourAnswersPage)(checkRoute)(None)(0)

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val affectedLoopList = request.userAnswers.get(AffectedLoopPage) match {
        case Some(list) => // append to existing list
          list :+ Affected.buildDetails(request.userAnswers)
        case None => // start new list
          IndexedSeq[Affected](Affected.buildDetails(request.userAnswers))
      }
      for {
        userAnswersWithAffectedLoop <- Future.fromTry(request.userAnswers.set(AffectedLoopPage, affectedLoopList))
        _                               <- sessionRepository.set(userAnswersWithAffectedLoop)
        checkRoute                      =  toCheckRoute(mode, userAnswersWithAffectedLoop)
      } yield Redirect(redirect(checkRoute))
  }

}

