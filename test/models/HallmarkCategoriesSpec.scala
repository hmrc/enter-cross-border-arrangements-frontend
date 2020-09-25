/*
 * Copyright 2020 HM Revenue & Customs
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