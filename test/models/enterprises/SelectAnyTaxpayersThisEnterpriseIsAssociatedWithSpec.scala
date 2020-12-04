package models.enterprises

import generators.ModelGenerators
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsError, JsString, Json}

class SelectAnyTaxpayersThisEnterpriseIsAssociatedWithSpec extends FreeSpec with MustMatchers with ScalaCheckPropertyChecks with OptionValues with ModelGenerators {

  "SelectAnyTaxpayersThisEnterpriseIsAssociatedWith" - {

    "must deserialise valid values" in {

      val gen = arbitrary[SelectAnyTaxpayersThisEnterpriseIsAssociatedWith]

      forAll(gen) {
        selectAnyTaxpayersThisEnterpriseIsAssociatedWith =>

          JsString(selectAnyTaxpayersThisEnterpriseIsAssociatedWith.toString).validate[SelectAnyTaxpayersThisEnterpriseIsAssociatedWith].asOpt.value mustEqual selectAnyTaxpayersThisEnterpriseIsAssociatedWith
      }
    }

    "must fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!SelectAnyTaxpayersThisEnterpriseIsAssociatedWith.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>

          JsString(invalidValue).validate[SelectAnyTaxpayersThisEnterpriseIsAssociatedWith] mustEqual JsError("error.invalid")
      }
    }

    "must serialise" in {

      val gen = arbitrary[SelectAnyTaxpayersThisEnterpriseIsAssociatedWith]

      forAll(gen) {
        selectAnyTaxpayersThisEnterpriseIsAssociatedWith =>

          Json.toJson(selectAnyTaxpayersThisEnterpriseIsAssociatedWith) mustEqual JsString(selectAnyTaxpayersThisEnterpriseIsAssociatedWith.toString)
      }
    }
  }
}
