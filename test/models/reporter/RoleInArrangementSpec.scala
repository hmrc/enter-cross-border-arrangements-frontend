package models.reporter

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsError, JsString, Json}

class RoleInArrangementSpec extends FreeSpec with MustMatchers with ScalaCheckPropertyChecks with OptionValues {

  "RoleInArrangement" - {

    "must deserialise valid values" in {

      val gen = Gen.oneOf(RoleInArrangement.values)

      forAll(gen) {
        roleInArrangement =>

          JsString(roleInArrangement.toString).validate[RoleInArrangement].asOpt.value mustEqual roleInArrangement
      }
    }

    "must fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!RoleInArrangement.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>

          JsString(invalidValue).validate[RoleInArrangement] mustEqual JsError("error.invalid")
      }
    }

    "must serialise" in {

      val gen = Gen.oneOf(RoleInArrangement.values)

      forAll(gen) {
        roleInArrangement =>

          Json.toJson(roleInArrangement) mustEqual JsString(roleInArrangement.toString)
      }
    }
  }
}
