/*
 * Copyright 2021 HM Revenue & Customs
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

package forms.reporter

import forms.mappings.Mappings
import javax.inject.Inject
import models.Country
import play.api.data.Form

class ReporterTaxResidentCountryFormProvider @Inject() extends Mappings {

  def apply(countryList: Seq[Country]): Form[Country] =
    Form(
      "country" -> text("reporterTaxResidentCountry.error.required")
        .verifying("reporterTaxResidentCountry.error.country.required",
          value => countryList.exists(_.code == value) || value == "GB")
        .transform[Country](value => countryList.find(_.code == value).get, _.code)
    )
}
