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

package models

import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels._

sealed trait ExemptCountries

object ExemptCountries extends Enumerable.Implicits {

  case object UnitedKingdom extends WithName("unitedKingdom") with ExemptCountries
  case object Austria extends WithName("austria") with ExemptCountries
  case object Belgium extends WithName("belgium") with ExemptCountries
  case object Bulgaria extends WithName("bulgaria") with ExemptCountries
  case object Cyprus extends WithName("cyprus") with ExemptCountries
  case object Croatia extends WithName("croatia") with ExemptCountries
  case object Czechia extends WithName("czechia") with ExemptCountries
  case object Denmark extends WithName("denmark") with ExemptCountries
  case object Estonia extends WithName("estonia") with ExemptCountries
  case object Finland extends WithName("finland") with ExemptCountries
  case object France extends WithName("france") with ExemptCountries
  case object Germany extends WithName("germany") with ExemptCountries
  case object Greece extends WithName("greece") with ExemptCountries
  case object Hungary extends WithName("hungary") with ExemptCountries
  case object Ireland extends WithName("ireland") with ExemptCountries
  case object Italy extends WithName("italy") with ExemptCountries
  case object Latvia extends WithName("latvia") with ExemptCountries
  case object Lithuania extends WithName("lithuania") with ExemptCountries
  case object Luxembourg extends WithName("luxembourg") with ExemptCountries
  case object Malta extends WithName("malta") with ExemptCountries
  case object Netherlands extends WithName("netherlands") with ExemptCountries
  case object Poland extends WithName("poland") with ExemptCountries
  case object Portugal extends WithName("portugal") with ExemptCountries
  case object Romania extends WithName("romania") with ExemptCountries
  case object Slovakia extends WithName("slovakia") with ExemptCountries
  case object Slovenia extends WithName("slovenia") with ExemptCountries
  case object Spain extends WithName("spain") with ExemptCountries
  case object Sweden extends WithName("sweden") with ExemptCountries


  val values: Seq[ExemptCountries] = Seq(
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
      Checkboxes.Checkbox(msg"site.uk", UnitedKingdom.toString),
      Checkboxes.Checkbox(msg"site.Belgium", Belgium.toString),
      Checkboxes.Checkbox(msg"site.bulgaria", Bulgaria.toString),
      Checkboxes.Checkbox(msg"site.croatia", Croatia.toString),
      Checkboxes.Checkbox(msg"site.cyprus", Cyprus.toString),
      Checkboxes.Checkbox(msg"site.denmark", Denmark.toString),
      Checkboxes.Checkbox(msg"site.estonia", Estonia.toString),
      Checkboxes.Checkbox(msg"site.finland", Finland.toString),
      Checkboxes.Checkbox(msg"site.france", France.toString),
      Checkboxes.Checkbox(msg"site.germany", Germany.toString),
      Checkboxes.Checkbox(msg"site.greece", Greece.toString),
      Checkboxes.Checkbox(msg"site.hungary", Hungary.toString),
      Checkboxes.Checkbox(msg"site.ireland", Ireland.toString),
      Checkboxes.Checkbox(msg"site.italy", Italy.toString),
      Checkboxes.Checkbox(msg"site.latvia", Latvia.toString),
      Checkboxes.Checkbox(msg"site.lithuania", Lithuania.toString),
      Checkboxes.Checkbox(msg"site.luxembourg", Luxembourg.toString),
      Checkboxes.Checkbox(msg"site.malta", Malta.toString),
      Checkboxes.Checkbox(msg"site.netherlands", Netherlands.toString),
      Checkboxes.Checkbox(msg"site.poland", Poland.toString),
      Checkboxes.Checkbox(msg"site.portugal", Portugal.toString),
      Checkboxes.Checkbox(msg"site.romania", Romania.toString),
      Checkboxes.Checkbox(msg"site.slovakia", Slovakia.toString),
      Checkboxes.Checkbox(msg"site.slovenia", Slovenia.toString),
      Checkboxes.Checkbox(msg"site.spain", Spain.toString),
      Checkboxes.Checkbox(msg"site.sweden", Sweden.toString),
    )

    Checkboxes.set(field, items)
  }

  implicit val enumerable: Enumerable[ExemptCountries] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
