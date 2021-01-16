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

package controllers.enterprises

import controllers.actions._
import controllers.mixins.{CheckRoute, RoutingSupport}
import models.enterprises.AssociatedEnterprise

import javax.inject.Inject
import models.{Mode, SelectType}
import navigation.NavigatorForEnterprises
import pages.enterprises.{AssociatedEnterpriseCheckYourAnswersPage, AssociatedEnterpriseLoopPage, AssociatedEnterpriseTypePage, YouHaveNotAddedAnyAssociatedEnterprisesPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.CheckYourAnswersHelper

import scala.concurrent.{ExecutionContext, Future}

class AssociatedEnterpriseCheckYourAnswersController @Inject()(
    override val messagesApi: MessagesApi,
    navigator: NavigatorForEnterprises,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    sessionRepository: SessionRepository,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with RoutingSupport {

  def onPageLoad(id: Int, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val helper = new CheckYourAnswersHelper(request.userAnswers)

      val isOrganisation = request.userAnswers.get(AssociatedEnterpriseTypePage, id) match {
        case Some(SelectType.Organisation) => true
        case _ => false
      }

      val (summaryRows, countrySummary) = if (isOrganisation) {
        (
          Seq(helper.associatedEnterpriseType, helper.organisationName).flatten ++
          helper.buildOrganisationAddressGroup ++
          helper.buildOrganisationEmailAddressGroup,
          helper.buildTaxResidencySummaryForOrganisation
        )
      } else {
        (
          Seq(helper.associatedEnterpriseType, helper.individualName).flatten ++
            helper.buildIndividualDateOfBirthGroup ++
            helper.buildIndividualPlaceOfBirthGroup ++
            helper.buildIndividualAddressGroup ++
            helper.buildIndividualEmailAddressGroup,
          helper.buildTaxResidencySummaryForIndividuals
        )
      }

      val isEnterpriseAffected = Seq(helper.isAssociatedEnterpriseAffected).flatten

      val json = Json.obj(
        "id" -> id,
        "mode" -> mode,
        "summaryRows" -> summaryRows,
        "countrySummary" -> countrySummary,
        "isEnterpriseAffected" -> isEnterpriseAffected
      )

      renderer.render("enterprises/associatedEnterpriseCheckYourAnswers.njk", json).map(Ok(_))
  }

  def redirect(checkRoute: CheckRoute): Call =
    navigator.routeMap(AssociatedEnterpriseCheckYourAnswersPage)(checkRoute)(None)(0)

  def onSubmit(id: Int, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val enterpriseLoopList = request.userAnswers.get(AssociatedEnterpriseLoopPage, id) match {
        case Some(list) => // append to existing list
          list :+ AssociatedEnterprise.buildAssociatedEnterprise(request.userAnswers)
        case None => // start new list
          IndexedSeq[AssociatedEnterprise](AssociatedEnterprise.buildAssociatedEnterprise(request.userAnswers))
      }

      for {
        userAnswers <- Future.fromTry(request.userAnswers.remove(YouHaveNotAddedAnyAssociatedEnterprisesPage, id))
        userAnswersWithEnterpriseLoop <- Future.fromTry(userAnswers.set(AssociatedEnterpriseLoopPage, id, enterpriseLoopList))
        _ <- sessionRepository.set(userAnswersWithEnterpriseLoop)
        checkRoute     =  toCheckRoute(mode, userAnswersWithEnterpriseLoop)
      } yield {
        Redirect(redirect(checkRoute))
      }
  }
}
