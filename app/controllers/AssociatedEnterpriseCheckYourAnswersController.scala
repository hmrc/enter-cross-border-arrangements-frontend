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

package controllers

import controllers.actions._
import models.SelectType
import pages.AssociatedEnterpriseTypePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.{CheckYourAnswersHelper, CheckYourAnswersOrganisationHelper}

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class AssociatedEnterpriseCheckYourAnswersController @Inject()(
    override val messagesApi: MessagesApi,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val helper = new CheckYourAnswersHelper(request.userAnswers)
      val organisationHelper = new CheckYourAnswersOrganisationHelper(request.userAnswers)

      val isOrganisation = request.userAnswers.get(AssociatedEnterpriseTypePage) match {
        case Some(SelectType.Organisation) => true
        case _ => false
      }

      val (summaryRows, countrySummary) = if (isOrganisation) {
        (
          helper.associatedEnterpriseType ++
            organisationHelper.buildOrganisationDetails,
          organisationHelper.buildTaxResidencySummary
        )
      } else {
        (
          Seq(helper.individualName, helper.individualDateOfBirth).flatten ++
            helper.buildIndividualPlaceOfBirthGroup ++
            helper.buildIndividualAddressGroup ++
            helper.buildIndividualEmailAddressGroup,
          helper.buildTaxResidencySummaryForIndividuals
        )
      }

      val isEnterpriseAffected = Seq(helper.isAssociatedEnterpriseAffected).flatten

      val json = Json.obj(
        "summaryRows" -> summaryRows,
        "countrySummary" -> countrySummary,
        "isEnterpriseAffected" -> isEnterpriseAffected
      )

      renderer.render("associatedEnterpriseCheckYourAnswers.njk", json).map(Ok(_))
  }
}
