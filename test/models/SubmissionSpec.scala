/*
 * Copyright 2022 HM Revenue & Customs
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

import base.ModelSpecBase
import models.affected.Affected
import models.arrangement.ArrangementDetails
import models.disclosure.{DisclosureDetails, DisclosureType}
import models.enterprises.AssociatedEnterprise
import models.hallmarks.HallmarkDetails
import models.intermediaries.Intermediary
import models.reporter.{ReporterDetails, ReporterLiability}
import models.taxpayer.Taxpayer
import org.scalatest.TryValues.convertTryToSuccessOrFailure
import pages.affected.AffectedLoopPage
import pages.arrangement.ArrangementDetailsPage
import pages.disclosure.{DisclosureDetailsPage, FirstInitialDisclosureMAPage}
import pages.hallmarks.HallmarkDetailsPage
import pages.intermediaries.IntermediaryLoopPage
import pages.reporter.ReporterDetailsPage
import pages.unsubmitted.UnsubmittedDisclosurePage

class SubmissionSpec extends ModelSpecBase {

  import helpers.data.ValidUserAnswersForSubmission._

  val submissionEnrollmentID: String                                    = "enrolmentId"
  val submissionDisclosureDetails: DisclosureDetails                    = validDisclosureDetails
  val submissionReporterDetails: Option[ReporterDetails]                = None
  val submissionAssociatedEnterprises: IndexedSeq[AssociatedEnterprise] = IndexedSeq.empty
  val submissionTaxpayers: IndexedSeq[Taxpayer]                         = IndexedSeq.empty
  val submissionIntermediaries: IndexedSeq[Intermediary]                = IndexedSeq.empty
  val submissionAffectedPersons: IndexedSeq[Affected]                   = IndexedSeq.empty
  val submissionHallmarkDetails: Option[HallmarkDetails]                = None
  val submissionArrangementDetails: Option[ArrangementDetails]          = None

  "Submission" - {

    "from models" - {

      "must build from empty or optional models" in {

        val submission = Submission(
          submissionEnrollmentID,
          submissionDisclosureDetails,
          submissionReporterDetails,
          submissionAssociatedEnterprises,
          submissionTaxpayers,
          submissionIntermediaries,
          submissionAffectedPersons,
          submissionHallmarkDetails,
          submissionArrangementDetails
        )

        submission.enrolmentID must be("enrolmentId")
        submission.disclosureDetails must be(validDisclosureDetails)
        submission.reporterDetails must be(None)
        submission.associatedEnterprises must be(IndexedSeq.empty)
        submission.taxpayers must be(IndexedSeq.empty)
        submission.intermediaries must be(IndexedSeq.empty)
        submission.affectedPersons must be(IndexedSeq.empty)
        submission.hallmarkDetails must be(None)
        submission.arrangementDetails must be(None)
      }

      "must build from individual reporter model" in {

        val reporterDetailsAsIndividual = ReporterDetails(Some(validIndividual))

        val submissionReporterDetails: Option[ReporterDetails] = Some(reporterDetailsAsIndividual)

        val submission = Submission(
          submissionEnrollmentID,
          submissionDisclosureDetails,
          submissionReporterDetails,
          submissionAssociatedEnterprises,
          submissionTaxpayers,
          submissionIntermediaries,
          submissionAffectedPersons,
          submissionHallmarkDetails,
          submissionArrangementDetails
        )

        submission.enrolmentID must be("enrolmentId")
        submission.disclosureDetails must be(validDisclosureDetails)
        submission.reporterDetails.map {
          reporterDetails =>
            reporterDetails.individual must be(Some(validIndividual))
            reporterDetails.organisation must be(None)
            reporterDetails.liability must be(None)
        }
        submission.associatedEnterprises must be(IndexedSeq.empty)
        submission.taxpayers must be(IndexedSeq.empty)
        submission.intermediaries must be(IndexedSeq.empty)
        submission.affectedPersons must be(IndexedSeq.empty)
        submission.hallmarkDetails must be(None)
        submission.arrangementDetails must be(None)
      }

      "must build from organisation reporter model" in {

        val reporterDetailsAsOrganisation = ReporterDetails(None, Some(validOrganisation), Some(validLiability))

        val submissionReporterDetails: Option[ReporterDetails] = Some(reporterDetailsAsOrganisation)

        val submission = Submission(
          submissionEnrollmentID,
          submissionDisclosureDetails,
          submissionReporterDetails,
          submissionAssociatedEnterprises,
          submissionTaxpayers,
          submissionIntermediaries,
          submissionAffectedPersons,
          submissionHallmarkDetails,
          submissionArrangementDetails
        )

        submission.enrolmentID must be("enrolmentId")
        submission.disclosureDetails must be(validDisclosureDetails)
        submission.reporterDetails.map {
          reporterDetails =>
            reporterDetails.individual must be(None)
            reporterDetails.organisation must be(Some(validOrganisation))
            reporterDetails.liability must be(Some(validLiability))
        }
        submission.associatedEnterprises must be(IndexedSeq.empty)
        submission.taxpayers must be(IndexedSeq.empty)
        submission.intermediaries must be(IndexedSeq.empty)
        submission.affectedPersons must be(IndexedSeq.empty)
        submission.hallmarkDetails must be(None)
        submission.arrangementDetails must be(None)
      }

      "must build from associated enterprises model" in {

        val submissionAssociatedEnterprises = validEnterprises

        val submission = Submission(
          submissionEnrollmentID,
          submissionDisclosureDetails,
          submissionReporterDetails,
          submissionAssociatedEnterprises,
          submissionTaxpayers,
          submissionIntermediaries,
          submissionAffectedPersons,
          submissionHallmarkDetails,
          submissionArrangementDetails
        )

        submission.enrolmentID must be("enrolmentId")
        submission.disclosureDetails must be(validDisclosureDetails)
        submission.reporterDetails must be(None)
        submission.associatedEnterprises must be(validEnterprises)
        submission.taxpayers must be(IndexedSeq.empty)
        submission.intermediaries must be(IndexedSeq.empty)
        submission.affectedPersons must be(IndexedSeq.empty)
        submission.hallmarkDetails must be(None)
        submission.arrangementDetails must be(None)
      }

      "must build from taxpayers model" in {

        val submissionTaxpayers = validTaxpayers

        val submission = Submission(
          submissionEnrollmentID,
          submissionDisclosureDetails,
          submissionReporterDetails,
          submissionAssociatedEnterprises,
          submissionTaxpayers,
          submissionIntermediaries,
          submissionAffectedPersons,
          submissionHallmarkDetails,
          submissionArrangementDetails
        )

        submission.enrolmentID must be("enrolmentId")
        submission.disclosureDetails must be(validDisclosureDetails)
        submission.reporterDetails must be(None)
        submission.associatedEnterprises must be(IndexedSeq.empty)
        submission.taxpayers must be(validTaxpayers)
        submission.intermediaries must be(IndexedSeq.empty)
        submission.affectedPersons must be(IndexedSeq.empty)
        submission.hallmarkDetails must be(None)
        submission.arrangementDetails must be(None)
      }

      "must build from intermediaries model" in {

        val submissionIntermediaries = validIntermediaries

        val submission = Submission(
          submissionEnrollmentID,
          submissionDisclosureDetails,
          submissionReporterDetails,
          submissionAssociatedEnterprises,
          submissionTaxpayers,
          submissionIntermediaries,
          submissionAffectedPersons,
          submissionHallmarkDetails,
          submissionArrangementDetails
        )

        submission.enrolmentID must be("enrolmentId")
        submission.disclosureDetails must be(validDisclosureDetails)
        submission.reporterDetails must be(None)
        submission.associatedEnterprises must be(IndexedSeq.empty)
        submission.taxpayers must be(IndexedSeq.empty)
        submission.intermediaries must be(validIntermediaries)
        submission.affectedPersons must be(IndexedSeq.empty)
        submission.hallmarkDetails must be(None)
        submission.arrangementDetails must be(None)
      }

      "must build from affected persons model" in {

        val submissionAffectedPersons = validAffectedPersons

        val submission = Submission(
          submissionEnrollmentID,
          submissionDisclosureDetails,
          submissionReporterDetails,
          submissionAssociatedEnterprises,
          submissionTaxpayers,
          submissionIntermediaries,
          submissionAffectedPersons,
          submissionHallmarkDetails,
          submissionArrangementDetails
        )

        submission.enrolmentID must be("enrolmentId")
        submission.disclosureDetails must be(validDisclosureDetails)
        submission.reporterDetails must be(None)
        submission.associatedEnterprises must be(IndexedSeq.empty)
        submission.taxpayers must be(IndexedSeq.empty)
        submission.intermediaries must be(IndexedSeq.empty)
        submission.affectedPersons must be(validAffectedPersons)
        submission.hallmarkDetails must be(None)
        submission.arrangementDetails must be(None)
      }

      "must build from hallmarks model" in {

        val submissionHallmarkDetails = Some(validHallmarkDetails)

        val submission = Submission(
          submissionEnrollmentID,
          submissionDisclosureDetails,
          submissionReporterDetails,
          submissionAssociatedEnterprises,
          submissionTaxpayers,
          submissionIntermediaries,
          submissionAffectedPersons,
          submissionHallmarkDetails,
          submissionArrangementDetails
        )

        submission.enrolmentID must be("enrolmentId")
        submission.disclosureDetails must be(validDisclosureDetails)
        submission.reporterDetails must be(None)
        submission.associatedEnterprises must be(IndexedSeq.empty)
        submission.taxpayers must be(IndexedSeq.empty)
        submission.intermediaries must be(IndexedSeq.empty)
        submission.affectedPersons must be(IndexedSeq.empty)
        submission.hallmarkDetails must be(Some(validHallmarkDetails))
        submission.arrangementDetails must be(None)
      }

      "must build from arrangement details model" in {

        val submissionArrangementDetails = Some(validArrangementDetails)

        val submission = Submission(
          submissionEnrollmentID,
          submissionDisclosureDetails,
          submissionReporterDetails,
          submissionAssociatedEnterprises,
          submissionTaxpayers,
          submissionIntermediaries,
          submissionAffectedPersons,
          submissionHallmarkDetails,
          submissionArrangementDetails
        )

        submission.enrolmentID must be("enrolmentId")
        submission.disclosureDetails must be(validDisclosureDetails)
        submission.reporterDetails must be(None)
        submission.associatedEnterprises must be(IndexedSeq.empty)
        submission.taxpayers must be(IndexedSeq.empty)
        submission.intermediaries must be(IndexedSeq.empty)
        submission.affectedPersons must be(IndexedSeq.empty)
        submission.hallmarkDetails must be(None)
        submission.arrangementDetails must be(Some(validArrangementDetails))
      }

    }

    "from pages" - {

      "from details pages" in {

        val submission = Submission(userAnswersModelsForOrganisation, 0, "enrolmentId")

        submission.enrolmentID must be("enrolmentId")
        submission.disclosureDetails must be(validDisclosureDetails)
        submission.reporterDetails.map {
          reporterDetails =>
            reporterDetails.individual must be(None)
            reporterDetails.organisation must be(Some(validOrganisation))
            reporterDetails.liability must be(None)
        }
        submission.associatedEnterprises must be(validEnterprises)
        submission.taxpayers must be(validTaxpayers)
        submission.intermediaries must be(validIntermediaries)
        submission.affectedPersons must be(validAffectedPersons)
        submission.hallmarkDetails must be(Some(validHallmarkDetails))
        submission.arrangementDetails must be(Some(validArrangementDetails))
      }

      "update initial disclosure MA when there is a first initial disclosure page" in {

        val submissionWithFirstInitalDisclosure = userAnswersModelsForOrganisation
          .setBase(FirstInitialDisclosureMAPage, true)
          .success
          .value

        val submission = Submission(submissionWithFirstInitalDisclosure, 0, "enrolmentId")
        submission.disclosureDetails.initialDisclosureMA must be(true)
      }
    }

    "displayAssociatedEnterprises" - {

      "must return false if reporter is intermediary - has no relevant taxpayers & InitialMA is true" in {

        val reporterDetailsAsIntermediary = ReporterDetails(None, Some(validOrganisation), Some(ReporterLiability("intermediary")))

        val userAnswers = UserAnswers("id")
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
          .success
          .value
          .set(DisclosureDetailsPage, 0, validDisclosureDetails)
          .success
          .value
          .set(ReporterDetailsPage, 0, reporterDetailsAsIntermediary)
          .success
          .value
          .set(IntermediaryLoopPage, 0, validIntermediaries)
          .success
          .value
          .set(AffectedLoopPage, 0, validAffectedPersons)
          .success
          .value
          .set(HallmarkDetailsPage, 0, validHallmarkDetails)
          .success
          .value
          .set(ArrangementDetailsPage, 0, validArrangementDetails)
          .success
          .value

        val submission = Submission(userAnswers, 0, "enrolmentID")

        submission.displayAssociatedEnterprises must be(false)

      }

      "must return false if reporter is intermediary - has no relevant taxpayers - initialMA is false & firstInitialDisclosureMA is true" in {

        val reporterDetailsAsIntermediary = ReporterDetails(None, Some(validOrganisation), Some(ReporterLiability("intermediary")))

        val validDisclosureDetails = DisclosureDetails(disclosureName = "DisclosureName",
                                                       disclosureType = DisclosureType.Dac6add,
                                                       initialDisclosureMA = false,
                                                       firstInitialDisclosureMA = Some(true)
        )

        val userAnswers = UserAnswers("id")
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
          .success
          .value
          .set(DisclosureDetailsPage, 0, validDisclosureDetails)
          .success
          .value
          .set(ReporterDetailsPage, 0, reporterDetailsAsIntermediary)
          .success
          .value
          .set(IntermediaryLoopPage, 0, validIntermediaries)
          .success
          .value
          .set(AffectedLoopPage, 0, validAffectedPersons)
          .success
          .value
          .set(HallmarkDetailsPage, 0, validHallmarkDetails)
          .success
          .value
          .set(ArrangementDetailsPage, 0, validArrangementDetails)
          .success
          .value

        val submission = Submission(userAnswers, 0, "enrolmentID")

        submission.displayAssociatedEnterprises must be(false)

      }

      "must return true if reporter is taxpayer & InitialMA is false" in {

        val reporterDetailsAsIntermediary = ReporterDetails(None, Some(validOrganisation), Some(ReporterLiability("taxpayer")))

        val validDisclosureDetails = DisclosureDetails(disclosureName = "DisclosureName", disclosureType = DisclosureType.Dac6new, initialDisclosureMA = false)

        val userAnswers = UserAnswers("id")
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
          .success
          .value
          .set(DisclosureDetailsPage, 0, validDisclosureDetails)
          .success
          .value
          .set(ReporterDetailsPage, 0, reporterDetailsAsIntermediary)
          .success
          .value
          .set(IntermediaryLoopPage, 0, validIntermediaries)
          .success
          .value
          .set(AffectedLoopPage, 0, validAffectedPersons)
          .success
          .value
          .set(HallmarkDetailsPage, 0, validHallmarkDetails)
          .success
          .value
          .set(ArrangementDetailsPage, 0, validArrangementDetails)
          .success
          .value

        val submission = Submission(userAnswers, 0, "enrolmentID")

        submission.displayAssociatedEnterprises must be(true)

      }

      "must return false if reporter is intermediary & firstInitialMA is true when doing a replace of the new" in {

        val reporterDetailsAsIntermediary = ReporterDetails(None, Some(validOrganisation), Some(ReporterLiability("intermediary")))

        val validDisclosureDetails = DisclosureDetails(disclosureName = "DisclosureName",
                                                       disclosureType = DisclosureType.Dac6rep,
                                                       initialDisclosureMA = false,
                                                       firstInitialDisclosureMA = Some(true)
        )

        val userAnswers = UserAnswers("id")
          .setBase(UnsubmittedDisclosurePage, Seq(UnsubmittedDisclosure("1", "My First")))
          .success
          .value
          .set(DisclosureDetailsPage, 0, validDisclosureDetails)
          .success
          .value
          .set(ReporterDetailsPage, 0, reporterDetailsAsIntermediary)
          .success
          .value
          .set(IntermediaryLoopPage, 0, validIntermediaries)
          .success
          .value
          .set(AffectedLoopPage, 0, validAffectedPersons)
          .success
          .value
          .set(HallmarkDetailsPage, 0, validHallmarkDetails)
          .success
          .value
          .set(ArrangementDetailsPage, 0, validArrangementDetails)
          .success
          .value

        val submission = Submission(userAnswers, 0, "enrolmentID")

        submission.displayAssociatedEnterprises must be(false)

      }
    }
  }
}
