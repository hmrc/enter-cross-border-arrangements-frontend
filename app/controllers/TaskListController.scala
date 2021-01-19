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

package controllers

import connectors.{CrossBorderArrangementsConnector, ValidationConnector}
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import pages.GeneratedIDPage
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import services.{TransformationService, XMLGenerationService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TaskListController @Inject()(
                                    val controllerComponents: MessagesControllerComponents,
                                    xmlGenerationService: XMLGenerationService,
                                    transformationService: TransformationService,
                                    identify: IdentifierAction,
                                    getData: DataRetrievalAction,
                                    requireData: DataRequiredAction,
                                    validationConnector: ValidationConnector,
                                    crossBorderArrangementsConnector: CrossBorderArrangementsConnector,
                                    sessionRepository: SessionRepository,
                                    renderer: Renderer
                                  )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onSubmit(id: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
     //generate xml from user answers
     val xml = xmlGenerationService.createXmlSubmission(request.userAnswers, id)
     //send it off to be validated and business rules
      validationConnector.sendForValidation(xml).flatMap {
        _.fold(
            //did it fail? oh my god - hand back to the user to fix
            errors => {
              val json = Json.obj(
                "errors" -> errors
              )
              renderer.render("validationErrors.njk", json).map(Ok(_))
            },

            //did it succeed - hand off to the backend to do it's generating thing
            messageRefId => {
              val uniqueXmlSubmission = transformationService.rewriteMessageRefID(xml, messageRefId)
              val submission = transformationService.constructSubmission("manual-submission.xml", request.enrolmentID, uniqueXmlSubmission)
              for {
                ids <- crossBorderArrangementsConnector.submitXML(submission)
                userAnswersWithIDs <- Future.fromTry(request.userAnswers.set(GeneratedIDPage, id, ids))
                _                  <- sessionRepository.set(userAnswersWithIDs)
              } yield {
                Redirect(routes.IndexController.onPageLoad().url) //TODO: Correct to page
              }
            }
          )
      }
  }
}
