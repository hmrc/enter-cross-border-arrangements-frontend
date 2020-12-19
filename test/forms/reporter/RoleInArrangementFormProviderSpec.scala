package forms.reporter

import forms.behaviours.OptionFieldBehaviours
import models.reporter.RoleInArrangement
import play.api.data.FormError

class RoleInArrangementFormProviderSpec extends OptionFieldBehaviours {

  val form = new RoleInArrangementFormProvider()()

  ".value" - {

    val fieldName = "value"
    val requiredKey = "roleInArrangement.error.required"

    behave like optionsField[RoleInArrangement](
      form,
      fieldName,
      validValues  = RoleInArrangement.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
