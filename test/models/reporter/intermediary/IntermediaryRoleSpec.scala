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

package models.reporter.intermediary

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsError, JsString, Json}

class IntermediaryRoleSpec extends FreeSpec with MustMatchers with ScalaCheckPropertyChecks with OptionValues {

  "IntermediaryRole" - {

    "must deserialise valid values" in {

      val gen = Gen.oneOf(IntermediaryRole.values)

      forAll(gen) {
        intermediaryRole =>

          JsString(intermediaryRole.toString).validate[IntermediaryRole].asOpt.value mustEqual intermediaryRole
      }
    }

    "must fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!IntermediaryRole.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>

          JsString(invalidValue).validate[IntermediaryRole] mustEqual JsError("error.invalid")
      }
    }

    "must serialise" in {

      val gen = Gen.oneOf(IntermediaryRole.values)

      forAll(gen) {
        intermediaryRole =>

          Json.toJson(intermediaryRole) mustEqual JsString(intermediaryRole.toString)
      }
    }
  }
}
