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

package models.reporter

import controllers.exceptions.SomeInformationIsMissingException

import java.time.LocalDate
import models.reporter.RoleInArrangement.{Intermediary, Taxpayer}
import models.reporter.intermediary.{IntermediaryRole, IntermediaryWhyReportInUK}
import models.reporter.taxpayer.{TaxpayerWhyReportArrangement, TaxpayerWhyReportInUK}
import models.{UserAnswers, YesNoDoNotKnowRadios}
import pages.reporter.RoleInArrangementPage
import pages.reporter.intermediary._
import pages.reporter.taxpayer.{ReporterTaxpayersStartDateForImplementingArrangementPage, TaxpayerWhyReportArrangementPage, TaxpayerWhyReportInUKPage}
import play.api.libs.json.{Json, OFormat}

case class ReporterLiability(role: String,
                             nexus: Option[String] = None,
                             capacity: Option[String] = None,
                             nationalExemption: Option[Boolean] = None,
                             exemptCountries: Option[List[String]] = None,
                             implementingDate: Option[LocalDate] = None
)

object ReporterLiability {
  implicit val format: OFormat[ReporterLiability] = Json.format[ReporterLiability]

  private def getTaxpayerNexus(ua: UserAnswers, id: Int): Option[String] =
    ua.get(TaxpayerWhyReportInUKPage, id) match {
      case Some(nexus) if !nexus.equals(TaxpayerWhyReportInUK.DoNotKnow) => Some(nexus.toString)
      case Some(_)                                                       => None
      case None =>
        throw new SomeInformationIsMissingException(id, "Reporter liability must indicate why report in uk.")
    }

  private def getTaxpayerCapacity(ua: UserAnswers, id: Int): Option[String] =
    if (ua.get(TaxpayerWhyReportInUKPage, id).contains(TaxpayerWhyReportInUK.DoNotKnow)) {
      None
    } else {
      ua.get(TaxpayerWhyReportArrangementPage, id) match {
        case Some(capacity) if !capacity.equals(TaxpayerWhyReportArrangement.DoNotKnow) => Some(capacity.toString)
        case Some(_)                                                                    => None
        case None =>
          throw new SomeInformationIsMissingException(id, "Reporter liability must indicate why report arrangement.")
      }
    }

  ///
  private def getIntermediaryNexus(ua: UserAnswers, id: Int): Option[String] =
    ua.get(IntermediaryWhyReportInUKPage, id) match {
      case Some(nexus) if !nexus.equals(IntermediaryWhyReportInUK.DoNotKnow) => Some(nexus.toString)
      case _                                                                 => None
    }

  private def getIntermediaryCapacity(ua: UserAnswers, id: Int): Option[String] =
    ua.get(IntermediaryRolePage, id) match {
      case Some(capacity) if !capacity.equals(IntermediaryRole.Unknown) => Some(capacity.toString)
      case _                                                            => None
    }

  private def getNationalExemption(ua: UserAnswers, id: Int): Option[Boolean] =
    ua.get(IntermediaryExemptionInEUPage, id) match {
      case Some(YesNoDoNotKnowRadios.Yes)       => Some(true)
      case Some(YesNoDoNotKnowRadios.No)        => Some(false)
      case Some(YesNoDoNotKnowRadios.DoNotKnow) => None
      case _                                    => throw new SomeInformationIsMissingException(id, "Reporter liability must indicate the exemption status or 'I do not know' ")
    }

  private def getExemptCountries(ua: UserAnswers, id: Int): Option[List[String]] =
    if (getNationalExemption(ua, id).contains(true)) {
      ua.get(IntermediaryDoYouKnowExemptionsPage, id) match {
        case Some(true) =>
          ua.get(IntermediaryWhichCountriesExemptPage, id)
            .fold(
              throw new SomeInformationIsMissingException(id,
                                                          "Reporter Liability must contain countries" +
                                                            "when 'yes' to 'do you know exemptions' is selected"
              )
            )(
              selectedCountries => Some(selectedCountries.toList.map(_.toString).sorted)
            )
        case Some(false) => None
        case None =>
          throw new SomeInformationIsMissingException(id,
                                                      "Reporter Liability must contain countries" +
                                                        "when 'yes' to 'do you know exemptions' is selected"
          )
      }
    } else {
      None
    }

  def buildReporterLiability(ua: UserAnswers, id: Int): ReporterLiability =
    ua.get(RoleInArrangementPage, id) match {
      case Some(RoleInArrangement.Taxpayer) =>
        new ReporterLiability(
          role = Taxpayer.toString,
          nexus = getTaxpayerNexus(ua, id),
          capacity = getTaxpayerCapacity(ua, id),
          implementingDate = ua.get(ReporterTaxpayersStartDateForImplementingArrangementPage, id)
        )

      case Some(RoleInArrangement.Intermediary) =>
        new ReporterLiability(
          role = Intermediary.toString,
          nexus = getIntermediaryNexus(ua, id),
          capacity = getIntermediaryCapacity(ua, id),
          nationalExemption = getNationalExemption(ua, id),
          exemptCountries = getExemptCountries(ua, id)
        )

      case _ => throw new SomeInformationIsMissingException(id, "Unable to build reporter liability as missing mandatory answers")
    }
}
