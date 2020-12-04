package forms.enterprises

import forms.behaviours.CheckboxFieldBehaviours
import models.enterprises.SelectAnyTaxpayersThisEnterpriseIsAssociatedWith
import play.api.data.FormError

class SelectAnyTaxpayersThisEnterpriseIsAssociatedWithFormProviderSpec extends CheckboxFieldBehaviours {

  val form = new SelectAnyTaxpayersThisEnterpriseIsAssociatedWithFormProvider()()

  ".value" - {

    val fieldName = "value"
    val requiredKey = "selectAnyTaxpayersThisEnterpriseIsAssociatedWith.error.required"

    behave like checkboxField[SelectAnyTaxpayersThisEnterpriseIsAssociatedWith](
      form,
      fieldName,
      validValues  = SelectAnyTaxpayersThisEnterpriseIsAssociatedWith.values,
      invalidError = FormError(s"$fieldName[0]", "error.invalid")
    )

    behave like mandatoryCheckboxField(
      form,
      fieldName,
      requiredKey
    )
  }
}
