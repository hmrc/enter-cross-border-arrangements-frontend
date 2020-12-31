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

package controllers.intermediaries

import controllers.actions._
import javax.inject.Inject
import models.SelectType
import models.SelectType.{Individual, Organisation}
import pages.intermediaries.IntermediariesTypePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.SummaryList
import utils.CheckYourAnswersHelper

import scala.concurrent.ExecutionContext

class IntermediariesCheckYourAnswersController @Inject()(
                                                          override val messagesApi: MessagesApi,
                                                          identify: IdentifierAction,
                                                          getData: DataRetrievalAction,
                                                          requireData: DataRequiredAction,
                                                          val controllerComponents: MessagesControllerComponents,
                                                          renderer: Renderer
                                                       )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      val helper = new CheckYourAnswersHelper(request.userAnswers)

      val (intermediarySummary: Seq[SummaryList.Row], tinCountrySummary: Seq[SummaryList.Row], intermediarySummary2: Seq[SummaryList.Row]) =

        request.userAnswers.get(IntermediariesTypePage) match {

          case Some(SelectType.Organisation) =>
            (helper.intermediariesType.toSeq ++
              helper.organisationName.toSeq ++
              helper.buildOrganisationAddressGroup ++
              helper.buildOrganisationEmailAddressGroup,

              helper.buildTaxResidencySummaryForOrganisation,

              Seq(helper.whatTypeofIntermediary ++
              helper.isExemptionKnown ++
              helper.isExemptionCountryKnown).flatten ++
              helper.exemptCountries.toSeq
              )

          case Some(SelectType.Individual) =>
            (Seq(helper.intermediariesType ++
              helper.individualName ++
              helper.individualDateOfBirth).flatten ++
              helper.buildIndividualPlaceOfBirthGroup ++
              helper.buildIndividualAddressGroup ++
              helper.buildIndividualEmailAddressGroup,

              helper.buildTaxResidencySummaryForIndividuals,

              Seq(helper.whatTypeofIntermediary ++
              helper.isExemptionKnown ++
              helper.isExemptionCountryKnown).flatten ++
              helper.exemptCountries.toSeq
            )

          case _ => throw new RuntimeException("Unable to retrieve select type for Intermediary")
      }

      renderer.render(
        "intermediaries/intermediariesCheckYourAnswers.njk",
        Json.obj(
          "intermediarySummary" -> intermediarySummary,
          "tinCountrySummary" -> tinCountrySummary,
          "intermediarySummary2" -> intermediarySummary2

        )).map(Ok(_))
  }
}

