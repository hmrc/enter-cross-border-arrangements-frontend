package pages.taxpayer

import pages.behaviours.PageBehaviours

class RemoveTaxpayerPageSpec extends PageBehaviours {

  "RemoveTaxpayerPage" - {

    beRetrievable[Boolean](RemoveTaxpayerPage)

    beSettable[Boolean](RemoveTaxpayerPage)

    beRemovable[Boolean](RemoveTaxpayerPage)
  }
}
