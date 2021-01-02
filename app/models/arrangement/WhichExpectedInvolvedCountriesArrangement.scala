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

package models.arrangement

import models.{Enumerable, WithName}
import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels._

sealed trait WhichExpectedInvolvedCountriesArrangement

object WhichExpectedInvolvedCountriesArrangement extends Enumerable.Implicits {

  case object UnitedKingdom extends WithName("GB") with WhichExpectedInvolvedCountriesArrangement
  case object Austria extends WithName("AT") with WhichExpectedInvolvedCountriesArrangement
  case object Belgium extends WithName("BE") with WhichExpectedInvolvedCountriesArrangement
  case object Bulgaria extends WithName("BG") with WhichExpectedInvolvedCountriesArrangement
  case object Croatia extends WithName("HR") with WhichExpectedInvolvedCountriesArrangement
  case object Cyprus extends WithName("CY") with WhichExpectedInvolvedCountriesArrangement
  case object Czechia extends WithName("CZ") with WhichExpectedInvolvedCountriesArrangement
  case object Denmark extends WithName("DK") with WhichExpectedInvolvedCountriesArrangement
  case object Estonia extends WithName("EE") with WhichExpectedInvolvedCountriesArrangement
  case object Finland extends WithName("FI") with WhichExpectedInvolvedCountriesArrangement
  case object France extends WithName("FR") with WhichExpectedInvolvedCountriesArrangement
  case object Germany extends WithName("DE") with WhichExpectedInvolvedCountriesArrangement
  case object Greece extends WithName("GR") with WhichExpectedInvolvedCountriesArrangement
  case object Hungary extends WithName("HU") with WhichExpectedInvolvedCountriesArrangement
  case object Ireland extends WithName("IE") with WhichExpectedInvolvedCountriesArrangement
  case object Italy extends WithName("IT") with WhichExpectedInvolvedCountriesArrangement
  case object Latvia extends WithName("LV") with WhichExpectedInvolvedCountriesArrangement
  case object Lithuania extends WithName("LT") with WhichExpectedInvolvedCountriesArrangement
  case object Luxembourg extends WithName("LU") with WhichExpectedInvolvedCountriesArrangement
  case object Malta extends WithName("MT") with WhichExpectedInvolvedCountriesArrangement
  case object Netherlands extends WithName("NL") with WhichExpectedInvolvedCountriesArrangement
  case object Poland extends WithName("PL") with WhichExpectedInvolvedCountriesArrangement
  case object Portugal extends WithName("PT") with WhichExpectedInvolvedCountriesArrangement
  case object Romania extends WithName("RO") with WhichExpectedInvolvedCountriesArrangement
  case object Slovakia extends WithName("SK") with WhichExpectedInvolvedCountriesArrangement
  case object Slovenia extends WithName("SI") with WhichExpectedInvolvedCountriesArrangement
  case object Spain extends WithName("ES") with WhichExpectedInvolvedCountriesArrangement
  case object Sweden extends WithName("SE") with WhichExpectedInvolvedCountriesArrangement

  val values: Seq[WhichExpectedInvolvedCountriesArrangement] = Seq(
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

  def checkboxes(form: Form[_])(implicit messages: Messages): Seq[Checkboxes.Item] = {

    val field = form("value")
    val items = Seq(
      Checkboxes.Checkbox(msg"whichExpectedInvolvedCountriesArrangement.GB", UnitedKingdom.toString),
      Checkboxes.Checkbox(msg"whichExpectedInvolvedCountriesArrangement.AT", Austria.toString),
      Checkboxes.Checkbox(msg"whichExpectedInvolvedCountriesArrangement.BE", Belgium.toString),
      Checkboxes.Checkbox(msg"whichExpectedInvolvedCountriesArrangement.BG", Bulgaria.toString),
      Checkboxes.Checkbox(msg"whichExpectedInvolvedCountriesArrangement.HR", Croatia.toString),
      Checkboxes.Checkbox(msg"whichExpectedInvolvedCountriesArrangement.CY", Cyprus.toString),
      Checkboxes.Checkbox(msg"whichExpectedInvolvedCountriesArrangement.CZ", Czechia.toString),
      Checkboxes.Checkbox(msg"whichExpectedInvolvedCountriesArrangement.DK", Denmark.toString),
      Checkboxes.Checkbox(msg"whichExpectedInvolvedCountriesArrangement.EE", Estonia.toString),
      Checkboxes.Checkbox(msg"whichExpectedInvolvedCountriesArrangement.FI", Finland.toString),
      Checkboxes.Checkbox(msg"whichExpectedInvolvedCountriesArrangement.FR", France.toString),
      Checkboxes.Checkbox(msg"whichExpectedInvolvedCountriesArrangement.DE", Germany.toString),
      Checkboxes.Checkbox(msg"whichExpectedInvolvedCountriesArrangement.GR", Greece.toString),
      Checkboxes.Checkbox(msg"whichExpectedInvolvedCountriesArrangement.HU", Hungary.toString),
      Checkboxes.Checkbox(msg"whichExpectedInvolvedCountriesArrangement.IE", Ireland.toString),
      Checkboxes.Checkbox(msg"whichExpectedInvolvedCountriesArrangement.IT", Italy.toString),
      Checkboxes.Checkbox(msg"whichExpectedInvolvedCountriesArrangement.LV", Latvia.toString),
      Checkboxes.Checkbox(msg"whichExpectedInvolvedCountriesArrangement.LT", Lithuania.toString),
      Checkboxes.Checkbox(msg"whichExpectedInvolvedCountriesArrangement.LU", Luxembourg.toString),
      Checkboxes.Checkbox(msg"whichExpectedInvolvedCountriesArrangement.MT", Malta.toString),
      Checkboxes.Checkbox(msg"whichExpectedInvolvedCountriesArrangement.NL", Netherlands.toString),
      Checkboxes.Checkbox(msg"whichExpectedInvolvedCountriesArrangement.PL", Poland.toString),
      Checkboxes.Checkbox(msg"whichExpectedInvolvedCountriesArrangement.PT", Portugal.toString),
      Checkboxes.Checkbox(msg"whichExpectedInvolvedCountriesArrangement.RO", Romania.toString),
      Checkboxes.Checkbox(msg"whichExpectedInvolvedCountriesArrangement.SK", Slovakia.toString),
      Checkboxes.Checkbox(msg"whichExpectedInvolvedCountriesArrangement.SI", Slovenia.toString),
      Checkboxes.Checkbox(msg"whichExpectedInvolvedCountriesArrangement.ES", Spain.toString),
      Checkboxes.Checkbox(msg"whichExpectedInvolvedCountriesArrangement.SE", Sweden.toString)

    )

    Checkboxes.set(field, items)
  }

  implicit val enumerable: Enumerable[WhichExpectedInvolvedCountriesArrangement] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
