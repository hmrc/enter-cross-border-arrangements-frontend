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

package models

import base.ModelSpecBase
import generators.ModelGenerators
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import play.api.libs.json.{JsError, JsString, Json}

class CountryListSpec extends ModelSpecBase with ModelGenerators {

  "ExemptCountries" - {

    "must deserialise valid values" in {

      val gen = Gen.oneOf(CountryList.values)

      forAll(gen) {
        countries =>

          JsString(countries.toString).validate[CountryList].asOpt.value mustEqual countries
      }
    }

    "must fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!CountryList.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>

          JsString(invalidValue).validate[CountryList] mustEqual JsError("error.invalid")
      }
    }

    "must serialise" in {

      val gen = Gen.oneOf(CountryList.values)

      forAll(gen) {
        countries =>

          Json.toJson(countries) mustEqual JsString(countries.toString)
      }
    }

    "must list UK first and then alphabetically" in {

      import CountryList._
      val sorted = List[CountryList](Sweden, UnitedKingdom, Belgium).sorted
      sorted.head mustEqual UnitedKingdom
      sorted(1) mustEqual Belgium
      sorted(2) mustEqual Sweden
    }
  }
}
