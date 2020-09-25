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

package generators

import models._
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import pages._
import play.api.libs.json.{JsValue, Json}

trait UserAnswersEntryGenerators extends PageGenerators with ModelGenerators {

  implicit lazy val arbitraryHallmarkBUserAnswersEntry: Arbitrary[(HallmarkBPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[HallmarkBPage.type]
        value <- arbitrary[HallmarkB].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryMeetMainBenefitTestUserAnswersEntry: Arbitrary[(MainBenefitTestPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[MainBenefitTestPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryHallmarkAUserAnswersEntry: Arbitrary[(HallmarkAPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[HallmarkAPage.type]
        value <- arbitrary[HallmarkA].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryHallmarkCategoriesUserAnswersEntry: Arbitrary[(HallmarkCategoriesPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[HallmarkCategoriesPage.type]
        value <- arbitrary[HallmarkCategories].map(Json.toJson(_))
      } yield (page, value)
    }
}