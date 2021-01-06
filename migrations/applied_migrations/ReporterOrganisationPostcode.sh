#!/bin/bash

echo ""
echo "Applying migration ReporterOrganisationPostcode"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /reporterOrganisationPostcode                        controllers.ReporterOrganisationPostcodeController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /reporterOrganisationPostcode                        controllers.ReporterOrganisationPostcodeController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeReporterOrganisationPostcode                  controllers.ReporterOrganisationPostcodeController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeReporterOrganisationPostcode                  controllers.ReporterOrganisationPostcodeController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "reporterOrganisationPostcode.title = reporterOrganisationPostcode" >> ../conf/messages.en
echo "reporterOrganisationPostcode.heading = reporterOrganisationPostcode" >> ../conf/messages.en
echo "reporterOrganisationPostcode.checkYourAnswersLabel = reporterOrganisationPostcode" >> ../conf/messages.en
echo "reporterOrganisationPostcode.error.required = Enter reporterOrganisationPostcode" >> ../conf/messages.en
echo "reporterOrganisationPostcode.error.length = ReporterOrganisationPostcode must be 10 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryReporterOrganisationPostcodeUserAnswersEntry: Arbitrary[(ReporterOrganisationPostcodePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[ReporterOrganisationPostcodePage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryReporterOrganisationPostcodePage: Arbitrary[ReporterOrganisationPostcodePage.type] =";\
    print "    Arbitrary(ReporterOrganisationPostcodePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(ReporterOrganisationPostcodePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def reporterOrganisationPostcode: Option[Row] = userAnswers.get(ReporterOrganisationPostcodePage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"reporterOrganisationPostcode.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(lit\"$answer\"),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.ReporterOrganisationPostcodeController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"reporterOrganisationPostcode.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration ReporterOrganisationPostcode completed"
