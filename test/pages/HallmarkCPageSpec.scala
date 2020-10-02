package pages

import models.HallmarkC
import pages.behaviours.PageBehaviours

class HallmarkCPageSpec extends PageBehaviours {

  "HallmarkCPage" - {

    beRetrievable[Set[HallmarkC]](HallmarkCPage)

    beSettable[Set[HallmarkC]](HallmarkCPage)

    beRemovable[Set[HallmarkC]](HallmarkCPage)
  }
}
