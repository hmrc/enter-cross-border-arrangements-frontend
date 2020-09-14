#!/bin/bash

echo ""
echo "Applying migration MeetMainBenefitTest"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /meetMainBenefitTest                        controllers.MeetMainBenefitTestController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /meetMainBenefitTest                        controllers.MeetMainBenefitTestController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeMeetMainBenefitTest                  controllers.MeetMainBenefitTestController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeMeetMainBenefitTest                  controllers.MeetMainBenefitTestController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "meetMainBenefitTest.title = meetMainBenefitTest" >> ../conf/messages.en
echo "meetMainBenefitTest.heading = meetMainBenefitTest" >> ../conf/messages.en
echo "meetMainBenefitTest.checkYourAnswersLabel = meetMainBenefitTest" >> ../conf/messages.en
echo "meetMainBenefitTest.error.required = Select yes if meetMainBenefitTest" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryMeetMainBenefitTestUserAnswersEntry: Arbitrary[(MeetMainBenefitTestPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[MeetMainBenefitTestPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryMeetMainBenefitTestPage: Arbitrary[MeetMainBenefitTestPage.type] =";\
    print "    Arbitrary(MeetMainBenefitTestPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(MeetMainBenefitTestPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def meetMainBenefitTest: Option[Row] = userAnswers.get(MeetMainBenefitTestPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"meetMainBenefitTest.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(yesOrNo(answer)),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.MeetMainBenefitTestController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"meetMainBenefitTest.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration MeetMainBenefitTest completed"
