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

package controllers.reporter.taxpayer

import controllers.actions._
import forms.taxpayer.WhatIsTaxpayersStartDateForImplementingArrangementFormProvider
import helpers.DateHelper.dateFormatterNumericDMY
import helpers.JourneyHelpers
import models.SelectType.{Individual, Organisation}
import models.{Mode, UserAnswers}
import navigation.Navigator
import pages.reporter.WhatIsReporterTaxpayersStartDateForImplementingArrangementPage
import pages.taxpayer.TaxpayerSelectTypePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{DateInput, NunjucksSupport}

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class WhatIsReporterTaxpayersStartDateForImplementingArrangementController @Inject()(
    override val messagesApi: MessagesApi,
    sessionRepository: SessionRepository,
    navigator: Navigator,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    formProvider: WhatIsTaxpayersStartDateForImplementingArrangementFormProvider,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport {

  val numberOfMonthsToAdd = 6
  val form = formProvider()

  private def actionUrl(mode: Mode): String = routes.WhatIsReporterTaxpayersStartDateForImplementingArrangementController.onPageLoad(mode).url

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val preparedForm = request.userAnswers.get(WhatIsReporterTaxpayersStartDateForImplementingArrangementPage) match {
        case Some(value) => form.fill(value)
        case None        => form
      }

      val viewModel = DateInput.localDate(preparedForm("value"))

      val json = Json.obj (
        "form" -> preparedForm,
        "mode" -> mode,
        "date" -> viewModel,
        "exampleDate" -> LocalDate.now.plusMonths (numberOfMonthsToAdd).format (dateFormatterNumericDMY),
        "displayName" -> getDisplayName(request.userAnswers),
        "actionUrl" -> actionUrl(mode)
      )

      renderer.render ("taxpayer/whatIsTaxpayersStartDateForImplementingArrangement.njk", json).map (Ok (_) )

  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>  {

          val viewModel = DateInput.localDate(formWithErrors("value"))

          val json = Json.obj(
            "form" -> formWithErrors,
            "mode" -> mode,
            "date" -> viewModel,
            "exampleDate" -> LocalDate.now.plusMonths(numberOfMonthsToAdd).format(dateFormatterNumericDMY),
            "displayName" -> getDisplayName(request.userAnswers),
            "actionUrl" -> actionUrl(mode)
          )

          renderer.render("taxpayer/whatIsTaxpayersStartDateForImplementingArrangement.njk", json).map(BadRequest(_))
        },
        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(WhatIsReporterTaxpayersStartDateForImplementingArrangementPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(WhatIsReporterTaxpayersStartDateForImplementingArrangementPage, mode, updatedAnswers))
      )
  }

  private def getDisplayName(userAnswers: UserAnswers): Option[String] = {
   userAnswers.get(TaxpayerSelectTypePage) map { //ToDo this may have to change for reporter
     case Organisation => JourneyHelpers.getOrganisationName(userAnswers)
     case Individual => JourneyHelpers.getIndividualName(userAnswers)
   }
  }
}
