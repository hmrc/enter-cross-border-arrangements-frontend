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

package services

import scala.util.{Failure, Success, Try}
import scala.xml.transform.{RewriteRule, RuleTransformer}
import scala.xml.{Elem, Node, NodeSeq}

class TransformationService {

  def build(xml: Elem, messageRefId: String, enrolmentID: String): Try[NodeSeq] =
    (for {
      uniqueXmlSubmission <- rewriteMessageRefID(xml, messageRefId)
      submission          <- constructSubmission("manual-submission.xml", enrolmentID, uniqueXmlSubmission)
    } yield Success(submission)).getOrElse(Failure(new IllegalStateException("Unable to build submission")))

  def rewriteMessageRefID(xml: Elem, messageRefID: String): Option[NodeSeq] =
    new RuleTransformer(new RewriteRule {

      override def transform(n: Node): Seq[Node] = n match {
        case Elem(_, "MessageRefId", _, _, _*) =>
          <MessageRefId>{messageRefID}</MessageRefId>
        case other => other
      }
    }).transform(xml).headOption

  def constructSubmission(fileName: String, enrolmentID: String, document: NodeSeq): Option[NodeSeq] = {
    val submission =
      <submission>
        <fileName>{fileName}</fileName>
        <enrolmentID>{enrolmentID}</enrolmentID>
        <file></file>
      </submission>

    new RuleTransformer(new RewriteRule {
      override def transform(n: Node): Seq[Node] = n match {
        case elem: Elem if elem.label == "file" =>
          elem.copy(child = document)
        case other => other
      }
    }).transform(submission).headOption
  }

}
