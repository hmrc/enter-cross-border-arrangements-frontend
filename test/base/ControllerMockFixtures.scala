/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package base

import controllers.actions._
import models.UserAnswers
import navigation.{FakeNavigator, Navigator}
import org.mockito.{Mockito, MockitoSugar}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{BeforeAndAfterEach, TestSuite}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.Call
import repositories.SessionRepository
import uk.gov.hmrc.nunjucks.NunjucksRenderer

trait ControllerMockFixtures extends AnyFreeSpec with Matchers with GuiceOneAppPerSuite with MockitoSugar with BeforeAndAfterEach {
  self: TestSuite =>

  def onwardRoute = Call("GET", "/foo")
  final val mockRenderer: NunjucksRenderer = mock[NunjucksRenderer]
  final val mockDataRetrievalAction: DataRetrievalAction = mock[DataRetrievalAction]
  final val mockSessionRepository: SessionRepository = mock[SessionRepository]
  protected val fakeNavigator: Navigator = new FakeNavigator(onwardRoute)

  override def beforeEach {
    Mockito.reset(
      mockRenderer,
      mockSessionRepository,
      mockDataRetrievalAction
    )
    super.beforeEach()
  }

  protected def retrieveUserAnswersData(userAnswers: UserAnswers): Unit = {
    when(mockDataRetrievalAction.apply()).thenReturn(new FakeDataRetrievalAction(Some(userAnswers)))
  }

  protected def retrieveNoData(): Unit = {
    when(mockDataRetrievalAction.apply()).thenReturn(new FakeDataRetrievalAction(None))
  }

  override def fakeApplication(): Application =
    guiceApplicationBuilder()
      .build()

  // Override to provide custom binding
  def guiceApplicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[DataRequiredAction].to[DataRequiredActionImpl],
        bind[IdentifierAction].to[FakeIdentifierAction],
        bind[DataRetrievalAction].toInstance(mockDataRetrievalAction),
        bind[NunjucksRenderer].toInstance(mockRenderer),
        bind[SessionRepository].toInstance(mockSessionRepository),
        bind[Navigator].toInstance(fakeNavigator)
      )

  //@deprecated("Use guiceApplicationBuilder() instead", "")
  protected def applicationBuilder(userAnswers: Option[UserAnswers] = None): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[DataRequiredAction].to[DataRequiredActionImpl],
        bind[IdentifierAction].to[FakeIdentifierAction],
        bind[DataRetrievalAction].toInstance(new FakeDataRetrievalActionProvider(userAnswers)),
        bind[NunjucksRenderer].toInstance(mockRenderer)
      )

}
