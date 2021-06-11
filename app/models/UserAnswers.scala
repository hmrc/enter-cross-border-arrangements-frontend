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

package models

import pages._
import play.api.libs.json._
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.LocalDateTime
import scala.util.{Failure, Success, Try}

final case class UserAnswers(
                              id: String,
                              data: JsObject = Json.obj(),
                              lastUpdated: LocalDateTime = LocalDateTime.now
                            ) {

  def getBase[A](page: QuestionPage[A])(implicit rds: Reads[A]): Option[A] =
    Reads.optionNoError(Reads.at(page.path)).reads(data).getOrElse(None)

  def setBase[A](page: QuestionPage[A], value: A)(implicit writes: Writes[A]): Try[UserAnswers] = {

    val updatedData = data.setObject(page.path, Json.toJson(value)) match {
      case JsSuccess(jsValue, _) =>
        Success(jsValue)
      case JsError(errors) =>
        Failure(JsResultException(errors))
    }

    updatedData.flatMap {
      d =>
        val updatedAnswers = copy (data = d)
        page.cleanupBase(Some(value), updatedAnswers)
    }
  }

  def removeBase[A](page: QuestionPage[A]): Try[UserAnswers] = {

    val updatedData = data.setObject(page.path, JsNull) match {
      case JsSuccess(jsValue, _) =>
        Success(jsValue)
      case JsError(_) =>
        Success(data)
    }

    updatedData.flatMap {
      d =>
        val updatedAnswers = copy (data = d)
        page.cleanupBase(None, updatedAnswers)
    }
  }

  def get[A](page: UnsubmittedIndex[A])(implicit rds: Reads[A]): Option[A] =
    Reads.optionNoError(Reads.at(page.path)).reads(data).getOrElse(None)

  def get[A](page: QuestionPage[A], index: Int)(implicit rds: Reads[A]): Option[A] =
    get(UnsubmittedIndex.fromQuestionPage(page, index)(this))

  def set[A](page: UnsubmittedIndex[A], value: A)(implicit writes: Writes[A]): Try[UserAnswers] = {

    val updatedData = data.setObject(page.path, Json.toJson(value)) match {
      case JsSuccess(jsValue, _) =>
        Success(jsValue)
      case JsError(errors) =>
        Failure(JsResultException(errors))
    }

    updatedData.flatMap {
      d =>
        val updatedAnswers = copy (data = d)
        page.cleanup(Some(value), updatedAnswers)
    }
  }

  def set[A](page: QuestionPage[A], index: Int, value: A)(implicit writes: Writes[A]): Try[UserAnswers] =
    set(UnsubmittedIndex.fromQuestionPage(page, index)(this), value)

  def set[A, M](page: DetailsPage[A, M], index: Int)(implicit writes: Writes[A], model: M): Try[UserAnswers] =
    page.getFromModel(model).fold[Try[UserAnswers]](Success(this)) { value =>
      set(UnsubmittedIndex.fromQuestionPage(page, index)(this), value)
    }

  def set[A](page: LoopPage[A], index: Int)(implicit writes: Writes[A]): Try[UserAnswers] =
    set(UnsubmittedIndex.fromQuestionPage(page, index)(this), page.updatedLoopList(this, index))

  def set(loopPage: LoopDetailsPage, index: Int, position: Int)(f: LoopDetails => LoopDetails): Try[UserAnswers] = {
    val loopDetails = get(loopPage, index).fold(IndexedSeq[LoopDetails](f(LoopDetails()))) { list =>
      list
        .lift(position).map(f)
        .fold(list) { updatedLoop => list.updated(position, updatedLoop) }
    }
    set(UnsubmittedIndex.fromQuestionPage(loopPage, index)(this), loopDetails)
  }

  def set(loopPage: LoopDetailsPage, index: Int)(implicit model: WithTaxResidency): Try[UserAnswers] =
    Option(model.taxResidencies.map(LoopDetails.apply)).fold[Try[UserAnswers]](Success(this)) { value =>
      set(UnsubmittedIndex.fromQuestionPage(loopPage, index)(this), value)
    }

  def remove[A](page: UnsubmittedIndex[A]): Try[UserAnswers] = {

    val updatedData = data.setObject(page.path, JsNull) match {
      case JsSuccess(jsValue, _) =>
        Success(jsValue)
      case JsError(_) =>
        Success(data)
    }

    updatedData.flatMap {
      d =>
        val updatedAnswers = copy (data = d)
        page.cleanup(None, updatedAnswers)
    }
  }

  def remove[A](page: QuestionPage[A], index: Int): Try[UserAnswers] =
    remove(UnsubmittedIndex.fromQuestionPage(page, index)(this))

  def removeAll(pageList: Seq[_ <: QuestionPage[Any]]): Try[UserAnswers] =
    pageList.foldLeft(removeBase(pageList.head)) { case (result, step) => result.flatMap(_.removeBase(step)) }

  def hasNewValue[A](page: QuestionPage[A], id: Int, value: A)(implicit rds: Reads[A]): Boolean =
    get(page, id).exists(_ != value)

  def restoreFromLoop[A](loopPage: LoopPage[A], id: Int, itemId: Option[String])(implicit rds: Reads[A]): UserAnswers =
    itemId
      .filter(_.nonEmpty)
      .flatMap { nonEmptyItemId =>
        this
          .get(loopPage, id)
          .flatMap[A]{ _.find { _.asInstanceOf[WithRestore].matchItem(nonEmptyItemId) } }
          .map { _.asInstanceOf[WithRestore].restore(this, id).getOrElse(this) }
      }.getOrElse(this)

}

object UserAnswers {

  val format: OFormat[UserAnswers] = OFormat(reads, writes)

  import MongoJavatimeFormats.Implicits._

  implicit lazy val reads: Reads[UserAnswers] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "_id").read[String] and
      (__ \ "data").read[JsObject] and
      (__ \ "lastUpdated").read[LocalDateTime]
    ) (UserAnswers.apply _)
  }

  implicit lazy val writes: OWrites[UserAnswers] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "_id").write[String] and
      (__ \ "data").write[JsObject] and
      (__ \ "lastUpdated").write[LocalDateTime]
    ) (unlift(UserAnswers.unapply))
  }
}
