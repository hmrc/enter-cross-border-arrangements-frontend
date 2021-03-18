package pages.taxpayer

import pages.QuestionPage
import play.api.libs.json.JsPath

case object RemoveTaxpayerPage extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "removeTaxpayer"
}
