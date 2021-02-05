package pages.disclosure

import pages.QuestionPage
import play.api.libs.json.JsPath

case object RemoveDisclosurePage extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "removeDisclosure"
}
