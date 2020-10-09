package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class OrganisationNameFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "organisationName.error.required"
  val lengthKey = "organisationName.error.length"
  val maxLength = 35

  val form = new OrganisationNameFormProvider()()

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
