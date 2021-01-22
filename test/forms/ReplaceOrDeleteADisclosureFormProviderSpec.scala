package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class ReplaceOrDeleteADisclosureFormProviderSpec extends StringFieldBehaviours {

  val form = new ReplaceOrDeleteADisclosureFormProvider()()

  ".arrangementID" - {

    val fieldName = "arrangementID"
    val requiredKey = "replaceOrDeleteADisclosure.error.arrangementID.required"
    val lengthKey = "replaceOrDeleteADisclosure.error.arrangementID.length"
    val maxLength = 20

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

  ".disclosureID" - {

    val fieldName = "disclosureID"
    val requiredKey = "replaceOrDeleteADisclosure.error.disclosureID.required"
    val lengthKey = "replaceOrDeleteADisclosure.error.disclosureID.length"
    val maxLength = 20

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
