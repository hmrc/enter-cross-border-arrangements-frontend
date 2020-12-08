package pages.$package$

import java.time.LocalDate
import pages._

import play.api.libs.json.JsPath

case object $className$Page extends QuestionPage[LocalDate] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "$className;format="decap"$"
}
