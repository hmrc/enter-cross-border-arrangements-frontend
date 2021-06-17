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

package controllers.individual

import base.SpecBase
import models.{Country, LoopDetails, TaxReferenceNumbers, UnsubmittedDisclosure, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.scalatest.BeforeAndAfterEach
import pages.individual.IndividualLoopPage
import pages.unsubmitted.UnsubmittedDisclosurePage
import play.api.libs.json._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html

import scala.concurrent.Future

class IndividualCheckYourAnswersController_TaxResidencySpec extends SpecBase with BeforeAndAfterEach {

  val countryUK    = Country("valid","GB","United Kingdom")
  val countryNonUK = Country("valid","FR","France")

  val taxReferenceNumberSingle   = TaxReferenceNumbers("1234567890", None, None)
  val taxReferenceNumberMultiple = TaxReferenceNumbers("1234567890", Some("2345678901"), None)

  override def beforeEach: Unit = {
    reset(
      mockRenderer
    )
    when(mockRenderer.render(any(), any())(any()))
      .thenReturn(Future.successful(Html("")))

  }

  private def verifyList(userAnswers: UserAnswers)(
    assertFunction: String => Unit): Unit = {

    val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

    val request = FakeRequest(GET, controllers.individual.routes.IndividualCheckYourAnswersController.onPageLoad(0).url)

    val result = route(application, request).value

    status(result) mustEqual OK

    val templateCaptor = ArgumentCaptor.forClass(classOf[String])
    val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

    verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

    val json: JsObject = jsonCaptor.getValue
    val list = (json \ "countrySummary" ).toString

    templateCaptor.getValue mustEqual "individual/check-your-answers.njk"
    assertFunction(list)

    application.stop()

  }

  "Check Your Answers Controller" - {

    "must return country rows for UK, if known" in {

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(IndividualLoopPage, 0, IndexedSeq(LoopDetails(taxResidentOtherCountries = Some(false),
          whichCountry = Some(countryUK),
          doYouKnowTIN = Some(false),
          taxNumbersNonUK = None,
          doYouKnowUTR = Some(false),
          taxNumbersUK = None
        )))
        .success.value
      verifyList(userAnswers) { list =>
        list.contains("""{"key":{"text":"Tax resident countries","classes":"govuk-!-width-one-half"},"value":{"text":""}""") mustBe true
        list.contains("/disclose-cross-border-arrangements/manual/individual/change-which-country-tax-0") mustBe true
        list.contains("""{"key":{"text":"Country ","classes":"govuk-!-width-one-half"},"value":{"text":"United Kingdom"}}""") mustBe true

      }
    }

    "must return country rows with UTR for UK, if known" in {

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(IndividualLoopPage, 0, IndexedSeq(LoopDetails(taxResidentOtherCountries = Some(false),
          whichCountry = Some(countryUK),
          doYouKnowTIN = Some(false),
          taxNumbersNonUK = None,
          doYouKnowUTR = Some(true),
          taxNumbersUK = Some(taxReferenceNumberSingle)
        )))
        .success.value
      verifyList(userAnswers) { list =>
        list.contains("""{"key":{"text":"Tax resident countries","classes":"govuk-!-width-one-half"},"value":{"text":""}""") mustBe true
        list.contains("/disclose-cross-border-arrangements/manual/individual/change-which-country-tax-0") mustBe true
        list.contains("""{"key":{"text":"Country ","classes":"govuk-!-width-one-half"},"value":{"text":"United Kingdom"}}""") mustBe true
        list.contains("""{"key":{"text":"UTR","classes":"govuk-!-width-one-half"},"value":{"text":"1234567890"}}""") mustBe true

      }
    }

    "must return country rows with multiple UTRs for UK, if known" in {

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(IndividualLoopPage, 0, IndexedSeq(LoopDetails(taxResidentOtherCountries = Some(false),
          whichCountry = Some(countryUK),
          doYouKnowTIN = Some(false),
          taxNumbersNonUK = None,
          doYouKnowUTR = Some(true),
          taxNumbersUK = Some(taxReferenceNumberMultiple)
        )))
        .success.value
      verifyList(userAnswers) { list =>
        list.contains("""{"key":{"text":"Tax resident countries","classes":"govuk-!-width-one-half"},"value":{"text":""}""") mustBe true
        list.contains("/disclose-cross-border-arrangements/manual/individual/change-which-country-tax-0") mustBe true
        list.contains("""{"key":{"text":"Country ","classes":"govuk-!-width-one-half"},"value":{"text":"United Kingdom"}}""") mustBe true
        list.contains("""{"key":{"text":"UTRs","classes":"govuk-!-width-one-half"},"value":{"text":"1234567890, 2345678901"}}""") mustBe true

      }
    }

    "must return country row for non UK, if known" in {

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(IndividualLoopPage, 0, IndexedSeq(LoopDetails(taxResidentOtherCountries = Some(false),
          whichCountry = Some(countryNonUK),
          doYouKnowTIN = Some(false),
          taxNumbersNonUK = None,
          doYouKnowUTR = Some(false),
          taxNumbersUK = None
        )))
        .success.value
      verifyList(userAnswers) { list =>
        list.contains("""{"key":{"text":"Tax resident countries","classes":"govuk-!-width-one-half"},"value":{"text":""}""") mustBe true
        list.contains("/disclose-cross-border-arrangements/manual/individual/change-which-country-tax-0") mustBe true
        list.contains("""{"key":{"text":"Country ","classes":"govuk-!-width-one-half"},"value":{"text":"France"}}""") mustBe true

      }
    }

    "must return country rows with TIN for non UK, if known" in {

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(IndividualLoopPage, 0, IndexedSeq(LoopDetails(taxResidentOtherCountries = Some(false),
          whichCountry = Some(countryNonUK),
          doYouKnowTIN = Some(true),
          taxNumbersNonUK = Some(taxReferenceNumberSingle),
          doYouKnowUTR = Some(false),
          taxNumbersUK = None
        )))
        .success.value
      verifyList(userAnswers) { list =>
        list.contains("""{"key":{"text":"Tax resident countries","classes":"govuk-!-width-one-half"},"value":{"text":""}""") mustBe true
        list.contains("/disclose-cross-border-arrangements/manual/individual/change-which-country-tax-0") mustBe true
        list.contains("""{"key":{"text":"Country ","classes":"govuk-!-width-one-half"},"value":{"text":"France"}}""") mustBe true
        list.contains("""{"key":{"text":"Tax identification number for France","classes":"govuk-!-width-one-half"},"value":{"text":"1234567890"}}""") mustBe true

      }
    }

    "must return country rows with multiple TINs for non UK, if known" in {

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(IndividualLoopPage, 0, IndexedSeq(LoopDetails(taxResidentOtherCountries = Some(false),
          whichCountry = Some(countryNonUK),
          doYouKnowTIN = Some(true),
          taxNumbersNonUK = Some(taxReferenceNumberMultiple),
          doYouKnowUTR = Some(false),
          taxNumbersUK = None
        )))
        .success.value
      verifyList(userAnswers) { list =>
        list.contains("""{"key":{"text":"Tax resident countries","classes":"govuk-!-width-one-half"},"value":{"text":""}""") mustBe true
        list.contains("/disclose-cross-border-arrangements/manual/individual/change-which-country-tax-0") mustBe true
        list.contains("""{"key":{"text":"Country ","classes":"govuk-!-width-one-half"},"value":{"text":"France"}}""") mustBe true
        list.contains("""{"key":{"text":"Tax identification numbers for France","classes":"govuk-!-width-one-half"},"value":{"text":"1234567890, 2345678901"}}""") mustBe true

      }
    }

    "must return country combination rows" in {

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
        .set(IndividualLoopPage, 0, IndexedSeq(
          LoopDetails(taxResidentOtherCountries = Some(false),
          whichCountry = Some(countryUK),
          doYouKnowTIN = Some(false),
          taxNumbersNonUK = None,
          doYouKnowUTR = Some(true),
          taxNumbersUK = Some(taxReferenceNumberMultiple)
          )
          , LoopDetails(taxResidentOtherCountries = Some(false),
          whichCountry = Some(countryNonUK),
          doYouKnowTIN = Some(true),
          taxNumbersNonUK = Some(taxReferenceNumberMultiple),
          doYouKnowUTR = Some(false),
          taxNumbersUK = None
        )))
        .success.value
      verifyList(userAnswers) { list =>
        list.contains("""{"key":{"text":"Tax resident countries","classes":"govuk-!-width-one-half"},"value":{"text":""}""") mustBe true
        list.contains("/disclose-cross-border-arrangements/manual/individual/change-which-country-tax-0") mustBe true
        list.contains("""{"key":{"text":"Country 1","classes":"govuk-!-width-one-half"},"value":{"text":"United Kingdom"}}""") mustBe true
        list.contains("""{"key":{"text":"UTRs","classes":"govuk-!-width-one-half"},"value":{"text":"1234567890, 2345678901"}}""") mustBe true
        list.contains("""{"key":{"text":"Country 2","classes":"govuk-!-width-one-half"},"value":{"text":"France"}}""") mustBe true
        list.contains("""{"key":{"text":"Tax identification numbers for France","classes":"govuk-!-width-one-half"},"value":{"text":"1234567890, 2345678901"}}""") mustBe true

      }
    }

  }
}

