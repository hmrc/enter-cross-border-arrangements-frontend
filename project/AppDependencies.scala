import sbt._

object AppDependencies {
  import play.core.PlayVersion

  private val bootstrapVersion = "7.15.0"

  val compile = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"       %% "play-frontend-hmrc"               % "7.3.0-play-28",
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28"               % "1.1.0",
    "uk.gov.hmrc"       %% "play-conditional-form-mapping"    % "1.13.0-play-28",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-28"       % bootstrapVersion,
    "uk.gov.hmrc"       %% "play-nunjucks"                    % "0.43.0-play-28",
    "uk.gov.hmrc"       %% "play-nunjucks-viewmodel"          % "0.18.0-play-28"
  )

  val test = Seq(
    "uk.gov.hmrc"                 %% "bootstrap-test-play-28"  % bootstrapVersion,
    "org.pegdown"                 %  "pegdown"                 % "1.6.0",
    "org.jsoup"                   %  "jsoup"                   % "1.15.3",
    "com.typesafe.play"           %% "play-test"               % PlayVersion.current,
    "org.mockito"                 %% "mockito-scala"           % "1.17.12" ,
    "org.scalatestplus"           %% "scalacheck-1-17"         % "3.2.14.0",
    "com.github.tomakehurst"      %  "wiremock-standalone"     % "2.27.2",
    "wolfendale"                  %% "scalacheck-gen-regexp"   % "0.1.2",
    "uk.gov.hmrc.mongo"           %% "hmrc-mongo-test-play-28" % "0.74.0",
    "com.vladsch.flexmark"        %  "flexmark-all"            % "0.64.0"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test
}
