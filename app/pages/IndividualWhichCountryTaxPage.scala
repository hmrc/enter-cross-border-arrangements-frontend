package pages

import play.api.libs.json.JsPath

object IndividualWhichCountryTaxPage extends QuestionPage[String] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "individualWhichCountryTax"
}
