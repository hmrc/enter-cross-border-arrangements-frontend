#!/bin/bash

echo ""
echo "Applying migration DoYouKnowTheReasonToReportArrangementNow"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /doYouKnowTheReasonToReportArrangementNow                        controllers.DoYouKnowTheReasonToReportArrangementNowController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /doYouKnowTheReasonToReportArrangementNow                        controllers.DoYouKnowTheReasonToReportArrangementNowController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeDoYouKnowTheReasonToReportArrangementNow                  controllers.DoYouKnowTheReasonToReportArrangementNowController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeDoYouKnowTheReasonToReportArrangementNow                  controllers.DoYouKnowTheReasonToReportArrangementNowController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "doYouKnowTheReasonToReportArrangementNow.title = doYouKnowTheReasonToReportArrangementNow" >> ../conf/messages.en
echo "doYouKnowTheReasonToReportArrangementNow.heading = doYouKnowTheReasonToReportArrangementNow" >> ../conf/messages.en
echo "doYouKnowTheReasonToReportArrangementNow.checkYourAnswersLabel = doYouKnowTheReasonToReportArrangementNow" >> ../conf/messages.en
echo "doYouKnowTheReasonToReportArrangementNow.error.required = Select yes if doYouKnowTheReasonToReportArrangementNow" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryDoYouKnowTheReasonToReportArrangementNowUserAnswersEntry: Arbitrary[(DoYouKnowTheReasonToReportArrangementNowPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[DoYouKnowTheReasonToReportArrangementNowPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryDoYouKnowTheReasonToReportArrangementNowPage: Arbitrary[DoYouKnowTheReasonToReportArrangementNowPage.type] =";\
    print "    Arbitrary(DoYouKnowTheReasonToReportArrangementNowPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(DoYouKnowTheReasonToReportArrangementNowPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def doYouKnowTheReasonToReportArrangementNow: Option[Row] = userAnswers.get(DoYouKnowTheReasonToReportArrangementNowPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"doYouKnowTheReasonToReportArrangementNow.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(yesOrNo(answer)),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.DoYouKnowTheReasonToReportArrangementNowController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"doYouKnowTheReasonToReportArrangementNow.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration DoYouKnowTheReasonToReportArrangementNow completed"
