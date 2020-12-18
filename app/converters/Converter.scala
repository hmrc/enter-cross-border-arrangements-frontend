package converters

import converters.Converter.PageList
import pages.QuestionPage

case class Converter[A](pageList: PageList, op: (A, PageList) => A = Converter.nop)

object Converter {

  type PageList = Seq[_ <: QuestionPage[Any]]

  def nop[A]: (A, PageList) => A = {
    case (input, _) => input
  }

}
