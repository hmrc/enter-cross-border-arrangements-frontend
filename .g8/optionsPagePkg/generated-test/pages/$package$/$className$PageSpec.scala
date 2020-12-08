package pages.$package$

import models.$package$.$className$
import pages.behaviours.PageBehaviours

class $className$Spec extends PageBehaviours {

  "$className$Page" - {

    beRetrievable[$className$]($className$Page)

    beSettable[$className$]($className$Page)

    beRemovable[$className$]($className$Page)
  }
}
