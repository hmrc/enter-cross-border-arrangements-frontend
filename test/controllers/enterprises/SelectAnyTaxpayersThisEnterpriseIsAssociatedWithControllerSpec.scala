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

import base.SpecBase
import forms.enterprises.SelectAnyTaxpayersThisEnterpriseIsAssociatedWithFormProvider
import helpers.data.ValidUserAnswersForSubmission.{reporterDetailsAsOrganisation, validTaxpayers}
import matchers.JsonMatchers
import models.organisation.Organisation
import models.reporter.RoleInArrangement
import models.taxpayer.{TaxResidency, Taxpayer}
import models.{Address, CheckMode, Country, NormalMode, TaxReferenceNumbers, UnsubmittedDisclosure, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import pages.enterprises.SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage
import pages.reporter.{ReporterDetailsPage, RoleInArrangementPage}
import pages.taxpayer.TaxpayerLoopPage
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.data.Form
import play.api.inject.bind
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import repositories.SessionRepository
import uk.gov.hmrc.viewmodels.Text.Literal
import uk.gov.hmrc.viewmodels.{Checkboxes, NunjucksSupport}

import scala.concurrent.Future

class SelectAnyTaxpayersThisEnterpriseIsAssociatedWithControllerSpec extends SpecBase with NunjucksSupport with JsonMatchers {

  lazy private val selectAnyTaxpayersThisEnterpriseIsAssociatedWithRoute = controllers.enterprises.routes.SelectAnyTaxpayersThisEnterpriseIsAssociatedWithController.onPageLoad(0, NormalMode).url
  lazy private val selectAnyTaxpayersThisEnterpriseIsAssociatedWithCheckRoute = controllers.enterprises.routes.SelectAnyTaxpayersThisEnterpriseIsAssociatedWithController.onPageLoad(0, CheckMode).url

  private val formProvider = new SelectAnyTaxpayersThisEnterpriseIsAssociatedWithFormProvider()
  private val form = formProvider()

  private val address: Address = Address(Some(""), Some(""), Some(""), "Newcastle", Some("NE1"), Country("", "GB", "United Kingdom"))
  private val email = "email@email.com"
  private val taxResidencies = IndexedSeq(TaxResidency(Some(Country("", "GB", "United Kingdom")), Some(TaxReferenceNumbers("UTR1234", None, None))))
  private val taxpayers = IndexedSeq(Taxpayer("123", None, Some(Organisation("Taxpayers Ltd", Some(address), Some(email), taxResidencies)),None))

  private def taxpayerCheckboxes(form: Form[_], taxpayersList: IndexedSeq[Taxpayer]): Seq[Checkboxes.Item] = {
        val field = form("value")
        val items: Seq[Checkboxes.Checkbox] = taxpayersList.map { taxpayer =>
          Checkboxes.Checkbox(label = Literal(taxpayer.nameAsString), value = s"${taxpayer.taxpayerId}")
        }
        Checkboxes.set(field, items)
  }

  "SelectAnyTaxpayersThisEnterpriseIsAssociatedWith Controller" - {

    "must return OK and the correct view for a GET" in {

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(TaxpayerLoopPage, 0, taxpayers)
        .success
        .value

      when(mockRenderer.render(any(), any())(any())) thenReturn Future.successful(Html(""))

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()
      val request = FakeRequest(GET, selectAnyTaxpayersThisEnterpriseIsAssociatedWithRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"       -> form,
        "mode"       -> NormalMode,
        "checkboxes" -> taxpayerCheckboxes(form, taxpayers)
      )

      templateCaptor.getValue mustEqual "enterprises/selectAnyTaxpayersThisEnterpriseIsAssociatedWith.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must populate the view correctly on a GET when in check mode and the question has previously been answered" in {

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(TaxpayerLoopPage, 0, taxpayers)
        .success
        .value
        .set(SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage, 0, List("Taxpayer Ltd"))
        .success
        .value

      when(mockRenderer.render(any(), any())(any())) thenReturn Future.successful(Html(""))

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()
      val request = FakeRequest(GET, selectAnyTaxpayersThisEnterpriseIsAssociatedWithCheckRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.fill(List("Taxpayer Ltd"))

      val expectedJson = Json.obj(
        "form"       -> filledForm,
        "mode"       -> CheckMode,
        "checkboxes" -> taxpayerCheckboxes(filledForm, taxpayers)
      )

      templateCaptor.getValue mustEqual "enterprises/selectAnyTaxpayersThisEnterpriseIsAssociatedWith.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must populate the view correctly on a GET when taxpayer in reporter details has been selected" in {

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(ReporterDetailsPage, 0, reporterDetailsAsOrganisation)
        .success
        .value
        .set(RoleInArrangementPage, 0, RoleInArrangement.Taxpayer)
        .success
        .value
        .set(SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage, 0, List("Taxpayer Ltd"))
        .success
        .value

      when(mockRenderer.render(any(), any())(any())) thenReturn Future.successful(Html(""))

      val field = form("value")
      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()
      val request = FakeRequest(GET, selectAnyTaxpayersThisEnterpriseIsAssociatedWithRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())
      
      val expectedJson = Json.obj(
        "form"       -> form,
        "mode"       -> NormalMode,
        "checkboxes" -> Checkboxes.set(field,
          Seq(Checkboxes.Checkbox(label = Literal(reporterDetailsAsOrganisation.nameAsString), value = s"${reporterDetailsAsOrganisation.nameAsString}")))
      )

      templateCaptor.getValue mustEqual "enterprises/selectAnyTaxpayersThisEnterpriseIsAssociatedWith.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must populate the view correctly on a GET when taxpayer in reporter details & a relevant taxpayer have been selected" in {

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(ReporterDetailsPage, 0, reporterDetailsAsOrganisation)
        .success
        .value
        .set(RoleInArrangementPage, 0, RoleInArrangement.Taxpayer)
        .success
        .value
        .set(TaxpayerLoopPage, 0, validTaxpayers)
        .success
        .value
        .set(SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage, 0, List("Taxpayer Ltd"))
        .success
        .value

      when(mockRenderer.render(any(), any())(any())) thenReturn Future.successful(Html(""))

      val field = form("value")
      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()
      val request = FakeRequest(GET, selectAnyTaxpayersThisEnterpriseIsAssociatedWithRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form" -> form,
        "mode" -> NormalMode,
        "checkboxes" -> Checkboxes.set(field,
          Seq(Checkboxes.Checkbox(label = Literal(reporterDetailsAsOrganisation.nameAsString), value = s"${reporterDetailsAsOrganisation.nameAsString}"),
            Checkboxes.Checkbox(label = Literal("Taxpayers Ltd"), value = "123"),
            Checkboxes.Checkbox(label = Literal("Other Taxpayers Ltd"), value = s"Another ID")
          )
        )
      )
    }

      "must populate the view correctly on a GET when intermediary in reporter details & a relevant taxpayer have been selected" in {

        val userAnswers: UserAnswers = UserAnswers(userAnswersId)
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
          .set(ReporterDetailsPage, 0, reporterDetailsAsOrganisation)
          .success
          .value
          .set(RoleInArrangementPage, 0, RoleInArrangement.Intermediary)
          .success
          .value
          .set(TaxpayerLoopPage, 0, validTaxpayers)
          .success
          .value
          .set(SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage, 0, List("Taxpayer Ltd"))
          .success
          .value

        when(mockRenderer.render(any(), any())(any())) thenReturn Future.successful(Html(""))

        val field = form("value")
        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()
        val request = FakeRequest(GET, selectAnyTaxpayersThisEnterpriseIsAssociatedWithRoute)
        val templateCaptor = ArgumentCaptor.forClass(classOf[String])
        val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

        val result = route(application, request).value

        status(result) mustEqual OK

        verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

        val expectedJson = Json.obj(
          "form"       -> form,
          "mode"       -> NormalMode,
          "checkboxes" -> Checkboxes.set(field,
            Seq(
              Checkboxes.Checkbox(label = Literal("Taxpayers Ltd"), value = "123"),
              Checkboxes.Checkbox(label = Literal("Other Taxpayers Ltd"), value = s"Another ID")
            )
          )
        )

      templateCaptor.getValue mustEqual "enterprises/selectAnyTaxpayersThisEnterpriseIsAssociatedWith.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      val request =
        FakeRequest(POST, selectAnyTaxpayersThisEnterpriseIsAssociatedWithRoute)
          .withFormUrlEncodedBody(("value[0]", "1"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual "/disclose-cross-border-arrangements/manual/associated-enterprises/type/0"

      application.stop()
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()
      val request = FakeRequest(GET, selectAnyTaxpayersThisEnterpriseIsAssociatedWithRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()
      val request = FakeRequest(POST, selectAnyTaxpayersThisEnterpriseIsAssociatedWithRoute).withFormUrlEncodedBody(("value[0]", "1"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
