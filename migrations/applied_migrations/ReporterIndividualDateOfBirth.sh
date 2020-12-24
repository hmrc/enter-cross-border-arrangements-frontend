#!/bin/bash

echo ""
echo "Applying migration ReporterIndividualDateOfBirth"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /reporterIndividualDateOfBirth                  controllers.ReporterIndividualDateOfBirthController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /reporterIndividualDateOfBirth                  controllers.ReporterIndividualDateOfBirthController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeReporterIndividualDateOfBirth                        controllers.ReporterIndividualDateOfBirthController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeReporterIndividualDateOfBirth                        controllers.ReporterIndividualDateOfBirthController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "reporterIndividualDateOfBirth.title = ReporterIndividualDateOfBirth" >> ../conf/messages.en
echo "reporterIndividualDateOfBirth.heading = ReporterIndividualDateOfBirth" >> ../conf/messages.en
echo "reporterIndividualDateOfBirth.hint = For example, 12 11 2007" >> ../conf/messages.en
echo "reporterIndividualDateOfBirth.checkYourAnswersLabel = ReporterIndividualDateOfBirth" >> ../conf/messages.en
echo "reporterIndividualDateOfBirth.error.required.all = Enter the reporterIndividualDateOfBirth" >> ../conf/messages.en
echo "reporterIndividualDateOfBirth.error.required.two = The reporterIndividualDateOfBirth" must include {0} and {1} >> ../conf/messages.en
echo "reporterIndividualDateOfBirth.error.required = The reporterIndividualDateOfBirth must include {0}" >> ../conf/messages.en
echo "reporterIndividualDateOfBirth.error.invalid = Enter a real ReporterIndividualDateOfBirth" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryReporterIndividualDateOfBirthUserAnswersEntry: Arbitrary[(ReporterIndividualDateOfBirthPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[ReporterIndividualDateOfBirthPage.type]";\
    print "        value <- arbitrary[Int].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryReporterIndividualDateOfBirthPage: Arbitrary[ReporterIndividualDateOfBirthPage.type] =";\
    print "    Arbitrary(ReporterIndividualDateOfBirthPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(ReporterIndividualDateOfBirthPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def reporterIndividualDateOfBirth: Option[Row] = userAnswers.get(ReporterIndividualDateOfBirthPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"reporterIndividualDateOfBirth.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(Literal(answer.format(dateFormatter))),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.ReporterIndividualDateOfBirthController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"reporterIndividualDateOfBirth.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration ReporterIndividualDateOfBirth completed"
