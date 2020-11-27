#!/bin/bash

echo ""
echo "Applying migration WhyAreYouReportingThisArrangementNow"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whyAreYouReportingThisArrangementNow                        controllers.WhyAreYouReportingThisArrangementNowController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whyAreYouReportingThisArrangementNow                        controllers.WhyAreYouReportingThisArrangementNowController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhyAreYouReportingThisArrangementNow                  controllers.WhyAreYouReportingThisArrangementNowController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhyAreYouReportingThisArrangementNow                  controllers.WhyAreYouReportingThisArrangementNowController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whyAreYouReportingThisArrangementNow.title = Why are you reporting this arrangement now?" >> ../conf/messages.en
echo "whyAreYouReportingThisArrangementNow.heading = Why are you reporting this arrangement now?" >> ../conf/messages.en
echo "whyAreYouReportingThisArrangementNow.dAC6701 = The arrangement is available for implementation" >> ../conf/messages.en
echo "whyAreYouReportingThisArrangementNow.dAC6702 = Arrangement is ready for implementation" >> ../conf/messages.en
echo "whyAreYouReportingThisArrangementNow.checkYourAnswersLabel = Why are you reporting this arrangement now?" >> ../conf/messages.en
echo "whyAreYouReportingThisArrangementNow.error.required = Select whyAreYouReportingThisArrangementNow" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhyAreYouReportingThisArrangementNowUserAnswersEntry: Arbitrary[(WhyAreYouReportingThisArrangementNowPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[WhyAreYouReportingThisArrangementNowPage.type]";\
    print "        value <- arbitrary[WhyAreYouReportingThisArrangementNow].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhyAreYouReportingThisArrangementNowPage: Arbitrary[WhyAreYouReportingThisArrangementNowPage.type] =";\
    print "    Arbitrary(WhyAreYouReportingThisArrangementNowPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhyAreYouReportingThisArrangementNow: Arbitrary[WhyAreYouReportingThisArrangementNow] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(WhyAreYouReportingThisArrangementNow.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(WhyAreYouReportingThisArrangementNowPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def whyAreYouReportingThisArrangementNow: Option[Row] = userAnswers.get(WhyAreYouReportingThisArrangementNowPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"whyAreYouReportingThisArrangementNow.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(msg\"whyAreYouReportingThisArrangementNow.$answer\"),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.WhyAreYouReportingThisArrangementNowController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"whyAreYouReportingThisArrangementNow.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration WhyAreYouReportingThisArrangementNow completed"
