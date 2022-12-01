import sbt._

object AppDependencies {
  import play.core.PlayVersion

  val compile = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28"               % "0.74.0",
    "uk.gov.hmrc"       %% "play-conditional-form-mapping"    % "1.12.0-play-28",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-28"       % "7.11.0",
    "uk.gov.hmrc"       %% "play-nunjucks"                    % "0.40.0-play-28",
    "uk.gov.hmrc"       %% "play-nunjucks-viewmodel"          % "0.16.0-play-28",
    "uk.gov.hmrc"       %% "play-frontend-hmrc"               % "3.33.0-play-28"
  )

  val test = Seq(
    "org.scalatest"               %% "scalatest"               % "3.2.14",
    "org.scalatestplus.play"      %% "scalatestplus-play"      % "5.1.0",
    "org.pegdown"                 %  "pegdown"                 % "1.6.0",
    "org.jsoup"                   %  "jsoup"                   % "1.15.3",
    "com.typesafe.play"           %% "play-test"               % PlayVersion.current,
    "org.mockito"                 %% "mockito-scala"           % "1.17.12" ,
    "org.scalatestplus"           %% "scalacheck-1-17"           % "3.2.14.0",
    "com.github.tomakehurst"      %  "wiremock-standalone"     % "2.27.2",
    "wolfendale"                  %% "scalacheck-gen-regexp"   % "0.1.2",
    "uk.gov.hmrc.mongo"           %% "hmrc-mongo-test-play-28" % "0.74.0",
    "com.vladsch.flexmark"        %  "flexmark-all"              % "0.64.0"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test
}
