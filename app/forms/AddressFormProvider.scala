/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package forms

import javax.inject.Inject
import forms.mappings.Mappings
import models.{Address, Country}
import play.api.data.Form
import play.api.data.Forms.mapping
import utils.RegexConstants

class AddressFormProvider @Inject() extends Mappings with RegexConstants {

  val addressLineLength = 35
  val postCodeLength = 10
  def apply(countryList: Seq[Country]): Form[Address] = Form(
    mapping(
      "addressLine1" ->  validatedOptionalText(
        "address.error.addressLine1.invalid",
        "address.error.addressLine1.length",
        apiAddressRegex,
        addressLineLength
      ),

      "addressLine2" ->  validatedOptionalText(
        "address.error.addressLine2.invalid",
        "address.error.addressLine2.length",
        apiAddressRegex,
        addressLineLength
      ),

      "addressLine3" -> validatedOptionalText("address.error.addressLine3.invalid",
        "address.error.addressLine3.length",
        apiAddressRegex,
        addressLineLength),

      "city" -> validatedText("address.error.city.required", "address.error.city.invalid", "address.error.city.length",
        apiAddressRegex,
        addressLineLength),

      "postCode" -> validatedOptionalText("address.error.postcode.optional.invalid",
        "address.error.postcode.optional.length",
        apiAddressRegex,
        postCodeLength),


      "country" ->  text("address.error.country.required")
        .verifying("address.error.country.required", value => countryList.exists(_.code == value) || value == "GB")
        .transform[Country](value => countryList.find(_.code == value).get, _.code)
    )(Address.apply)(Address.unapply)
  )

}