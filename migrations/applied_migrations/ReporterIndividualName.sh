#!/bin/bash

echo ""
echo "Applying migration ReporterIndividualName"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /reporterIndividualName                        controllers.ReporterIndividualNameController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /reporterIndividualName                        controllers.ReporterIndividualNameController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeReporterIndividualName                  controllers.ReporterIndividualNameController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeReporterIndividualName                  controllers.ReporterIndividualNameController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "reporterIndividualName.title = reporterIndividualName" >> ../conf/messages.en
echo "reporterIndividualName.heading = reporterIndividualName" >> ../conf/messages.en
echo "reporterIndividualName.firstName = firstName" >> ../conf/messages.en
echo "reporterIndividualName.lastName = lastName" >> ../conf/messages.en
echo "reporterIndividualName.checkYourAnswersLabel = reporterIndividualName" >> ../conf/messages.en
echo "reporterIndividualName.error.firstName.required = Enter firstName" >> ../conf/messages.en
echo "reporterIndividualName.error.lastName.required = Enter lastName" >> ../conf/messages.en
echo "reporterIndividualName.error.firstName.length = firstName must be 100 characters or less" >> ../conf/messages.en
echo "reporterIndividualName.error.lastName.length = lastName must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryReporterIndividualNameUserAnswersEntry: Arbitrary[(ReporterIndividualNamePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[ReporterIndividualNamePage.type]";\
    print "        value <- arbitrary[ReporterIndividualName].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryReporterIndividualNamePage: Arbitrary[ReporterIndividualNamePage.type] =";\
    print "    Arbitrary(ReporterIndividualNamePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryReporterIndividualName: Arbitrary[ReporterIndividualName] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        firstName <- arbitrary[String]";\
    print "        lastName <- arbitrary[String]";\
    print "      } yield ReporterIndividualName(firstName, lastName)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(ReporterIndividualNamePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def reporterIndividualName: Option[Row] = userAnswers.get(ReporterIndividualNamePage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"reporterIndividualName.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(lit\"${answer.firstName} ${answer.lastName}\"),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.ReporterIndividualNameController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"reporterIndividualName.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration ReporterIndividualName completed"
