package models

import generators.ModelGenerators
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import play.api.libs.json.{JsError, JsString, Json}

class HallmarkCSpec extends FreeSpec with MustMatchers with ScalaCheckPropertyChecks with OptionValues with ModelGenerators {

  "HallmarkC" - {

    "must deserialise valid values" in {

      val gen = arbitrary[HallmarkC]

      forAll(gen) {
        hallmarkC =>

          JsString(hallmarkC.toString).validate[HallmarkC].asOpt.value mustEqual hallmarkC
      }
    }

    "must fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!HallmarkC.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>

          JsString(invalidValue).validate[HallmarkC] mustEqual JsError("error.invalid")
      }
    }

    "must serialise" in {

      val gen = arbitrary[HallmarkC]

      forAll(gen) {
        hallmarkC =>

          Json.toJson(hallmarkC) mustEqual JsString(hallmarkC.toString)
      }
    }
  }
}
