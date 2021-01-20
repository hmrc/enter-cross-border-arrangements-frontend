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
        result must be (content)
      }
    }
  }

}
