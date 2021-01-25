package pages.disclosure

import models.disclosure.ReplaceOrDeleteADisclosure
import pages.behaviours.PageBehaviours

class ReplaceOrDeleteADisclosurePageSpec extends PageBehaviours {

  "ReplaceOrDeleteADisclosurePage" - {

    beRetrievable[ReplaceOrDeleteADisclosure](ReplaceOrDeleteADisclosurePage)

    beSettable[ReplaceOrDeleteADisclosure](ReplaceOrDeleteADisclosurePage)

    beRemovable[ReplaceOrDeleteADisclosure](ReplaceOrDeleteADisclosurePage)
  }
}
