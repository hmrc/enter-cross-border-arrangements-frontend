#!/bin/bash

echo ""
echo "Applying migration TaxpayerWhyReportArrangement"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /taxpayerWhyReportArrangement                        controllers.TaxpayerWhyReportArrangementController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /taxpayerWhyReportArrangement                        controllers.TaxpayerWhyReportArrangementController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeTaxpayerWhyReportArrangement                  controllers.TaxpayerWhyReportArrangementController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeTaxpayerWhyReportArrangement                  controllers.TaxpayerWhyReportArrangementController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "taxpayerWhyReportArrangement.title = Why are you reporting the arrangement as a taxpayer?" >> ../conf/messages.en
echo "taxpayerWhyReportArrangement.heading = Why are you reporting the arrangement as a taxpayer?" >> ../conf/messages.en
echo "taxpayerWhyReportArrangement.noIntermediaries = There are no intermediaries involved" >> ../conf/messages.en
echo "taxpayerWhyReportArrangement.professionalPrivilege = The intermediaries involved have legal professional privilege" >> ../conf/messages.en
echo "taxpayerWhyReportArrangement.checkYourAnswersLabel = Why are you reporting the arrangement as a taxpayer?" >> ../conf/messages.en
echo "taxpayerWhyReportArrangement.error.required = Select taxpayerWhyReportArrangement" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTaxpayerWhyReportArrangementUserAnswersEntry: Arbitrary[(TaxpayerWhyReportArrangementPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[TaxpayerWhyReportArrangementPage.type]";\
    print "        value <- arbitrary[TaxpayerWhyReportArrangement].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTaxpayerWhyReportArrangementPage: Arbitrary[TaxpayerWhyReportArrangementPage.type] =";\
    print "    Arbitrary(TaxpayerWhyReportArrangementPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTaxpayerWhyReportArrangement: Arbitrary[TaxpayerWhyReportArrangement] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(TaxpayerWhyReportArrangement.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(TaxpayerWhyReportArrangementPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def taxpayerWhyReportArrangement: Option[Row] = userAnswers.get(TaxpayerWhyReportArrangementPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"taxpayerWhyReportArrangement.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(msg\"taxpayerWhyReportArrangement.$answer\"),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.TaxpayerWhyReportArrangementController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"taxpayerWhyReportArrangement.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration TaxpayerWhyReportArrangement completed"
