package models

import generators.ModelGenerators
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import play.api.libs.json.{JsError, JsString, Json}

class HallmarkCategoriesSpec extends FreeSpec with MustMatchers with ScalaCheckPropertyChecks with OptionValues with ModelGenerators {

  "HallmarkCategories" - {

    "must deserialise valid values" in {

      val gen = arbitrary[HallmarkCategories]

      forAll(gen) {
        hallmarkCategories =>

          JsString(hallmarkCategories.toString).validate[HallmarkCategories].asOpt.value mustEqual hallmarkCategories
      }
    }

    "must fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!HallmarkCategories.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>

          JsString(invalidValue).validate[HallmarkCategories] mustEqual JsError("error.invalid")
      }
    }

    "must serialise" in {

      val gen = arbitrary[HallmarkCategories]

      forAll(gen) {
        hallmarkCategories =>

          Json.toJson(hallmarkCategories) mustEqual JsString(hallmarkCategories.toString)
      }
    }
  }
}
