package pages.unsubmitted

import models.{UnsubmittedDisclosure, UserAnswers}
import pages.behaviours.PageBehaviours

class UnsubmittedDisclosurePageSpec extends PageBehaviours {

  "UnsubmittedDisclosurePage" - {

    "must return a valid unsubmitted disclosure from Index" in {

      val unsubmittedDisclosures = Seq(
        UnsubmittedDisclosure("0", "name_0"),
        UnsubmittedDisclosure("1", "name_1", true, true)
      )
      implicit val userAnswers = UserAnswers("internalId")
        .setBase(UnsubmittedDisclosurePage, unsubmittedDisclosures).success.value

      UnsubmittedDisclosurePage.fromIndex(1) mustBe (UnsubmittedDisclosure("1", "name_1", true, true))
    }
  }
}