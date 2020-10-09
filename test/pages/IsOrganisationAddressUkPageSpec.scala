package pages

import pages.behaviours.PageBehaviours

class IsOrganisationAddressUkPageSpec extends PageBehaviours {

  "iIsOrganisationAddressUkPage" - {

    beRetrievable[Boolean](IsOrganisationAddressUkPage)

    beSettable[Boolean](IsOrganisationAddressUkPage)

    beRemovable[Boolean](IsOrganisationAddressUkPage)
  }
}
