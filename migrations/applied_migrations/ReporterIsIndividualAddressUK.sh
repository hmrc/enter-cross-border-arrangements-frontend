#!/bin/bash

echo ""
echo "Applying migration ReporterIsIndividualAddressUK"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /reporterIsIndividualAddressUK                        controllers.ReporterIsIndividualAddressUKController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /reporterIsIndividualAddressUK                        controllers.ReporterIsIndividualAddressUKController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeReporterIsIndividualAddressUK                  controllers.ReporterIsIndividualAddressUKController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeReporterIsIndividualAddressUK                  controllers.ReporterIsIndividualAddressUKController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "reporterIsIndividualAddressUK.title = reporterIsIndividualAddressUK" >> ../conf/messages.en
echo "reporterIsIndividualAddressUK.heading = reporterIsIndividualAddressUK" >> ../conf/messages.en
echo "reporterIsIndividualAddressUK.checkYourAnswersLabel = reporterIsIndividualAddressUK" >> ../conf/messages.en
echo "reporterIsIndividualAddressUK.error.required = Select yes if reporterIsIndividualAddressUK" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryReporterIsIndividualAddressUKUserAnswersEntry: Arbitrary[(ReporterIsIndividualAddressUKPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[ReporterIsIndividualAddressUKPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryReporterIsIndividualAddressUKPage: Arbitrary[ReporterIsIndividualAddressUKPage.type] =";\
    print "    Arbitrary(ReporterIsIndividualAddressUKPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(ReporterIsIndividualAddressUKPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def reporterIsIndividualAddressUK: Option[Row] = userAnswers.get(ReporterIsIndividualAddressUKPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"reporterIsIndividualAddressUK.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(yesOrNo(answer)),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.ReporterIsIndividualAddressUKController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"reporterIsIndividualAddressUK.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration ReporterIsIndividualAddressUK completed"
