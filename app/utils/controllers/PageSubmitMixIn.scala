package utils.controllers

import controllers.routes
import models.{Mode, UserAnswers}
import play.api.mvc.Call

import scala.util.Try

trait PageSubmitMixIn[A] {

  val setPage: UserAnswers => A => Try[UserAnswers]

  val failOnSubmit: Call = routes.SessionExpiredController.onPageLoad()

  def redirect(mode: Mode, value: Option[A], index: Int = 0, alternative: Boolean = false): Call

}
