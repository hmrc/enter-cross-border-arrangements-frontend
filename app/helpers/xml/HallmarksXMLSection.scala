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

package helpers.xml

import models.{HallmarkDetailsNotDefinedError, Submission}
import models.hallmarks.HallmarkDetails

import scala.xml.{Elem, NodeSeq}

case class HallmarksXMLSection(submission: Submission) {

  val hallmarkDetails: HallmarkDetails = submission.hallmarkDetails
    .orElse(throw new IllegalStateException(HallmarkDetailsNotDefinedError.defaultMessage))
    .get.validate.fold(e => throw new IllegalStateException(e.defaultMessage), identity)

  val groupSize = 4000

  private[xml] def buildHallmarks: Elem = {

    val hallmarkContent = hallmarkDetails.hallmarkContent.fold(NodeSeq.Empty)(content =>
      content.grouped(groupSize).toList.map(string => <DAC6D1OtherInfo>{string}</DAC6D1OtherInfo>))

    <Hallmarks>
      <ListHallmarks>
        {hallmarkDetails.hallmarkType.map(hallmark => <Hallmark>{hallmark}</Hallmark>)}
      </ListHallmarks>
      {hallmarkContent}
    </Hallmarks>
  }

}
