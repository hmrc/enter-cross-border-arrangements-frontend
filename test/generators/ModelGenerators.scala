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

package generators

import models._
import models.affected.YouHaveNotAddedAnyAffected
import models.arrangement.{ExpectedArrangementValue, WhyAreYouReportingThisArrangementNow}
import models.disclosure.{DisclosureType, ReplaceOrDeleteADisclosure}
import models.enterprises.YouHaveNotAddedAnyAssociatedEnterprises
import models.hallmarks._
import models.intermediaries.YouHaveNotAddedAnyIntermediaries
import models.reporter.RoleInArrangement
import models.reporter.intermediary.{IntermediaryRole, IntermediaryWhyReportInUK}
import models.reporter.taxpayer.{TaxpayerWhyReportArrangement, TaxpayerWhyReportInUK}
import models.taxpayer.UpdateTaxpayer
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

trait ModelGenerators {

  implicit lazy val arbitraryGeneratedIDs: Arbitrary[GeneratedIDs] =
    Arbitrary {
      for {
        arrangementID <- Gen.option(arbitrary[String])
        disclosureID  <- Gen.option(arbitrary[String])
      } yield GeneratedIDs(arrangementID, disclosureID)
    }

  implicit lazy val arbitraryReplaceOrDeleteADisclosure: Arbitrary[ReplaceOrDeleteADisclosure] =
    Arbitrary {
      for {
        arrangementID <- arbitrary[String]
        disclosureID <- arbitrary[String]
      } yield ReplaceOrDeleteADisclosure(arrangementID, disclosureID)
    }

  implicit lazy val arbitraryReporterOrganisationOrIndividual: Arbitrary[ReporterOrganisationOrIndividual] =
    Arbitrary {
      Gen.oneOf(ReporterOrganisationOrIndividual.values)
    }
  import models.intermediaries.WhatTypeofIntermediary

  implicit lazy val arbitraryWhatTypeofIntermediary: Arbitrary[WhatTypeofIntermediary] =
    Arbitrary {
      Gen.oneOf(WhatTypeofIntermediary.values)
    }

  implicit lazy val arbitraryYouHaveNotAddedAnyIntermediaries: Arbitrary[YouHaveNotAddedAnyIntermediaries] =
    Arbitrary {
      Gen.oneOf(YouHaveNotAddedAnyIntermediaries.values)
    }

  implicit lazy val arbitraryYouHaveNotAddedAnyAffected: Arbitrary[YouHaveNotAddedAnyAffected] =
    Arbitrary {
      Gen.oneOf(YouHaveNotAddedAnyAffected.values)
    }

  implicit lazy val arbitraryIsExemptionKnown: Arbitrary[IsExemptionKnown] =
    Arbitrary {
      Gen.oneOf(IsExemptionKnown.values)
    }

  implicit lazy val arbitraryDisclosureType: Arbitrary[DisclosureType] =
    Arbitrary {
      Gen.oneOf(DisclosureType.values)
    }

  implicit lazy val arbitraryTaxpayerWhyReportArrangement: Arbitrary[TaxpayerWhyReportArrangement] =
    Arbitrary {
      Gen.oneOf(TaxpayerWhyReportArrangement.values)
    }

  implicit lazy val arbitraryTaxpayerWhyReportInUK: Arbitrary[TaxpayerWhyReportInUK] =
    Arbitrary {
      Gen.oneOf(TaxpayerWhyReportInUK.values)
    }

  implicit lazy val arbitraryIntermediaryExemptionInEU: Arbitrary[YesNoDoNotKnowRadios] =
    Arbitrary {
      Gen.oneOf(YesNoDoNotKnowRadios.values)
    }

  implicit lazy val arbitraryIntermediaryRole: Arbitrary[IntermediaryRole] =
    Arbitrary {
      Gen.oneOf(IntermediaryRole.values)
    }

  implicit lazy val arbitraryWhyReportInUK: Arbitrary[IntermediaryWhyReportInUK] =
    Arbitrary {
      Gen.oneOf(IntermediaryWhyReportInUK.values)
    }

  implicit lazy val arbitraryRoleInArrangement: Arbitrary[RoleInArrangement] =
    Arbitrary {
      Gen.oneOf(RoleInArrangement.values)
    }

  implicit lazy val arbitrarySelectType: Arbitrary[SelectType] =
    Arbitrary {
      Gen.oneOf(SelectType.values)
    }

  implicit lazy val arbitraryUpdateTaxpayer: Arbitrary[UpdateTaxpayer] =
    Arbitrary {
      Gen.oneOf(UpdateTaxpayer.values)
    }

  implicit lazy val arbitraryYouHaveNotAddedAnyAssociatedEnterprises: Arbitrary[YouHaveNotAddedAnyAssociatedEnterprises] =
    Arbitrary {
      Gen.oneOf(YouHaveNotAddedAnyAssociatedEnterprises.values)
    }

  implicit lazy val arbitraryWhatIsTheExpectedValueOfThisArrangement: Arbitrary[ExpectedArrangementValue] =
    Arbitrary {
      for {
        currency <- arbitrary[String]
        amount <- arbitrary[Int]
      } yield ExpectedArrangementValue(currency, amount)
    }

implicit lazy val arbitraryCountryList: Arbitrary[CountryList] =
    Arbitrary {
      Gen.oneOf(CountryList.values)
    }

  implicit lazy val arbitraryWhyAreYouReportingThisArrangementNow: Arbitrary[WhyAreYouReportingThisArrangementNow] =
    Arbitrary {
      Gen.oneOf(WhyAreYouReportingThisArrangementNow.values)
    }

  implicit lazy val arbitraryHallmarkD1: Arbitrary[HallmarkD1] =
    Arbitrary {
      Gen.oneOf(HallmarkD1.values)
    }

  implicit lazy val arbitraryHallmarkD: Arbitrary[HallmarkD] =
    Arbitrary {
      Gen.oneOf(HallmarkD.values)
    }

  implicit lazy val arbitraryHallmarkCategories: Arbitrary[HallmarkCategories] =
    Arbitrary {
      Gen.oneOf(HallmarkCategories.values)
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

  implicit val arbitraryName: Arbitrary[Name] = Arbitrary {
    for {
      firstName <- arbitrary[String]
      secondName <- arbitrary[String]
    } yield Name(firstName, secondName)
  }

  implicit val arbitraryTaxReferenceNumbers: Arbitrary[TaxReferenceNumbers] = Arbitrary {
    for {
      firstTaxNumber <- arbitrary[String]
      secondTaxNumber <- Gen.option(arbitrary[String])
      thirdTaxNumber <- Gen.option(arbitrary[String])
    } yield TaxReferenceNumbers(firstTaxNumber, secondTaxNumber, thirdTaxNumber)
  }

  implicit val arbitraryOrganisationLoopDetails: Arbitrary[IndexedSeq[LoopDetails]] = Arbitrary {
    for {
      taxResidentOtherCountries <- Gen.option(arbitrary[Boolean])
      whichCountry <- Gen.option(arbitrary[Country])
      doYouKnowTIN <- Gen.option(arbitrary[Boolean])
      taxNumbersNonUK <- Gen.option(arbitrary[TaxReferenceNumbers])
      doYouKnowUTR <- Gen.option(arbitrary[Boolean])
      taxNumbersUK <- Gen.option(arbitrary[TaxReferenceNumbers])
    } yield IndexedSeq(LoopDetails(taxResidentOtherCountries, whichCountry, doYouKnowTIN, taxNumbersNonUK, doYouKnowUTR, taxNumbersUK))
  }

}
