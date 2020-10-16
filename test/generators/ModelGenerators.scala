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

  implicit lazy val arbitraryHallmarkC1: Arbitrary[HallmarkC1] =
    Arbitrary {
      Gen.oneOf(HallmarkC1.values.toSeq)
    }

  implicit lazy val arbitraryHallmarkC: Arbitrary[HallmarkC] =
    Arbitrary {
      Gen.oneOf(HallmarkC.values.toSeq)
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

  implicit lazy val arbitraryCountry: Arbitrary[Country] = {
    Arbitrary {
      for {
        state <- Gen.oneOf(Seq("Valid", "Invalid"))
        code  <- Gen.pick(2, 'A' to 'Z')
        name  <- arbitrary[String]
      } yield Country(state, code.mkString, name)
    }
  }

  implicit val arbitraryAddress: Arbitrary[Address] = Arbitrary {
    for {
      addressLine1 <- Gen.option(arbitrary[String])
      addressLine2 <- Gen.option(arbitrary[String])
      addressLine3 <- Gen.option(arbitrary[String])
      city <- arbitrary[String]
      postalCode <- Gen.option(arbitrary[String])
      countryCode <- arbitrary[Country]
    } yield Address(addressLine1, addressLine2, addressLine3, city, postalCode, countryCode)
  }
}
