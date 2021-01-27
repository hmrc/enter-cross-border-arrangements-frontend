package pages.affected

import models.intermediaries.YouHaveNotAddedAnyIntermediaries
import pages.affected._

class YouHaveNotAddedAnyAffectedPageSpec {

  "YouHaveNotAddedAnyAffectedPage" - {

    beRetrievable[YouHaveNotAddedAnyAffected](YouHaveNotAddedAnyAffectedPage)

    beSettable[YouHaveNotAddedAnyAffected](YouHaveNotAddedAnyAffectedPage)

    beRemovable[YouHaveNotAddedAnyAffected](YouHaveNotAddedAnyAffectedPage)
  }
}
