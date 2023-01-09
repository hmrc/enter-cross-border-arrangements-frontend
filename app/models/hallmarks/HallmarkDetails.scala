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

package models.hallmarks

import controllers.exceptions.SomeInformationIsMissingException
import models.hallmarks.HallmarkD.{D1, D2}
import models.{HallmarkDMissingError, SubmissionError, UserAnswers}
import pages.hallmarks.{HallmarkD1OtherPage, HallmarkD1Page, HallmarkDPage}
import play.api.libs.json.{Json, OFormat}

case class HallmarkDetails(hallmarkType: List[String], hallmarkContent: Option[String] = None) {

  def validate: Either[SubmissionError, HallmarkDetails] =
    Either.cond(hallmarkType.exists(_.startsWith("DAC6D")), this, HallmarkDMissingError)
}

object HallmarkDetails {
  implicit val format: OFormat[HallmarkDetails] = Json.format[HallmarkDetails]

  def buildHallmarkDetails(ua: UserAnswers, id: Int): HallmarkDetails = {

    lazy val mandatoryD1content = ua
      .get(HallmarkD1OtherPage, id)
      .fold(throw new SomeInformationIsMissingException(id, "DAC6D1other information must be provided if DAC6D1other is selected"))(
        info => Some(info)
      )

    (ua.get(HallmarkDPage, id), ua.get(HallmarkD1Page, id)) match {

      // user selects D1 & D2
      case (Some(hallmarks), Some(hallmarkDParts)) if hallmarks.size > 1 =>
        if (hallmarkDParts.contains(HallmarkD1.D1other)) {
          new HallmarkDetails(
            hallmarkType = hallmarkDParts.toList.map(_.toString).sorted ::: List(HallmarkD.D2.toString),
            hallmarkContent = mandatoryD1content
          )
        } else {
          new HallmarkDetails(
            hallmarkType = hallmarkDParts.toList.map(_.toString).sorted ::: List(HallmarkD.D2.toString)
          )
        }

      // user selects D1 only
      case (Some(hallmarks), Some(hallmarkDParts)) if hallmarks.size == 1 && hallmarks.contains(D1) =>
        if (hallmarkDParts.contains(HallmarkD1.D1other)) {
          new HallmarkDetails(
            hallmarkType = hallmarkDParts.toList.map(_.toString).sorted,
            hallmarkContent = mandatoryD1content
          )
        } else {
          new HallmarkDetails(
            hallmarkType = hallmarkDParts.toList.map(_.toString).sorted
          )
        }

      // user selects D2 only
      case (Some(hallmarks), None) if hallmarks.contains(D2) && hallmarks.size == 1 =>
        new HallmarkDetails(
          hallmarkType = List(HallmarkD.D2.toString)
        )

      case _ => throw new SomeInformationIsMissingException(id, "Unable to build hallmark details as missing mandatory answers")
    }
  }
}
