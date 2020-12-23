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

package models.reporter.intermediary

import models.{Enumerable, WithName}
import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels.NunjucksSupport
import viewmodels.{Hint, Radios}

sealed trait IntermediaryRole

object IntermediaryRole extends Enumerable.Implicits with NunjucksSupport {

  case object Promoter extends WithName("promoter") with IntermediaryRole
  case object ServiceProvider extends WithName("serviceProvider") with IntermediaryRole
  case object Unknown extends WithName("unknown") with IntermediaryRole

  val values: Seq[IntermediaryRole] = Seq(
    Promoter,
    ServiceProvider,
    Unknown
  )

  def radios(form: Form[_])(implicit messages: Messages): Seq[Radios.Item] = {

    val field = form("value")
    val items = Seq(
      Radios.Radio(label = msg"intermediaryRole.promoter",
        value = Promoter.toString,
        hint = Some(Hint(msg"intermediaryRole.promoter.hint", "promoter-hint"))),

      Radios.Radio(label = msg"intermediaryRole.serviceProvider",
        value = ServiceProvider.toString,
        hint = Some(Hint(msg"intermediaryRole.serviceProvider.hint", "service-provider-hint"))),

      Radios.Radio(msg"intermediaryRole.unknown", ServiceProvider.toString)
    )

    Radios(field, items)
  }

  implicit val enumerable: Enumerable[IntermediaryRole] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
