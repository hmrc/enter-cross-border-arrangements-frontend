package pages

import models.HallmarkCategories
import pages.behaviours.PageBehaviours

class HallmarkCategoriesPageSpec extends PageBehaviours {

  "HallmarkCategoriesPage" - {

    beRetrievable[Set[HallmarkCategories]](HallmarkCategoriesPage)

    beSettable[Set[HallmarkCategories]](HallmarkCategoriesPage)

    beRemovable[Set[HallmarkCategories]](HallmarkCategoriesPage)
  }
}
