#!/bin/bash

echo ""
echo "Applying migration ReporterTinNonUKQuestion"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /reporterTinNonUKQuestion                        controllers.ReporterTinNonUKQuestionController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /reporterTinNonUKQuestion                        controllers.ReporterTinNonUKQuestionController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeReporterTinNonUKQuestion                  controllers.ReporterTinNonUKQuestionController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeReporterTinNonUKQuestion                  controllers.ReporterTinNonUKQuestionController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "reporterTinNonUKQuestion.title = reporterTinNonUKQuestion" >> ../conf/messages.en
echo "reporterTinNonUKQuestion.heading = reporterTinNonUKQuestion" >> ../conf/messages.en
echo "reporterTinNonUKQuestion.checkYourAnswersLabel = reporterTinNonUKQuestion" >> ../conf/messages.en
echo "reporterTinNonUKQuestion.error.required = Select yes if reporterTinNonUKQuestion" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryReporterTinNonUKQuestionUserAnswersEntry: Arbitrary[(ReporterTinNonUKQuestionPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[ReporterTinNonUKQuestionPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryReporterTinNonUKQuestionPage: Arbitrary[ReporterTinNonUKQuestionPage.type] =";\
    print "    Arbitrary(ReporterTinNonUKQuestionPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(ReporterTinNonUKQuestionPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def reporterTinNonUKQuestion: Option[Row] = userAnswers.get(ReporterTinNonUKQuestionPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"reporterTinNonUKQuestion.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(yesOrNo(answer)),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.ReporterTinNonUKQuestionController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"reporterTinNonUKQuestion.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration ReporterTinNonUKQuestion completed"
