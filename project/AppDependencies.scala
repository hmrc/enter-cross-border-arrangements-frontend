import sbt._

object AppDependencies {
  import play.core.PlayVersion

  val compile = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-27"               % "0.50.0",
    "uk.gov.hmrc"       %% "logback-json-logger"              % "5.1.0",
    "uk.gov.hmrc"       %% "play-conditional-form-mapping"    % "1.9.0-play-28",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-28"       % "5.12.0",
    "uk.gov.hmrc"       %% "bootstrap-health-play-28"         % "5.3.0",
    "uk.gov.hmrc"       %% "play-nunjucks"                    % "0.28.0-play-27",
    "uk.gov.hmrc"       %% "play-nunjucks-viewmodel"          % "0.14.0-play-27",
    "uk.gov.hmrc"       %% "play-frontend-hmrc"               % "3.0.0-play-28",
    "uk.gov.hmrc"       %% "emailaddress"                     % "3.5.0"
  )

  val test = Seq(
    "org.scalatest"               %% "scalatest"               % "3.2.9",
    "org.scalatestplus.play"      %% "scalatestplus-play"      % "5.1.0",
    "org.pegdown"                 %  "pegdown"                 % "1.6.0",
    "org.jsoup"                   %  "jsoup"                   % "1.10.3",
    "com.typesafe.play"           %% "play-test"               % PlayVersion.current,
    "org.mockito"                 %% "mockito-scala"           % "1.16.34" ,
    "org.scalatestplus"           %% "scalatestplus-scalacheck"  % "3.1.0.0-RC2",
    "com.github.tomakehurst"      %  "wiremock-standalone"     % "2.27.0",
    "wolfendale"                  %% "scalacheck-gen-regexp"   % "0.1.2",
    "uk.gov.hmrc.mongo"           %% "hmrc-mongo-test-play-27" % "0.50.0"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test
}
