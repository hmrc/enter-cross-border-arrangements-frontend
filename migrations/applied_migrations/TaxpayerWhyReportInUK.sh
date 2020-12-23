#!/bin/bash

echo ""
echo "Applying migration TaxpayerWhyReportInUK"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /taxpayerWhyReportInUK                        controllers.TaxpayerWhyReportInUKController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /taxpayerWhyReportInUK                        controllers.TaxpayerWhyReportInUKController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeTaxpayerWhyReportInUK                  controllers.TaxpayerWhyReportInUKController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeTaxpayerWhyReportInUK                  controllers.TaxpayerWhyReportInUKController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "taxpayerWhyReportInUK.title = Why are you required to report this arrangement in the United Kingdom?" >> ../conf/messages.en
echo "taxpayerWhyReportInUK.heading = Why are you required to report this arrangement in the United Kingdom?" >> ../conf/messages.en
echo "taxpayerWhyReportInUK.ukTaxResident = You are tax resident in the UK" >> ../conf/messages.en
echo "taxpayerWhyReportInUK.ukPermanentEstablishment = You have a permanent establishment in the UK" >> ../conf/messages.en
echo "taxpayerWhyReportInUK.checkYourAnswersLabel = Why are you required to report this arrangement in the United Kingdom?" >> ../conf/messages.en
echo "taxpayerWhyReportInUK.error.required = Select taxpayerWhyReportInUK" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTaxpayerWhyReportInUKUserAnswersEntry: Arbitrary[(TaxpayerWhyReportInUKPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[TaxpayerWhyReportInUKPage.type]";\
    print "        value <- arbitrary[TaxpayerWhyReportInUK].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTaxpayerWhyReportInUKPage: Arbitrary[TaxpayerWhyReportInUKPage.type] =";\
    print "    Arbitrary(TaxpayerWhyReportInUKPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTaxpayerWhyReportInUK: Arbitrary[TaxpayerWhyReportInUK] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(TaxpayerWhyReportInUK.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(TaxpayerWhyReportInUKPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def taxpayerWhyReportInUK: Option[Row] = userAnswers.get(TaxpayerWhyReportInUKPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"taxpayerWhyReportInUK.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(msg\"taxpayerWhyReportInUK.$answer\"),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.TaxpayerWhyReportInUKController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"taxpayerWhyReportInUK.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration TaxpayerWhyReportInUK completed"
