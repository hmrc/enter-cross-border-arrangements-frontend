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
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

trait ModelGenerators {

  implicit lazy val arbitraryHallmarkE: Arbitrary[HallmarkE] =
    Arbitrary {
      Gen.oneOf(HallmarkE.values.toSeq)
    }

  implicit lazy val arbitraryHallmarkD1: Arbitrary[HallmarkD1] =
    Arbitrary {
      Gen.oneOf(HallmarkD1.values.toSeq)
    }

  implicit lazy val arbitraryHallmarkD: Arbitrary[HallmarkD] =
    Arbitrary {
      Gen.oneOf(HallmarkD.values.toSeq)
    }

  implicit lazy val arbitraryHallmarkB: Arbitrary[HallmarkB] =
    Arbitrary {
      Gen.oneOf(HallmarkB.values.toSeq)
    }

  implicit lazy val arbitraryHallmarkA: Arbitrary[HallmarkA] =
    Arbitrary {
      Gen.oneOf(HallmarkA.values.toSeq)
    }

  implicit lazy val arbitraryHallmarkCategories: Arbitrary[HallmarkCategories] =
    Arbitrary {
      Gen.oneOf(HallmarkCategories.values.toSeq)
    }
}
