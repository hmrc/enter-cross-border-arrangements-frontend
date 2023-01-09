/*
 * Copyright 2023 HM Revenue & Customs
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

package controllers.mixins

import models.Mode

sealed trait CheckRoute {

  val mode: Mode
}

case class DefaultRouting(mode: Mode) extends CheckRoute
case class AssociatedEnterprisesRouting(mode: Mode) extends CheckRoute
case class TaxpayersRouting(mode: Mode) extends CheckRoute
case class IntermediariesRouting(mode: Mode) extends CheckRoute
case class ArrangementRouting(mode: Mode) extends CheckRoute
case class AffectedRouting(mode: Mode) extends CheckRoute
