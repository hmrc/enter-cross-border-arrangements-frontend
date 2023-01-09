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

package models

import models.affected.Affected
import models.arrangement.ArrangementDetails
import models.disclosure.DisclosureType.Dac6new
import models.disclosure.{DisclosureDetails, DisclosureType}
import models.enterprises.AssociatedEnterprise
import models.hallmarks.HallmarkDetails
import models.intermediaries.Intermediary
import models.reporter.ReporterDetails
import models.taxpayer.Taxpayer
import pages.affected.AffectedLoopPage
import pages.arrangement.ArrangementDetailsPage
import pages.disclosure.DisclosureDetailsPage
import pages.enterprises.AssociatedEnterpriseLoopPage
import pages.hallmarks.HallmarkDetailsPage
import pages.intermediaries.IntermediaryLoopPage
import pages.reporter.ReporterDetailsPage
import pages.taxpayer.TaxpayerLoopPage
import play.api.libs.json.{Json, OFormat}

case class Submission(enrolmentID: String,
                      disclosureDetails: DisclosureDetails,
                      reporterDetails: Option[ReporterDetails] = None,
                      associatedEnterprises: IndexedSeq[AssociatedEnterprise] = IndexedSeq.empty,
                      taxpayers: IndexedSeq[Taxpayer] = IndexedSeq.empty,
                      intermediaries: IndexedSeq[Intermediary] = IndexedSeq.empty,
                      affectedPersons: IndexedSeq[Affected] = IndexedSeq.empty,
                      hallmarkDetails: Option[HallmarkDetails] = None,
                      arrangementDetails: Option[ArrangementDetails] = None
) {

  val getDisclosureID: Option[String] = disclosureDetails.disclosureID

  val getArrangementID: Option[String] = disclosureDetails.arrangementID

  val getMessageRefId: Option[String] = disclosureDetails.messageRefId

  val getDisclosureType: DisclosureType = disclosureDetails.disclosureType

  val getInitialDisclosureMA: Boolean = disclosureDetails.initialDisclosureMA

  //Hide when MA = True, Reporter = intermediary, No relevant tax payers
  def displayAssociatedEnterprises(): Boolean =
    disclosureDetails.disclosureType match {
      case Dac6new =>
        !(disclosureDetails.initialDisclosureMA && reporterDetails.exists(_.isIntermediary) && taxpayers.isEmpty)
      case _ =>
        !(disclosureDetails.firstInitialDisclosureMA.getOrElse(false) && reporterDetails.exists(_.isIntermediary) && taxpayers.isEmpty)
    }

  def setDisclosureDetails(disclosureDetails: DisclosureDetails): Submission = copy(disclosureDetails = disclosureDetails)

  def updateIds(ids: GeneratedIDs): GeneratedIDs = ids match {
    case GeneratedIDs(None, None, _, _) =>
      ids.copy(disclosureID = getDisclosureID, arrangementID = getArrangementID)
    case _ => ids
  }

}

object Submission {
  implicit val format: OFormat[Submission] = Json.format[Submission]

  def apply(userAnswers: UserAnswers, id: Int, enrolmentID: String): Submission =
    (for {
      disclosureDetails <- userAnswers.get(DisclosureDetailsPage, id)
      reporterDetails       = userAnswers.get(ReporterDetailsPage, id)
      associatedEnterprises = userAnswers.get(AssociatedEnterpriseLoopPage, id).getOrElse(IndexedSeq.empty)
      taxpayers             = userAnswers.get(TaxpayerLoopPage, id).getOrElse(IndexedSeq.empty)
      intermediaries        = userAnswers.get(IntermediaryLoopPage, id).getOrElse(IndexedSeq.empty)
      affectedPersons       = userAnswers.get(AffectedLoopPage, id).getOrElse(IndexedSeq.empty)
      hallmarkDetails       = userAnswers.get(HallmarkDetailsPage, id)
      arrangementDetails    = userAnswers.get(ArrangementDetailsPage, id)
    } yield this(enrolmentID,
                 disclosureDetails,
                 reporterDetails,
                 associatedEnterprises,
                 taxpayers,
                 intermediaries,
                 affectedPersons,
                 hallmarkDetails,
                 arrangementDetails
    ))
      .getOrElse(throw new IllegalStateException("Unable to create submission from model"))

}
