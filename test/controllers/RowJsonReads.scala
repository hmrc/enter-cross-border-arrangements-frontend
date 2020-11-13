package controllers

import play.api.libs.functional.syntax._
import play.api.libs.json.{Reads, __}
import uk.gov.hmrc.viewmodels.Html
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels.Text.Literal

object RowJsonReads {

  implicit val keyReads: Reads[Key] = (
    (__ \ "text").read[String] and
      (__ \ "classes").readNullable[String]
    ) { (text, _) => Key(Literal(text)) }

  implicit val valueReads: Reads[Value] = (
    (__ \ "text").readNullable[String] and
      (__ \ "html").readNullable[String] and
      (__ \ "classes").readNullable[String]
    ){ (text, html, _) =>
    (text, html) match {
      case (Some(t), None) => Value(Literal(t))
      case (None, Some(h)) => Value(Html(h))
      case _ => Value(Literal("INVALID"))
    }

  }

  implicit val actionReads: Reads[Action] = (
    (__ \ "href").read[String] and
      (__ \ "text").read[String]
    ){ (href, text) => Action(Literal(text), href) }

  implicit val rowReads: Reads[Row] = (
    (__ \ "key").read[Key] and
      (__ \ "value").read[Value] and
      (__ \ "actions" \ "items").read[Seq[Action]]
    ){ (key, value, actions) => Row(key, value, actions) }

}
