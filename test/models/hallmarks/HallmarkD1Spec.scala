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

import base.ModelSpecBase
import generators.ModelGenerators
import org.scalacheck.Arbitrary.arbitrary
import play.api.libs.json.{JsError, JsString, Json}

class HallmarkD1Spec extends ModelSpecBase with ModelGenerators {

  "HallmarkD1" - {

    "must deserialise valid values" in {

      val gen = arbitrary[HallmarkD1]

      forAll(gen) {
        hallmarkD1 =>

          JsString(hallmarkD1.toString).validate[HallmarkD1].asOpt.value mustEqual hallmarkD1
      }
    }

    "must fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!HallmarkD1.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>

          JsString(invalidValue).validate[HallmarkD1] mustEqual JsError("error.invalid")
      }
    }

    "must serialise" in {

      val gen = arbitrary[HallmarkD1]

      forAll(gen) {
        hallmarkD1 =>

          Json.toJson(hallmarkD1) mustEqual JsString(hallmarkD1.toString)
      }
    }
  }
}
