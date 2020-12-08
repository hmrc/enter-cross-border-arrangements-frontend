package pages.$package$

import models.$package$.$className$
import play.api.libs.json.JsPath
import pages._

case object $className$Page extends QuestionPage[$className$] {
  
  override def path: JsPath = JsPath \ toString
  
  override def toString: String = "$className;format="decap"$"
}
