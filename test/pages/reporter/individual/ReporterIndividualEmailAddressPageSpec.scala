package pages.reporter.individual

import pages.behaviours.PageBehaviours

class ReporterIndividualEmailAddressPageSpec extends PageBehaviours {

  "ReporterIndividualEmailAddressPage" - {

    beRetrievable[Boolean](ReporterIndividualEmailAddressPage)

    beSettable[Boolean](ReporterIndividualEmailAddressPage)

    beRemovable[Boolean](ReporterIndividualEmailAddressPage)
  }
}
