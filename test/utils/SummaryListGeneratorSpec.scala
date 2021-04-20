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

package utils

import base.SpecBase
import models.disclosure.DisclosureDetails
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels.Text.Literal

class SummaryListGeneratorSpec extends SpecBase with SummaryImplicits {

  val dis: DisclosureDetails = DisclosureDetails("aName")


  "SummaryListGenerator " - {

    val sl = injector.instanceOf[SummaryListGenerator]

    "should produce valid disclosure list summary" in {
     val summary =  sl.generateSummaryList(0, dis)
      summary.length mustBe 3
      summary.head.value.content mustBe Literal("aName")
    }

  }
}
