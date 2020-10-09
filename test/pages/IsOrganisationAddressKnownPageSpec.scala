package pages

import pages.behaviours.PageBehaviours

class IsOrganisationAddressKnownPageSpec extends PageBehaviours {

  "IsOrganisationAddressKnownPage" - {

    beRetrievable[Boolean](IsOrganisationAddressKnownPage)

    beSettable[Boolean](IsOrganisationAddressKnownPage)

    beRemovable[Boolean](IsOrganisationAddressKnownPage)
  }
}
