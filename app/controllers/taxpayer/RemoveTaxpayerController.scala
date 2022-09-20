/*
 * Copyright 2022 HM Revenue & Customs
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

package controllers.taxpayer

import controllers.actions._
import forms.taxpayer.RemoveTaxpayerFormProvider
import models.UserAnswers
import models.enterprises.AssociatedEnterprise
import pages.enterprises.AssociatedEnterpriseLoopPage
import pages.taxpayer.TaxpayerLoopPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RemoveTaxpayerController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: RemoveTaxpayerFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private val form = formProvider()

  def onPageLoad(id: Int, itemId: String): Action[AnyContent] = (identify andThen getData.apply() andThen requireData).async {
    implicit request =>
      val preparedForm = form

      val json = Json.obj(
        "form"   -> preparedForm,
        "id"     -> id,
        "itemId" -> itemId,
        "name"   -> getTaxpayerName(request.userAnswers, id, itemId),
        "radios" -> Radios.yesNo(preparedForm("value"))
      )

      renderer.render("taxpayer/removeTaxpayer.njk", json).map(Ok(_))
  }

  def onSubmit(id: Int, itemId: String): Action[AnyContent] = (identify andThen getData.apply() andThen requireData).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => {

            val json = Json.obj(
              "form"   -> formWithErrors,
              "id"     -> id,
              "itemId" -> itemId,
              "name"   -> getTaxpayerName(request.userAnswers, id, itemId),
              "radios" -> Radios.yesNo(formWithErrors("value"))
            )

            renderer.render("taxpayer/removeTaxpayer.njk", json).map(BadRequest(_))
          },
          value =>
            if (value) {
              val updatedTaxpayerLoop = request.userAnswers.get(TaxpayerLoopPage, id).map(_.filterNot(_.taxpayerId == itemId)).getOrElse(IndexedSeq.empty)

              for {
                userAnswers <- Future.fromTry(request.userAnswers.set(TaxpayerLoopPage, id, updatedTaxpayerLoop))
                userAnswersRemoveEnterprise <- Future
                  .fromTry(userAnswers.set(AssociatedEnterpriseLoopPage, id, removeTaxpayerAssociations(userAnswers, id, itemId)))
                _ <- sessionRepository.set(userAnswersRemoveEnterprise)
              } yield Redirect(routes.UpdateTaxpayerController.onPageLoad(id))
            } else {
              Future.successful(Redirect(routes.UpdateTaxpayerController.onPageLoad(id)))
            }
        )
  }

  private[taxpayer] def removeTaxpayerAssociations(ua: UserAnswers, id: Int, itemId: String): IndexedSeq[AssociatedEnterprise] =
    ua.get(AssociatedEnterpriseLoopPage, id)
      .map(
        enterpriseLoop =>
          enterpriseLoop.map {
            associatedEnterprise =>
              associatedEnterprise.copy(associatedTaxpayers = associatedEnterprise.associatedTaxpayers.filterNot(_ == itemId))
          }
      )
      .fold(IndexedSeq[AssociatedEnterprise]())(
        enterprise => enterprise.filterNot(_.associatedTaxpayers.isEmpty)
      )

  private[taxpayer] def getTaxpayerName(ua: UserAnswers, id: Int, itemId: String): String =
    ua.get(TaxpayerLoopPage, id).flatMap(_.find(_.taxpayerId == itemId)).map(_.nameAsString).getOrElse("")
}
