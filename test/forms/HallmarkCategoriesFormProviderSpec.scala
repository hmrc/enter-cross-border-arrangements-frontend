package forms

import forms.behaviours.CheckboxFieldBehaviours
import models.HallmarkCategories
import play.api.data.FormError

class HallmarkCategoriesFormProviderSpec extends CheckboxFieldBehaviours {

  val form = new HallmarkCategoriesFormProvider()()

  ".value" - {

    val fieldName = "value"
    val requiredKey = "hallmarkCategories.error.required"

    behave like checkboxField[HallmarkCategories](
      form,
      fieldName,
      validValues  = HallmarkCategories.values,
      invalidError = FormError(s"$fieldName[0]", "error.invalid")
    )

    behave like mandatoryCheckboxField(
      form,
      fieldName,
      requiredKey
    )
  }
}
