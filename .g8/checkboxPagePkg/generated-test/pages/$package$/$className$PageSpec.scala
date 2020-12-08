package pages.$package$

import models.$package$.$className$
import pages.behaviours.PageBehaviours

class $className$PageSpec extends PageBehaviours {

  "$className$Page" - {

    beRetrievable[Set[$className$]]($className$Page)

    beSettable[Set[$className$]]($className$Page)

    beRemovable[Set[$className$]]($className$Page)
  }
}
