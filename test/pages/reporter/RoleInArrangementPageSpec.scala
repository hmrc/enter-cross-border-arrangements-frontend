package pages.reporter

import models.reporter.RoleInArrangement
import pages.behaviours.PageBehaviours

class RoleInArrangementSpec extends PageBehaviours {

  "RoleInArrangementPage" - {

    beRetrievable[RoleInArrangement](RoleInArrangementPage)

    beSettable[RoleInArrangement](RoleInArrangementPage)

    beRemovable[RoleInArrangement](RoleInArrangementPage)
  }
}
