package pages

import models.ReplaceOrDeleteADisclosure
import pages.behaviours.PageBehaviours

class ReplaceOrDeleteADisclosurePageSpec extends PageBehaviours {

  "ReplaceOrDeleteADisclosurePage" - {

    beRetrievable[ReplaceOrDeleteADisclosure](ReplaceOrDeleteADisclosurePage)

    beSettable[ReplaceOrDeleteADisclosure](ReplaceOrDeleteADisclosurePage)

    beRemovable[ReplaceOrDeleteADisclosure](ReplaceOrDeleteADisclosurePage)
  }
}
