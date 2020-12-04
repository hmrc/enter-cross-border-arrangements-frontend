package controllers.enterprises

import base.SpecBase
import forms.enterprises.SelectAnyTaxpayersThisEnterpriseIsAssociatedWithFormProvider
import matchers.JsonMatchers
import models.enterprises.SelectAnyTaxpayersThisEnterpriseIsAssociatedWith
import models.{NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.enterprises.SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage
import play.api.inject.bind
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import repositories.SessionRepository
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class SelectAnyTaxpayersThisEnterpriseIsAssociatedWithControllerSpec extends SpecBase with MockitoSugar with NunjucksSupport with JsonMatchers {

  def onwardRoute = Call("GET", "/foo")

  lazy val selectAnyTaxpayersThisEnterpriseIsAssociatedWithRoute = controllers.enterprises.routes.SelectAnyTaxpayersThisEnterpriseIsAssociatedWithController.onPageLoad(NormalMode).url

  val formProvider = new SelectAnyTaxpayersThisEnterpriseIsAssociatedWithFormProvider()
  val form = formProvider()

  "SelectAnyTaxpayersThisEnterpriseIsAssociatedWith Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any())) thenReturn Future.successful(Html(""))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
      val request = FakeRequest(GET, selectAnyTaxpayersThisEnterpriseIsAssociatedWithRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"       -> form,
        "mode"       -> NormalMode,
        "checkboxes" -> SelectAnyTaxpayersThisEnterpriseIsAssociatedWith.checkboxes(form)
      )

      templateCaptor.getValue mustEqual "enterprises/selectAnyTaxpayersThisEnterpriseIsAssociatedWith.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any())) thenReturn Future.successful(Html(""))

      val userAnswers = UserAnswers(userAnswersId).set(SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage, SelectAnyTaxpayersThisEnterpriseIsAssociatedWith.values.toSet).success.value
      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()
      val request = FakeRequest(GET, selectAnyTaxpayersThisEnterpriseIsAssociatedWithRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.fill(SelectAnyTaxpayersThisEnterpriseIsAssociatedWith.values.toSet)

      val expectedJson = Json.obj(
        "form"       -> filledForm,
        "mode"       -> NormalMode,
        "checkboxes" -> SelectAnyTaxpayersThisEnterpriseIsAssociatedWith.checkboxes(filledForm)
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
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      val request =
        FakeRequest(POST, selectAnyTaxpayersThisEnterpriseIsAssociatedWithRoute)
          .withFormUrlEncodedBody(("value[0]", SelectAnyTaxpayersThisEnterpriseIsAssociatedWith.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
      val request =  FakeRequest(POST, selectAnyTaxpayersThisEnterpriseIsAssociatedWithRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"       -> boundForm,
        "mode"       -> NormalMode,
        "checkboxes" -> SelectAnyTaxpayersThisEnterpriseIsAssociatedWith.checkboxes(boundForm)
      )

      templateCaptor.getValue mustEqual "enterprises/selectAnyTaxpayersThisEnterpriseIsAssociatedWith.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()
      val request = FakeRequest(GET, selectAnyTaxpayersThisEnterpriseIsAssociatedWithRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()
      val request = FakeRequest(POST, selectAnyTaxpayersThisEnterpriseIsAssociatedWithRoute).withFormUrlEncodedBody(("value[0]", SelectAnyTaxpayersThisEnterpriseIsAssociatedWith.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
