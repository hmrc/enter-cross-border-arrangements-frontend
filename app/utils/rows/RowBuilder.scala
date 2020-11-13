package utils.rows

import java.time.format.DateTimeFormatter

import models.UserAnswers
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels.{Content, MessageInterpolators}
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}

trait RowBuilder {

  implicit val messages: Messages
  val userAnswers: UserAnswers
  val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")

  private[utils] def yesOrNo(answer: Boolean): Content =
    if (answer) {
      msg"site.yes"
    } else {
      msg"site.no"
    }

  private[utils] def toRow(msgKey: String, content: Content, href: String)(implicit messages: Messages): Row = {
    val message = MessageInterpolators(StringContext.apply(s"$msgKey.checkYourAnswersLabel")).msg()
    Row(
      key     = Key(message, classes = Seq("govuk-!-width-one-half")),
      value   = Value(content),
      actions = List(
        Action(
          content            = msg"site.edit",
          href               = href,
          visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(message))
        )
      )
    )
  }

}
