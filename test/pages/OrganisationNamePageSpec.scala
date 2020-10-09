package pages

import pages.behaviours.PageBehaviours


class OrganisationNamePageSpec extends PageBehaviours {

  "OrganisationNamePage" - {

    beRetrievable[String](OrganisationNamePage)

    beSettable[String](OrganisationNamePage)

    beRemovable[String](OrganisationNamePage)
  }
}
