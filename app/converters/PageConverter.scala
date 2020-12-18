package converters

import models.UserAnswers

import scala.util.Try

trait PageConverter[A] {

  val converters: UserAnswers => Seq[Converter[A]]

  val clean: UserAnswers => Try[UserAnswers] = { userAnswers =>

    val pageList = converters(userAnswers).flatMap(_.pageList)
    pageList.foldLeft(userAnswers.remove(pageList.head)) { case (result, step) => result.flatMap(_.remove(step)) }
  }

  val convert: A => Seq[Converter[A]] => A =
    seed => _.foldLeft[A](seed) { case (s, converter) => converter.op(s, converter.pageList) }

}
