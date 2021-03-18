package forms

import forms.behaviours.BooleanFieldBehaviours
import forms.taxpayer.RemoveTaxpayerFormProvider
import play.api.data.FormError

class RemoveTaxpayerFormProviderSpec extends BooleanFieldBehaviours {

  val requiredKey = "removeTaxpayer.error.required"
  val invalidKey = "error.boolean"

  val form = new RemoveTaxpayerFormProvider()()

  ".value" - {

    val fieldName = "value"

    behave like booleanField(
      form,
      fieldName,
      invalidError = FormError(fieldName, invalidKey)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
