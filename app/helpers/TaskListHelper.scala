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

package helpers

import connectors.HistoryConnector
import models.UserAnswers
import models.disclosure.DisclosureType
import models.disclosure.DisclosureType.{Dac6add, Dac6rep}
import models.hallmarks.JourneyStatus
import models.hallmarks.JourneyStatus.{Completed, InProgress, NotStarted, Restricted}
import models.reporter.RoleInArrangement
import pages.QuestionPage
import pages.affected.AffectedStatusPage
import pages.arrangement.ArrangementStatusPage
import pages.disclosure.{DisclosureDetailsPage, DisclosureStatusPage, FirstInitialDisclosureMAPage}
import pages.enterprises.AssociatedEnterpriseStatusPage
import pages.hallmarks.HallmarkStatusPage
import pages.intermediaries.IntermediariesStatusPage
import pages.reporter.{ReporterStatusPage, RoleInArrangementPage}
import pages.taxpayer.{RelevantTaxpayerStatusPage, TaxpayerLoopPage}
import play.api.i18n.Messages
import repositories.SessionRepository
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.viewmodels.Html

import scala.concurrent.{ExecutionContext, Future}

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

  def haveAllJourneysBeenCompleted(pageList: Seq[_ <: QuestionPage[JourneyStatus]], ua: UserAnswers, id: Int, isInitialDisclosureMarketable: Boolean): Boolean =
    pageList
      .map(
        page =>
          ua.get(page, id) match {
            case Some(Completed)                                                                        => true
            case Some(InProgress) if isInitialDisclosureMarketable && optionalCompletion.contains(page) => false
            case _ if isInitialDisclosureMarketable && optionalCompletion.contains(page)                => true
            case _                                                                                      => false
          }
      )
      .forall(
        bool => bool
      )

  def hrefToStartJourneyOrCya(ua: UserAnswers, page: QuestionPage[JourneyStatus], url: String, altUrl: String, id: Int): String =
    ua.get(page, id) match {
      case Some(Completed) => altUrl
      case _               => url
    }

  private def getDisclosureTypeWithMAFlag(ua: UserAnswers, id: Int): (Option[DisclosureType], Boolean) = {
    val getMarketableFlag: Boolean                = ua.get(DisclosureDetailsPage, id).exists(_.initialDisclosureMA)
    val getDisclosureType: Option[DisclosureType] = ua.get(DisclosureDetailsPage, id).map(_.disclosureType)

    (getDisclosureType, getMarketableFlag)
  }

  def displaySectionOptional(ua: UserAnswers, id: Int, isInitialDisclosureMarketable: Boolean)(implicit messages: Messages): String =
    getDisclosureTypeWithMAFlag(ua, id) match {
      case (Some(Dac6add), true) =>
        messages("disclosureDetails.optional")
      case (Some(Dac6rep), _) if isInitialDisclosureMarketable =>
        messages("disclosureDetails.optional")
      case _ =>
        ""
    }

  def isDisplaySectionOptional(ua: UserAnswers, id: Int, isInitialDisclosureMarketable: Boolean): Boolean =
    getDisclosureTypeWithMAFlag(ua, id) match {
      case (Some(Dac6add), true) =>
        true
      case (Some(Dac6rep), _) if isInitialDisclosureMarketable =>
        true
      case _ => false
    }

  def userCanSubmit(ua: UserAnswers, id: Int, isInitialDisclosureMarketable: Boolean): Boolean = {

    val submissionContainsTaxpayer: Boolean = ua.get(TaxpayerLoopPage, id).fold(false)(_.nonEmpty) ||
      ua.get(RoleInArrangementPage, id).fold(false)(_.equals(RoleInArrangement.Taxpayer))

    val mandatoryCompletion =
      if (submissionContainsTaxpayer) {
        Seq(ReporterStatusPage, RelevantTaxpayerStatusPage, IntermediariesStatusPage, DisclosureStatusPage, AffectedStatusPage, AssociatedEnterpriseStatusPage)
      } else {
        Seq(ReporterStatusPage, RelevantTaxpayerStatusPage, IntermediariesStatusPage, DisclosureStatusPage, AffectedStatusPage)
      }

    val listToCheckForCompletion: Seq[QuestionPage[JourneyStatus]] =
      getDisclosureTypeWithMAFlag(ua, id) match {
        case (Some(DisclosureType.Dac6add), true) if !isInitialDisclosureMarketable =>
          mandatoryCompletion
        case _ =>
          mandatoryCompletion ++ optionalCompletion
      }

    (ua.get(HallmarkStatusPage, id), ua.get(ArrangementStatusPage, id)) match {
      case (Some(Completed), None) if isInitialDisclosureMarketable => false
      case _                                                        => haveAllJourneysBeenCompleted(listToCheckForCompletion, ua, id, isInitialDisclosureMarketable)
    }
  }

  def isInitialDisclosureMarketable(userAnswers: UserAnswers, id: Int, historyConnector: HistoryConnector, sessionRepository: SessionRepository)(implicit
    hc: HeaderCarrier,
    executionContext: ExecutionContext
  ): Future[Boolean] = {

    val disclosureDetails = userAnswers.get(DisclosureDetailsPage, id) match {
      case Some(details) => details
      case None          => throw new Exception("Missing disclosure details")
    }

    historyConnector
      .retrieveFirstDisclosureForArrangementID(disclosureDetails.arrangementID.getOrElse(""))
      .flatMap {
        firstDisclosureDetails =>
          disclosureDetails.disclosureType match {
            case DisclosureType.Dac6add => Future.successful(firstDisclosureDetails.initialDisclosureMA)
            case DisclosureType.Dac6rep =>
              historyConnector.searchDisclosures(disclosureDetails.disclosureID.getOrElse("")).flatMap {
                submissionHistory =>
                  for {
                    userAnswers <- Future.fromTry(userAnswers.setBase(FirstInitialDisclosureMAPage, firstDisclosureDetails.initialDisclosureMA))
                    _           <- sessionRepository.set(userAnswers)
                  } yield
                    if (
                      submissionHistory.details.nonEmpty &&
                      submissionHistory.details.head.importInstruction == "Add" &&
                      firstDisclosureDetails.initialDisclosureMA
                    ) {
                      //Note: There should only be one submission returned with an ADD instruction for the given disclosure ID
                      true
                    } else {
                      false
                    }
              }
            case _ => Future.successful(false)
          }
      }
      .recoverWith {
        case _ => Future.successful(false)
      }
  }
}
