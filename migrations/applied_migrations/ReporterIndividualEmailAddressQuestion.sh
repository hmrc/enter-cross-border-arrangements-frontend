#!/bin/bash

echo ""
echo "Applying migration ReporterIndividualEmailAddressQuestion"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /reporterIndividualEmailAddressQuestion                        controllers.ReporterIndividualEmailAddressQuestionController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /reporterIndividualEmailAddressQuestion                        controllers.ReporterIndividualEmailAddressQuestionController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeReporterIndividualEmailAddressQuestion                  controllers.ReporterIndividualEmailAddressQuestionController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeReporterIndividualEmailAddressQuestion                  controllers.ReporterIndividualEmailAddressQuestionController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "reporterIndividualEmailAddressQuestion.title = reporterIndividualEmailAddressQuestion" >> ../conf/messages.en
echo "reporterIndividualEmailAddressQuestion.heading = reporterIndividualEmailAddressQuestion" >> ../conf/messages.en
echo "reporterIndividualEmailAddressQuestion.checkYourAnswersLabel = reporterIndividualEmailAddressQuestion" >> ../conf/messages.en
echo "reporterIndividualEmailAddressQuestion.error.required = Select yes if reporterIndividualEmailAddressQuestion" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryReporterIndividualEmailAddressQuestionUserAnswersEntry: Arbitrary[(ReporterIndividualEmailAddressQuestionPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[ReporterIndividualEmailAddressQuestionPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryReporterIndividualEmailAddressQuestionPage: Arbitrary[ReporterIndividualEmailAddressQuestionPage.type] =";\
    print "    Arbitrary(ReporterIndividualEmailAddressQuestionPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(ReporterIndividualEmailAddressQuestionPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def reporterIndividualEmailAddressQuestion: Option[Row] = userAnswers.get(ReporterIndividualEmailAddressQuestionPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"reporterIndividualEmailAddressQuestion.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(yesOrNo(answer)),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.ReporterIndividualEmailAddressQuestionController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"reporterIndividualEmailAddressQuestion.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration ReporterIndividualEmailAddressQuestion completed"
