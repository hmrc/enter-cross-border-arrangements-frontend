package helpers.xml

import base.SpecBase
import models.{CompletionState, UserAnswers}

import scala.xml.Elem

class XMLBuilderSpec extends SpecBase {

  "XMLBuilderSpec" - {

    "convert user answers either to a CompletionState or to an XML Element" - {

      "if the conversion has exceptions" in {

        val builder = new XMLBuilder {
          override def toXml(userAnswers: UserAnswers): Either[CompletionState, Elem] = ???
        }
      }
    }
  }

}
