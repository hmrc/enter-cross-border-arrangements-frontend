package pages.$package$

import play.api.libs.json.JsPath
import pages.QuestionPage

case object $className$Page extends QuestionPage[Boolean] {
  
  override def path: JsPath = JsPath \ toString
  
  override def toString: String = "$className;format="decap"$"
}
