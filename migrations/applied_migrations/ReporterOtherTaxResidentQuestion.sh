#!/bin/bash

echo ""
echo "Applying migration ReporterOtherTaxResidentQuestion"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /reporterOtherTaxResidentQuestion                        controllers.ReporterOtherTaxResidentQuestionController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /reporterOtherTaxResidentQuestion                        controllers.ReporterOtherTaxResidentQuestionController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeReporterOtherTaxResidentQuestion                  controllers.ReporterOtherTaxResidentQuestionController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeReporterOtherTaxResidentQuestion                  controllers.ReporterOtherTaxResidentQuestionController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "reporterOtherTaxResidentQuestion.title = reporterOtherTaxResidentQuestion" >> ../conf/messages.en
echo "reporterOtherTaxResidentQuestion.heading = reporterOtherTaxResidentQuestion" >> ../conf/messages.en
echo "reporterOtherTaxResidentQuestion.checkYourAnswersLabel = reporterOtherTaxResidentQuestion" >> ../conf/messages.en
echo "reporterOtherTaxResidentQuestion.error.required = Select yes if reporterOtherTaxResidentQuestion" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryReporterOtherTaxResidentQuestionUserAnswersEntry: Arbitrary[(ReporterOtherTaxResidentQuestionPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[ReporterOtherTaxResidentQuestionPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryReporterOtherTaxResidentQuestionPage: Arbitrary[ReporterOtherTaxResidentQuestionPage.type] =";\
    print "    Arbitrary(ReporterOtherTaxResidentQuestionPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(ReporterOtherTaxResidentQuestionPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def reporterOtherTaxResidentQuestion: Option[Row] = userAnswers.get(ReporterOtherTaxResidentQuestionPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"reporterOtherTaxResidentQuestion.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(yesOrNo(answer)),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.ReporterOtherTaxResidentQuestionController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"reporterOtherTaxResidentQuestion.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration ReporterOtherTaxResidentQuestion completed"
