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

package models.hallmarks

import generators.ModelGenerators
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsError, JsString, Json}

class HallmarkESpec extends FreeSpec with MustMatchers with ScalaCheckPropertyChecks with OptionValues with ModelGenerators {

  "HallmarkE" - {

    "must deserialise valid values" in {

      val gen = arbitrary[HallmarkE]

      forAll(gen) {
        hallmarkE =>

          JsString(hallmarkE.toString).validate[HallmarkE].asOpt.value mustEqual hallmarkE
      }
    }

    "must fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!HallmarkE.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>

          JsString(invalidValue).validate[HallmarkE] mustEqual JsError("error.invalid")
      }
    }

    "must serialise" in {

      val gen = arbitrary[HallmarkE]

      forAll(gen) {
        hallmarkE =>

          Json.toJson(hallmarkE) mustEqual JsString(hallmarkE.toString)
      }
    }
  }
}
