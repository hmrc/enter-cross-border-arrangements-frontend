package forms

import forms.behaviours.CheckboxFieldBehaviours
import models.HallmarkC
import play.api.data.FormError

class HallmarkCFormProviderSpec extends CheckboxFieldBehaviours {

  val form = new HallmarkCFormProvider()()

  ".value" - {

    val fieldName = "value"
    val requiredKey = "hallmarkC.error.required"

    behave like checkboxField[HallmarkC](
      form,
      fieldName,
      validValues  = HallmarkC.values,
      invalidError = FormError(s"$fieldName[0]", "error.invalid")
    )

    behave like mandatoryCheckboxField(
      form,
      fieldName,
      requiredKey
    )
  }
}
