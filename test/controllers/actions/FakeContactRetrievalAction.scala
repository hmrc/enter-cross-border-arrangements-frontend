/*
 * Copyright 2022 HM Revenue & Customs
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

package controllers.actions

import models.UserAnswers
import models.requests.{DataRequest, DataRequestWithContacts}
import models.subscription.ContactDetails
import play.api.mvc.ActionTransformer

import scala.concurrent.{ExecutionContext, Future}

class FakeContactRetrievalProvider(dataToReturn: UserAnswers, contactDetails: Option[ContactDetails]) extends ContactRetrievalAction {

  def apply(): ActionTransformer[DataRequest, DataRequestWithContacts] =
    new FakeContactRetrievalAction(dataToReturn, contactDetails)
}

class FakeContactRetrievalAction(dataToReturn: UserAnswers, contactDetails: Option[ContactDetails])
    extends ActionTransformer[DataRequest, DataRequestWithContacts] {

  override protected def transform[A](request: DataRequest[A]): Future[DataRequestWithContacts[A]] =
    Future(DataRequestWithContacts(request.request, request.internalId, request.enrolmentID, dataToReturn, contactDetails))

  implicit override protected val executionContext: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global
}
