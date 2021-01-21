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

import base.SpecBase
import models.{CannotStart, CompletionState, UserAnswers}

import scala.xml.{Elem, NodeSeq}

class XMLBuilderSpec extends SpecBase {

  "XMLBuilderSpec" - {

    "build either a CompletionState or an XML Element from a NodeSeq and a funcion" - {

      val builder = new XMLBuilder {
        override def toXml(userAnswers: UserAnswers): Either[CompletionState, Elem] = Left(CannotStart)
      }

      "if the conversion has no exceptions" in {

        val content = Right(NodeSeq.Empty)
        val result = builder.build(content)(nodes => <SomeTag>{nodes}</SomeTag>)
        result must be (Right(<SomeTag></SomeTag>))
      }
    }
  }

}
