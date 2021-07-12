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

import models.CountryList.UnitedKingdom
import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels._

sealed trait CountryList extends Ordered[CountryList] {

  override def compare(that: CountryList): Int =
    (this, that) match {
      case (UnitedKingdom, _) => Int.MinValue
      case (_, UnitedKingdom) => Int.MaxValue
      case (country, other)   => country.name.compareTo(other.name)
    }

  val name: String = getClass.getSimpleName
}

object CountryList extends Enumerable.Implicits {

  case object UnitedKingdom extends WithName("GB") with CountryList
  case object Austria extends WithName("AT") with CountryList
  case object Belgium extends WithName("BE") with CountryList
  case object Bulgaria extends WithName("BG") with CountryList
  case object Croatia extends WithName("HR") with CountryList
  case object Cyprus extends WithName("CY") with CountryList
  case object Czechia extends WithName("CZ") with CountryList
  case object Denmark extends WithName("DK") with CountryList
  case object Estonia extends WithName("EE") with CountryList
  case object Finland extends WithName("FI") with CountryList
  case object France extends WithName("FR") with CountryList
  case object Germany extends WithName("DE") with CountryList
  case object Greece extends WithName("GR") with CountryList
  case object Hungary extends WithName("HU") with CountryList
  case object Ireland extends WithName("IE") with CountryList
  case object Italy extends WithName("IT") with CountryList
  case object Latvia extends WithName("LV") with CountryList
  case object Lithuania extends WithName("LT") with CountryList
  case object Luxembourg extends WithName("LU") with CountryList
  case object Malta extends WithName("MT") with CountryList
  case object Netherlands extends WithName("NL") with CountryList
  case object Poland extends WithName("PL") with CountryList
  case object Portugal extends WithName("PT") with CountryList
  case object Romania extends WithName("RO") with CountryList
  case object Slovakia extends WithName("SK") with CountryList
  case object Slovenia extends WithName("SI") with CountryList
  case object Spain extends WithName("ES") with CountryList
  case object Sweden extends WithName("SE") with CountryList

  val values: Seq[CountryList] = Seq(
    UnitedKingdom,
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

  val items = Seq(
    Checkboxes.Checkbox(msg"countriesListCheckboxes.GB", UnitedKingdom.toString),
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

  def checkboxes(form: Form[_])(implicit messages: Messages): Seq[Checkboxes.Item] = {
    val field = form("value")
    Checkboxes.set(field, items)
  }

  def nonGBCheckboxes(form: Form[_])(implicit messages: Messages): Seq[Checkboxes.Item] = {
    val field = form("value")
    //Removes the first element from the items list (GB)
    Checkboxes.set(field, items.drop(1))
  }

  implicit val enumerable: Enumerable[CountryList] =
    Enumerable(
      values.map(
        v => v.toString -> v
      ): _*
    )
}
