package pages

import pages.behaviours.PageBehaviours
import pages.taxpayer.RemoveTaxpayerPage

class RemoveTaxpayerPageSpec extends PageBehaviours {

  "RemoveTaxpayerPage" - {

    beRetrievable[Boolean](RemoveTaxpayerPage)

    beSettable[Boolean](RemoveTaxpayerPage)

    beRemovable[Boolean](RemoveTaxpayerPage)
  }
}
