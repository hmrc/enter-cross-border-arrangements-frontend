package navigation

import controllers.routes
import models.{CheckMode, Mode, NormalMode, UserAnswers}
import pages.Page
import play.api.mvc.{AnyContent, Call, Request}

abstract class AbstractNavigator {

  private[navigation] val normalRoutes:  Page => UserAnswers => Request[AnyContent] => Option[Call]
  private[navigation] val checkRouteMap: Page => UserAnswers => Request[AnyContent] => Option[Call]

  def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers)(implicit request: Request[AnyContent]): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)(request) match {
        case Some(call) => call
        case None => routes.SessionExpiredController.onPageLoad()
      }
    case CheckMode =>
      checkRouteMap(page)(userAnswers)(request) match {
        case Some(call) => call
        case None => routes.SessionExpiredController.onPageLoad()

      }
  }
}
