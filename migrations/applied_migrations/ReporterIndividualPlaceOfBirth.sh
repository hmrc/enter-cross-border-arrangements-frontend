#!/bin/bash

echo ""
echo "Applying migration ReporterIndividualPlaceOfBirth"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /reporterIndividualPlaceOfBirth                        controllers.ReporterIndividualPlaceOfBirthController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /reporterIndividualPlaceOfBirth                        controllers.ReporterIndividualPlaceOfBirthController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeReporterIndividualPlaceOfBirth                  controllers.ReporterIndividualPlaceOfBirthController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeReporterIndividualPlaceOfBirth                  controllers.ReporterIndividualPlaceOfBirthController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "reporterIndividualPlaceOfBirth.title = reporterIndividualPlaceOfBirth" >> ../conf/messages.en
echo "reporterIndividualPlaceOfBirth.heading = reporterIndividualPlaceOfBirth" >> ../conf/messages.en
echo "reporterIndividualPlaceOfBirth.checkYourAnswersLabel = reporterIndividualPlaceOfBirth" >> ../conf/messages.en
echo "reporterIndividualPlaceOfBirth.error.required = Enter reporterIndividualPlaceOfBirth" >> ../conf/messages.en
echo "reporterIndividualPlaceOfBirth.error.length = ReporterIndividualPlaceOfBirth must be 200 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryReporterIndividualPlaceOfBirthUserAnswersEntry: Arbitrary[(ReporterIndividualPlaceOfBirthPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[ReporterIndividualPlaceOfBirthPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryReporterIndividualPlaceOfBirthPage: Arbitrary[ReporterIndividualPlaceOfBirthPage.type] =";\
    print "    Arbitrary(ReporterIndividualPlaceOfBirthPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(ReporterIndividualPlaceOfBirthPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def reporterIndividualPlaceOfBirth: Option[Row] = userAnswers.get(ReporterIndividualPlaceOfBirthPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"reporterIndividualPlaceOfBirth.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(lit\"$answer\"),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.ReporterIndividualPlaceOfBirthController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"reporterIndividualPlaceOfBirth.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration ReporterIndividualPlaceOfBirth completed"
