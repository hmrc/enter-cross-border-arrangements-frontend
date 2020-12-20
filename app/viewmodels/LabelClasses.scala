package viewmodels

import play.api.i18n.Messages
import play.api.libs.functional.syntax._
import play.api.libs.json.{OWrites, _}


final case class LabelClasses(classes: Seq[String] = Seq.empty,
                              attributes: Map[String, String] = Map.empty)
object LabelClasses {

  implicit def writes(implicit messages: Messages): OWrites[LabelClasses] = (
    (__ \ "classes").writeNullable[String] and
      (__ \ "attributes").writeNullable[Map[String, String]]
    ){ labelClasses =>
    val attributes = Some(labelClasses.attributes).filter(_.nonEmpty)
    (classes(labelClasses.classes), attributes)}


  private def classes(classes: Seq[String]): Option[String] =
    if (classes.isEmpty) None else Some(classes.mkString(" "))
}