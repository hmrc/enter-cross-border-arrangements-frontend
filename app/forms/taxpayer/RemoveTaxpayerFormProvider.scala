package forms.taxpayer

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form

class RemoveTaxpayerFormProvider @Inject() extends Mappings {

  def apply(): Form[Boolean] =
    Form(
      "value" -> boolean("removeTaxpayer.error.required")
    )
}
