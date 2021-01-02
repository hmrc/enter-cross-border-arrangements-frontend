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

package models

import play.api.data.Form
import uk.gov.hmrc.viewmodels._

sealed trait CountriesListEUCheckboxes

object CountriesListEUCheckboxes extends Enumerable.Implicits {

  case object Austria extends WithName("AT") with CountriesListEUCheckboxes
  case object Belgium extends WithName("BE") with CountriesListEUCheckboxes
  case object Bulgaria extends WithName("BG") with CountriesListEUCheckboxes
  case object Croatia extends WithName("HR") with CountriesListEUCheckboxes
  case object Cyprus extends WithName("CY") with CountriesListEUCheckboxes
  case object Czechia extends WithName("CZ") with CountriesListEUCheckboxes
  case object Denmark extends WithName("DK") with CountriesListEUCheckboxes
  case object Estonia extends WithName("EE") with CountriesListEUCheckboxes
  case object Finland extends WithName("FI") with CountriesListEUCheckboxes
  case object France extends WithName("FR") with CountriesListEUCheckboxes
  case object Germany extends WithName("DE") with CountriesListEUCheckboxes
  case object Greece extends WithName("GR") with CountriesListEUCheckboxes
  case object Hungary extends WithName("HU") with CountriesListEUCheckboxes
  case object Ireland extends WithName("IE") with CountriesListEUCheckboxes
  case object Italy extends WithName("IT") with CountriesListEUCheckboxes
  case object Latvia extends WithName("LV") with CountriesListEUCheckboxes
  case object Lithuania extends WithName("LT") with CountriesListEUCheckboxes
  case object Luxembourg extends WithName("LU") with CountriesListEUCheckboxes
  case object Malta extends WithName("MT") with CountriesListEUCheckboxes
  case object Netherlands extends WithName("NL") with CountriesListEUCheckboxes
  case object Poland extends WithName("PL") with CountriesListEUCheckboxes
  case object Portugal extends WithName("PT") with CountriesListEUCheckboxes
  case object Romania extends WithName("RO") with CountriesListEUCheckboxes
  case object Slovakia extends WithName("SK") with CountriesListEUCheckboxes
  case object Slovenia extends WithName("SI") with CountriesListEUCheckboxes
  case object Spain extends WithName("ES") with CountriesListEUCheckboxes
  case object Sweden extends WithName("SE") with CountriesListEUCheckboxes

  val values: Seq[CountriesListEUCheckboxes] = Seq(
    Austria,
    Belgium,
    Bulgaria,
    Croatia,
    Cyprus,
    Czechia,
    Denmark,
    Estonia,
    Finland,
    France,
    Germany,
    Greece,
    Hungary,
    Ireland,
    Italy,
    Latvia,
    Lithuania,
    Luxembourg,
    Malta,
    Netherlands,
    Poland,
    Portugal,
    Romania,
    Slovakia,
    Slovenia,
    Spain,
    Sweden
  )

  def checkboxes(form: Form[_]): Seq[Checkboxes.Item] = {

    val field = form("value")
    val items = Seq(
      Checkboxes.Checkbox(msg"countriesListCheckboxes.AT", Austria.toString),
      Checkboxes.Checkbox(msg"countriesListCheckboxes.BE", Belgium.toString),
      Checkboxes.Checkbox(msg"countriesListCheckboxes.BG", Bulgaria.toString),
      Checkboxes.Checkbox(msg"countriesListCheckboxes.HR", Croatia.toString),
      Checkboxes.Checkbox(msg"countriesListCheckboxes.CY", Cyprus.toString),
      Checkboxes.Checkbox(msg"countriesListCheckboxes.CZ", Czechia.toString),
      Checkboxes.Checkbox(msg"countriesListCheckboxes.DK", Denmark.toString),
      Checkboxes.Checkbox(msg"countriesListCheckboxes.EE", Estonia.toString),
      Checkboxes.Checkbox(msg"countriesListCheckboxes.FI", Finland.toString),
      Checkboxes.Checkbox(msg"countriesListCheckboxes.FR", France.toString),
      Checkboxes.Checkbox(msg"countriesListCheckboxes.DE", Germany.toString),
      Checkboxes.Checkbox(msg"countriesListCheckboxes.GR", Greece.toString),
      Checkboxes.Checkbox(msg"countriesListCheckboxes.HU", Hungary.toString),
      Checkboxes.Checkbox(msg"countriesListCheckboxes.IE", Ireland.toString),
      Checkboxes.Checkbox(msg"countriesListCheckboxes.IT", Italy.toString),
      Checkboxes.Checkbox(msg"countriesListCheckboxes.LV", Latvia.toString),
      Checkboxes.Checkbox(msg"countriesListCheckboxes.LT", Lithuania.toString),
      Checkboxes.Checkbox(msg"countriesListCheckboxes.LU", Luxembourg.toString),
      Checkboxes.Checkbox(msg"countriesListCheckboxes.MT", Malta.toString),
      Checkboxes.Checkbox(msg"countriesListCheckboxes.NL", Netherlands.toString),
      Checkboxes.Checkbox(msg"countriesListCheckboxes.PL", Poland.toString),
      Checkboxes.Checkbox(msg"countriesListCheckboxes.PT", Portugal.toString),
      Checkboxes.Checkbox(msg"countriesListCheckboxes.RO", Romania.toString),
      Checkboxes.Checkbox(msg"countriesListCheckboxes.SK", Slovakia.toString),
      Checkboxes.Checkbox(msg"countriesListCheckboxes.SI", Slovenia.toString),
      Checkboxes.Checkbox(msg"countriesListCheckboxes.ES", Spain.toString),
      Checkboxes.Checkbox(msg"countriesListCheckboxes.SE", Sweden.toString)
    )

    Checkboxes.set(field, items)
  }

  implicit val enumerable: Enumerable[CountriesListEUCheckboxes] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
