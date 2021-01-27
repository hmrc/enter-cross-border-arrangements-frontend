package pages.affected

class AffectedTypePageSpec {

  "AffectedTypePage" - {

    beRetrievable[SelectType](AffectedTypePage)

    beSettable[SelectType](AffectedTypePage)

    beRemovable[SelectType](AffectedTypePage)
  }
}