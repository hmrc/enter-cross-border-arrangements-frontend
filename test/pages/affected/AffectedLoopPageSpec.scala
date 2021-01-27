package pages.affected

import models.affected._
import pages.organisation.OrganisationLoopPage

class AffectedLoopPageSpec {

  "AffectedLoopPage" - {

    beRetrievable[IndexedSeq[Affected]](AffectedLoopPage)

    beSettable[IndexedSeq[Affected]](AffectedLoopPage)

    beRemovable[IndexedSeq[Affected]](AffectedLoopPage)
  }
