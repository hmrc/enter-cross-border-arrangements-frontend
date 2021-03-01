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

package controllers.test

import controllers.actions._
import models.Submission
import pages.affected.AffectedLoopPage
import pages.arrangement.ArrangementDetailsPage
import pages.disclosure.DisclosureDetailsPage
import pages.enterprises.AssociatedEnterpriseLoopPage
import pages.hallmarks.HallmarkDetailsPage
import pages.intermediaries.IntermediaryLoopPage
import pages.reporter.ReporterDetailsPage
import pages.taxpayer.TaxpayerLoopPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import services.XMLGenerationService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class GeneratedXMLController @Inject()(
    override val messagesApi: MessagesApi,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    xmlGenerationService: XMLGenerationService,
    val controllerComponents: MessagesControllerComponents,
    contactRetrievalAction: ContactRetrievalAction,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(id: Int): Action[AnyContent] = (identify andThen getData andThen requireData andThen contactRetrievalAction).async {
    implicit request =>

      //TODO Delete later if no longer needed
      val submission = Submission(request.userAnswers, id, request.enrolmentID)

      xmlGenerationService.createXmlSubmission(submission).fold(
        error =>  throw new RuntimeException(error),
        xml => {

          val prettyPrinter = new scala.xml.PrettyPrinter(80, 4)
          val json = Json.obj(
            "xml" -> prettyPrinter.format(xml)
          )

          renderer.render("generatedXML.njk", json).map(Ok(_))
        }
      )
  }
}
