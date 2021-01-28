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
import models.{Mode, SelectType, UserAnswers}
import navigation.NavigatorForEnterprises
import pages.enterprises.{AssociatedEnterpriseCheckYourAnswersPage, AssociatedEnterpriseLoopPage, AssociatedEnterpriseTypePage, YouHaveNotAddedAnyAssociatedEnterprisesPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.CheckYourAnswersHelper

import javax.inject.Inject
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
          Seq(helper.selectAnyTaxpayersThisEnterpriseIsAssociatedWith(id),
            helper.associatedEnterpriseType(id),
            helper.organisationName(id)).flatten ++
          helper.buildOrganisationAddressGroup(id) ++
          helper.buildOrganisationEmailAddressGroup(id),
          helper.buildTaxResidencySummaryForOrganisation(id)
        )
      } else {
        (
          Seq(helper.selectAnyTaxpayersThisEnterpriseIsAssociatedWith(id),
            helper.associatedEnterpriseType(id),
            helper.individualName(id)).flatten ++
          helper.buildIndividualDateOfBirthGroup(id) ++
          helper.buildIndividualPlaceOfBirthGroup(id) ++
          helper.buildIndividualAddressGroup(id) ++
          helper.buildIndividualEmailAddressGroup(id),
          helper.buildTaxResidencySummaryForIndividuals(id)
        )
      }

      val isEnterpriseAffected = Seq(helper.isAssociatedEnterpriseAffected(id)).flatten

      val json = Json.obj(
        "id" -> id,
        "mode" -> mode,
        "summaryRows" -> summaryRows,
        "countrySummary" -> countrySummary,
        "isEnterpriseAffected" -> isEnterpriseAffected
      )

      renderer.render("enterprises/associatedEnterpriseCheckYourAnswers.njk", json).map(Ok(_))
  }

  def redirect(id: Int, checkRoute: CheckRoute): Call =
    navigator.routeMap(AssociatedEnterpriseCheckYourAnswersPage)(checkRoute)(id)(None)(0)

  def onSubmit(id: Int, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      for {
        userAnswers <- Future.fromTry(request.userAnswers.remove(YouHaveNotAddedAnyAssociatedEnterprisesPage, id))
        userAnswersWithEnterpriseLoop <- Future.fromTry(userAnswers.set(AssociatedEnterpriseLoopPage, id, updatedLoopList(request.userAnswers, id)))
        _ <- sessionRepository.set(userAnswersWithEnterpriseLoop)
        checkRoute     =  toCheckRoute(mode, userAnswersWithEnterpriseLoop)
      } yield {
        Redirect(redirect(id, checkRoute))
      }
  }

  private[enterprises] def updatedLoopList(userAnswers: UserAnswers, id: Int): IndexedSeq[AssociatedEnterprise] = {
    val associatedEnterprise: AssociatedEnterprise = AssociatedEnterprise.buildAssociatedEnterprise(userAnswers, id)
    userAnswers.get(AssociatedEnterpriseLoopPage, id) match {
      case Some(list) => // append to existing list
        list.filterNot(_.nameAsString == associatedEnterprise.nameAsString) :+ associatedEnterprise
      case None => // start new list
        IndexedSeq[AssociatedEnterprise](associatedEnterprise)
    }
  }

}
