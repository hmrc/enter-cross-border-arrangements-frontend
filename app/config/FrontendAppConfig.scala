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

package config

import com.google.inject.{Inject, Singleton}
import controllers.routes
import play.api.Configuration
import play.api.i18n.Lang
import play.api.mvc.Call
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

@Singleton
class FrontendAppConfig @Inject() (configuration: Configuration, servicesConfig: ServicesConfig) {

  private val contactHost = configuration.get[String]("contact-frontend.host")
  private val contactFormServiceIdentifier = "DAC6"

  lazy val countryCodeJson: String = configuration.get[String]("json.countries")
  lazy val currencyCodeJson: String = configuration.get[String]("json.currencies")
  val analyticsToken: String = configuration.get[String](s"google-analytics.token")
  val analyticsHost: String = configuration.get[String](s"google-analytics.host")
  val reportAProblemPartialUrl = s"$contactHost/contact/problem_reports_ajax?service=$contactFormServiceIdentifier"
  val reportAProblemNonJSUrl = s"$contactHost/contact/problem_reports_nonjs?service=$contactFormServiceIdentifier"
  lazy val betaFeedbackUrl = s"$contactHost/contact/beta-feedback?service=$contactFormServiceIdentifier"
  lazy val betaFeedbackUnauthenticatedUrl = s"$contactHost/contact/beta-feedback-unauthenticated?service=$contactFormServiceIdentifier"

  lazy val authUrl: String = configuration.get[Service]("auth").baseUrl
  lazy val loginUrl: String = configuration.get[String]("urls.login")
  lazy val loginContinueUrl: String = configuration.get[String]("urls.loginContinue")
  val signOutUrl: String             = configuration.get[String]("urls.logout")
  lazy val lostUTRUrl: String = "https://www.gov.uk/find-lost-utr-number"
  lazy val discloseArrangeLink: String = configuration.get[String]("urls.homepage")

  lazy val crossBorderArrangementsUrl: String = servicesConfig.baseUrl("cross-border-arrangements")
  lazy val discloseCrossBorderArrangementsFrontendUrl: String = configuration.get[Service]("microservice.services.disclose-cross-border-arrangements-frontend").baseUrl
  lazy val addressLookUpUrl: String = configuration.get[Service]("microservice.services.address-lookup").baseUrl
  lazy val taxpayersUrl: String = s"${configuration.get[Service]("microservice.services.enter-cross-border-arrangements").baseUrl}${configuration.get[String]("urls.taxpayers")}"
  lazy val intermediariesUrl: String = s"${configuration.get[Service]("microservice.services.enter-cross-border-arrangements").baseUrl}${configuration.get[String]("urls.intermediaries")}"

  lazy val hallmarksUrl: String = s"${configuration.get[Service]("microservice.services.enter-cross-border-arrangements").baseUrl}${configuration.get[String]("urls.hallmarks")}"
  lazy val hallmarksCYAUrl: String = s"${configuration.get[Service]("microservice.services.enter-cross-border-arrangements").baseUrl}${configuration.get[String]("urls.hallmarksCYA")}"
  lazy val reportersUrl: String = s"${configuration.get[Service]("microservice.services.enter-cross-border-arrangements").baseUrl}${configuration.get[String]("urls.reporters")}"
  lazy val reportersCYAUrl: String = s"${configuration.get[Service]("microservice.services.enter-cross-border-arrangements").baseUrl}${configuration.get[String]("urls.reportersCYA")}"
  lazy val arrangementsUrl: String = s"${configuration.get[Service]("microservice.services.enter-cross-border-arrangements").baseUrl}${configuration.get[String]("urls.arrangements")}"
  lazy val arrangementsCYAUrl: String = s"${configuration.get[Service]("microservice.services.enter-cross-border-arrangements").baseUrl}${configuration.get[String]("urls.arrangementsCYA")}"
  lazy val disclosureStartUrl: String = s"${configuration.get[Service]("microservice.services.enter-cross-border-arrangements").baseUrl}${configuration.get[String]("urls.disclosures")}"
  lazy val disclosureCYAUrl: String = s"${configuration.get[Service]("microservice.services.enter-cross-border-arrangements").baseUrl}${configuration.get[String]("urls.disclosuresCYA")}"

  lazy val languageTranslationEnabled: Boolean =
    configuration.get[Boolean]("microservice.services.features.welsh-translation")

  lazy val timeoutSeconds: String = configuration.get[String]("session.timeoutSeconds")
  lazy val countdownSeconds: String = configuration.get[String]("session.countdownSeconds")

  def languageMap: Map[String, Lang] = Map(
    "english" -> Lang("en"),
    "cymraeg" -> Lang("cy")
  )

  def routeToSwitchLanguage: String => Call =
    (lang: String) => routes.LanguageSwitchController.switchToLanguage(lang)
}
