package pages.enterprises

import models.enterprises.SelectAnyTaxpayersThisEnterpriseIsAssociatedWith
import pages.behaviours.PageBehaviours

class SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPageSpec extends PageBehaviours {

  "SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage" - {

    beRetrievable[Set[SelectAnyTaxpayersThisEnterpriseIsAssociatedWith]](SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage)

    beSettable[Set[SelectAnyTaxpayersThisEnterpriseIsAssociatedWith]](SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage)

    beRemovable[Set[SelectAnyTaxpayersThisEnterpriseIsAssociatedWith]](SelectAnyTaxpayersThisEnterpriseIsAssociatedWithPage)
  }
}
