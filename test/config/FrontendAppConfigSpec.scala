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

package config

import base.SpecBase

class FrontendAppConfigSpec extends SpecBase {

  val config: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  "FrontendAppConfigSpec" - {

    "must return correct url for disclosure homepage" in {

      config.discloseArrangeLink mustBe "http://localhost:9758/disclose-cross-border-arrangements/upload"

    }

    "must return correct url for loginContinue" in {

      config.loginContinueUrl mustBe "http://localhost:9762/disclose-cross-border-arrangements/manual"

    }

    "must return correct url for hallmarks" in {

      config.hallmarksUrl mustBe "http://localhost:9762/disclose-cross-border-arrangements/manual/hallmarks/hallmark-category-d"

    }
  }
}
