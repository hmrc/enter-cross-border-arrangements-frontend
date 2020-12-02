#!/bin/bash

echo ""
echo "Applying migration UpdateTaxpayer"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /updateTaxpayer                        controllers.UpdateTaxpayerController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /updateTaxpayer                        controllers.UpdateTaxpayerController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeUpdateTaxpayer                  controllers.UpdateTaxpayerController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeUpdateTaxpayer                  controllers.UpdateTaxpayerController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "updateTaxpayer.title = updateTaxpayer" >> ../conf/messages.en
echo "updateTaxpayer.heading = updateTaxpayer" >> ../conf/messages.en
echo "updateTaxpayer.now = Yes, add now" >> ../conf/messages.en
echo "updateTaxpayer.later = Yes, I'll add later" >> ../conf/messages.en
echo "updateTaxpayer.checkYourAnswersLabel = updateTaxpayer" >> ../conf/messages.en
echo "updateTaxpayer.error.required = Select updateTaxpayer" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryUpdateTaxpayerUserAnswersEntry: Arbitrary[(UpdateTaxpayerPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[UpdateTaxpayerPage.type]";\
    print "        value <- arbitrary[UpdateTaxpayer].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryUpdateTaxpayerPage: Arbitrary[UpdateTaxpayerPage.type] =";\
    print "    Arbitrary(UpdateTaxpayerPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryUpdateTaxpayer: Arbitrary[UpdateTaxpayer] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(UpdateTaxpayer.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(UpdateTaxpayerPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def updateTaxpayer: Option[Row] = userAnswers.get(UpdateTaxpayerPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"updateTaxpayer.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(msg\"updateTaxpayer.$answer\"),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.UpdateTaxpayerController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"updateTaxpayer.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration UpdateTaxpayer completed"
