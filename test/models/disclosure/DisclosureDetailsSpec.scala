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

package models.disclosure

import base.ModelSpecBase
import org.scalacheck.Gen
import play.api.libs.json.Json

class DisclosureDetailsSpec extends ModelSpecBase {

  "DisclosureDetails" - {

    "must serialise" in {

      val gen = Gen.oneOf(DisclosureType.values)

      forAll(gen) {
        disclosureType =>
          val disclosureDetails = DisclosureDetails(
            disclosureName = "name",
            disclosureType = disclosureType,
            arrangementID = Some("arrangementID"),
            disclosureID = Some("disclosureID"),
            initialDisclosureMA = true,
            messageRefId = Some("messageRefId")

          )

          Json.toJson(disclosureDetails).toString() mustEqual
            s"""{"disclosureName":"name","disclosureType":"${disclosureType}","arrangementID":"arrangementID","disclosureID":"disclosureID","initialDisclosureMA":true,"messageRefId":"messageRefId"}"""
      }
    }
  }
}
