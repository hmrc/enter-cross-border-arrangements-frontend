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

package helpers

import models.UserAnswers
import models.disclosure.DisclosureType.{Dac6add, Dac6rep}
import models.disclosure.{DisclosureDetails, DisclosureType}
import models.hallmarks.JourneyStatus
import models.hallmarks.JourneyStatus.{Completed, InProgress, NotStarted, Restricted}
import models.reporter.RoleInArrangement
import pages.QuestionPage
import pages.affected.AffectedStatusPage
import pages.arrangement.ArrangementStatusPage
import pages.disclosure.DisclosureStatusPage
import pages.enterprises.AssociatedEnterpriseStatusPage
import pages.hallmarks.HallmarkStatusPage
import pages.intermediaries.IntermediariesStatusPage
import pages.reporter.{ReporterStatusPage, RoleInArrangementPage}
import pages.taxpayer.{RelevantTaxpayerStatusPage, TaxpayerLoopPage}
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels.Html

object TaskListHelper {

  val optionalCompletion: Seq[QuestionPage[JourneyStatus]] = Seq(HallmarkStatusPage, ArrangementStatusPage)

  def retrieveRowWithStatus(status: JourneyStatus,
                            url: Option[String],
                            linkContent: String,
                            id: String,
                            ariaLabel: String,
                            rowStyle: String,
                            extraStyle: Option[String] = None
  )(implicit messages: Messages): Html =
    status match {
      case Completed =>
        taskListItemProvider(url, Completed.toString, linkContent, s"$id-completed", ariaLabel, rowStyle, s"govuk-tag", extraStyle)
      case InProgress =>
        taskListItemProvider(url, InProgress.toString, linkContent, s"$id-inProgress", ariaLabel, rowStyle, s"govuk-tag govuk-tag--blue", extraStyle)
      case Restricted =>
        taskListItemProvider(None, Restricted.toString, linkContent, s"$id-restricted", ariaLabel, rowStyle, s"govuk-tag govuk-tag--grey", extraStyle)
      case _ =>
        taskListItemProvider(url, NotStarted.toString, linkContent, s"$id-notStarted", ariaLabel, rowStyle, s"govuk-tag govuk-tag--grey", extraStyle)
    }

  def taskListItemProvider(url: Option[String],
                           status: String,
                           linkContent: String,
                           id: String,
                           ariaLabel: String,
                           rowStyle: String,
                           colourClass: String,
                           extraStyle: Option[String] = None
  )(implicit messages: Messages): Html = {

    val utlToHref = url.fold("")(
      url => s"href=$url"
    )

    val liOptions = extraStyle match {
      case Some(style) => s"class='app-task-list__$rowStyle' style='$style'"
      case _           => s"class='app-task-list__$rowStyle'"
    }

    Html(
      s"<li $liOptions><a class='app-task-list__task-name' $utlToHref aria-describedby='$ariaLabel'> ${messages(linkContent)}</a>" +
        s"<strong class='$colourClass app-task-list__task-completed' id='$id'>$status</strong></li>"
    )
  }

  def haveAllJourneysBeenCompleted(pageList: Seq[_ <: QuestionPage[JourneyStatus]], ua: UserAnswers, id: Int, disclosureDetails: DisclosureDetails): Boolean = {

    val firstInitialDisclosure = disclosureDetails.firstInitialDisclosureMA.getOrElse(false)
    val isDac6RepOfNewMarketable =
      disclosureDetails.disclosureType.equals(DisclosureType.Dac6rep) && disclosureDetails.initialDisclosureMA && firstInitialDisclosure

    pageList
      .map(
        page =>
          ua.get(page, id) match {
            case Some(Completed)                                                                 => true
            case Some(InProgress) if firstInitialDisclosure && optionalCompletion.contains(page) => false
            case _ if isDac6RepOfNewMarketable && optionalCompletion.contains(page)              => false
            case _ if firstInitialDisclosure && optionalCompletion.contains(page)                => true
            case _                                                                               => false
          }
      )
      .forall(
        bool => bool
      )
  }

  def hrefToStartJourneyOrCya(ua: UserAnswers, page: QuestionPage[JourneyStatus], url: String, altUrl: String, id: Int): String =
    ua.get(page, id) match {
      case Some(Completed) => altUrl
      case _               => url
    }

  def displaySectionOptional(disclosureDetails: DisclosureDetails)(implicit messages: Messages): String =
    (disclosureDetails.disclosureType, disclosureDetails.initialDisclosureMA, disclosureDetails.firstInitialDisclosureMA) match {
      case (Dac6add, _, Some(true)) =>
        messages("disclosureDetails.optional")
      case (Dac6rep, false, Some(true)) =>
        messages("disclosureDetails.optional")
      case _ =>
        ""
    }

  def isDisplaySectionOptional(disclosureType: DisclosureType, firstInitialDisclosureWasMarketable: Boolean): Boolean =
    disclosureType match {
      case Dac6add if firstInitialDisclosureWasMarketable =>
        true
      case Dac6rep if firstInitialDisclosureWasMarketable =>
        true
      case _ => false
    }

  def userCanSubmit(ua: UserAnswers, id: Int, disclosureDetails: DisclosureDetails): Boolean = {

    val submissionContainsTaxpayer: Boolean = ua.get(TaxpayerLoopPage, id).fold(false)(_.nonEmpty) ||
      ua.get(RoleInArrangementPage, id).fold(false)(_.equals(RoleInArrangement.Taxpayer))

    val firstInitialDisclosureWasMarketable = disclosureDetails.firstInitialDisclosureMA.getOrElse(false)

    val mandatoryCompletion =
      if (submissionContainsTaxpayer) {
        Seq(ReporterStatusPage, RelevantTaxpayerStatusPage, IntermediariesStatusPage, DisclosureStatusPage, AffectedStatusPage, AssociatedEnterpriseStatusPage)
      } else {
        Seq(ReporterStatusPage, RelevantTaxpayerStatusPage, IntermediariesStatusPage, DisclosureStatusPage, AffectedStatusPage)
      }

    val listToCheckForCompletion: Seq[QuestionPage[JourneyStatus]] =
      disclosureDetails.disclosureType match {
        case DisclosureType.Dac6add if firstInitialDisclosureWasMarketable =>
          mandatoryCompletion

        case DisclosureType.Dac6rep if firstInitialDisclosureWasMarketable && disclosureDetails.initialDisclosureMA =>
          mandatoryCompletion ++ optionalCompletion

        case DisclosureType.Dac6rep if firstInitialDisclosureWasMarketable =>
          mandatoryCompletion

        case _ =>
          mandatoryCompletion ++ optionalCompletion
      }

    (ua.get(HallmarkStatusPage, id), ua.get(ArrangementStatusPage, id)) match {
      case (Some(Completed), None) if firstInitialDisclosureWasMarketable => false
      case _                                                              => haveAllJourneysBeenCompleted(listToCheckForCompletion, ua, id, disclosureDetails)
    }
  }
}
