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
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito._
import play.api.Configuration
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import scala.concurrent.Future

class FrontendAppConfigSpec extends SpecBase {

  val config: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  "FrontendAppConfigSpec" - {

    "must return correct url for disclosure homepage" in {

      config.discloseArrangeLink mustBe "http://localhost:9758/disclose-cross-border-arrangements"

    }

    "must return correct url for loginContinue" in {

      config.loginContinueUrl mustBe "http://localhost:9762/enter-cross-border-arrangements"

    }

    "must return correct url for hallmarks" in {

      config.hallmarksUrl mustBe "http://localhost:9762/enter-cross-border-arrangements/hallmarks/hallmark-category-d"

    }
  }
}
