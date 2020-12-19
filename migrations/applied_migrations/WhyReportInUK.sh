#!/bin/bash

echo ""
echo "Applying migration WhyReportInUK"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whyReportInUK                        controllers.WhyReportInUKController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whyReportInUK                        controllers.WhyReportInUKController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhyReportInUK                  controllers.WhyReportInUKController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhyReportInUK                  controllers.WhyReportInUKController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whyReportInUK.title = Why are you required to report this arrangement in the United Kingdom?" >> ../conf/messages.en
echo "whyReportInUK.heading = Why are you required to report this arrangement in the United Kingdom?" >> ../conf/messages.en
echo "whyReportInUK.taxResidentUk = You are tax resident in the UK" >> ../conf/messages.en
echo "whyReportInUK.permanentEstablishment = You have a permanent establishment in the UK" >> ../conf/messages.en
echo "whyReportInUK.checkYourAnswersLabel = Why are you required to report this arrangement in the United Kingdom?" >> ../conf/messages.en
echo "whyReportInUK.error.required = Select whyReportInUK" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhyReportInUKUserAnswersEntry: Arbitrary[(WhyReportInUKPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[WhyReportInUKPage.type]";\
    print "        value <- arbitrary[WhyReportInUK].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhyReportInUKPage: Arbitrary[WhyReportInUKPage.type] =";\
    print "    Arbitrary(WhyReportInUKPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhyReportInUK: Arbitrary[WhyReportInUK] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(WhyReportInUK.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(WhyReportInUKPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def whyReportInUK: Option[Row] = userAnswers.get(WhyReportInUKPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"whyReportInUK.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(msg\"whyReportInUK.$answer\"),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.WhyReportInUKController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"whyReportInUK.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration WhyReportInUK completed"
