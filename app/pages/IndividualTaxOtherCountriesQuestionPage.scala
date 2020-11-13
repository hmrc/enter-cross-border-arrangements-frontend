package pages

import play.api.libs.json.JsPath

object IndividualTaxOtherCountriesQuestionPage extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "individualUkTaxNumbersQuestionPage"
}
