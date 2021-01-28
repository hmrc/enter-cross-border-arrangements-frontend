package controllers.intermediaries

import base.SpecBase
import models.intermediaries.{Intermediary, WhatTypeofIntermediary}
import models.organisation.Organisation
import models.taxpayer.TaxResidency
import models.{Address, Country, IsExemptionKnown, LoopDetails, SelectType, TaxReferenceNumbers, UnsubmittedDisclosure, UserAnswers}
import org.scalatestplus.mockito.MockitoSugar
import pages.intermediaries.{IntermediariesTypePage, IntermediaryLoopPage, IsExemptionKnownPage, WhatTypeofIntermediaryPage}
import pages.organisation.{OrganisationLoopPage, OrganisationNamePage}
import pages.unsubmitted.UnsubmittedDisclosurePage

class IntermediariesCheckYourAnswersControllerSpec extends SpecBase with MockitoSugar {

  "must ensure the correct updated loop list" - {

    val address: Address = Address(Some(""), Some(""), Some(""), "Newcastle", Some("NE1"), Country("", "GB", "United Kingdom"))
    val email = "email@email.com"
    val taxResidencies = IndexedSeq(TaxResidency(Some(Country("", "GB", "United Kingdom")), Some(TaxReferenceNumbers("UTR1234", None, None))))

    def buildUserAnswers(list: IndexedSeq[Intermediary]): UserAnswers = UserAnswers(userAnswersId)
      .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First"))).success.value
      .set(IntermediariesTypePage, 0, SelectType.Organisation).success.value
      .set(OrganisationNamePage, 0, "Intermediary Ltd").success.value
      .set(WhatTypeofIntermediaryPage, 0, WhatTypeofIntermediary.IDoNotKnow).success.value
      .set(IsExemptionKnownPage,0, IsExemptionKnown.Unknown).success.value
      .set(OrganisationLoopPage, 0, IndexedSeq(LoopDetails(None, Some(Country("","GB","United Kingdom")), None, None, None, None))).success.value
      .set(IntermediaryLoopPage, 0, list).success.value

    val controller: IntermediariesCheckYourAnswersController = injector.instanceOf[IntermediariesCheckYourAnswersController]

    def organisation(name: String) = Organisation(name, Some(address), Some(email), taxResidencies)

    def buildIntermediary(id: String, name: String) =
      Intermediary(id, None, Some(organisation(name)), WhatTypeofIntermediary.IDoNotKnow, IsExemptionKnown.Unknown)

    "if ids are not duplicated" in {

      val list1: IndexedSeq[Intermediary] = IndexedSeq(
        buildIntermediary("ID1", "First Ltd"), buildIntermediary("ID2", "Second Ltd")
      )

      controller.updatedLoopList(buildUserAnswers(list1), 0).map(_.nameAsString) must contain theSameElementsAs(list1).map(_.nameAsString) :+ "Intermediary Ltd"
    }

    "if ids are duplicated" in {

      val list1: IndexedSeq[Intermediary] = IndexedSeq(
        buildIntermediary("ID1", "Intermediary Ltd"), buildIntermediary("ID2", "Second Ltd")
      )

      controller.updatedLoopList(buildUserAnswers(list1), 0).map(_.nameAsString) must contain theSameElementsAs(list1).map(_.nameAsString)
    }
  }

}
