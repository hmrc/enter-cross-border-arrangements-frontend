#!/bin/bash

echo ""
echo "Applying migration WhatIsTheExpectedValueOfThisArrangement"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whatIsTheExpectedValueOfThisArrangement                        controllers.WhatIsTheExpectedValueOfThisArrangementController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whatIsTheExpectedValueOfThisArrangement                        controllers.WhatIsTheExpectedValueOfThisArrangementController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhatIsTheExpectedValueOfThisArrangement                  controllers.WhatIsTheExpectedValueOfThisArrangementController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhatIsTheExpectedValueOfThisArrangement                  controllers.WhatIsTheExpectedValueOfThisArrangementController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whatIsTheExpectedValueOfThisArrangement.title = whatIsTheExpectedValueOfThisArrangement" >> ../conf/messages.en
echo "whatIsTheExpectedValueOfThisArrangement.heading = whatIsTheExpectedValueOfThisArrangement" >> ../conf/messages.en
echo "whatIsTheExpectedValueOfThisArrangement.Currency = Currency" >> ../conf/messages.en
echo "whatIsTheExpectedValueOfThisArrangement.Amount = Amount" >> ../conf/messages.en
echo "whatIsTheExpectedValueOfThisArrangement.checkYourAnswersLabel = whatIsTheExpectedValueOfThisArrangement" >> ../conf/messages.en
echo "whatIsTheExpectedValueOfThisArrangement.error.Currency.required = Enter Currency" >> ../conf/messages.en
echo "whatIsTheExpectedValueOfThisArrangement.error.Amount.required = Enter Amount" >> ../conf/messages.en
echo "whatIsTheExpectedValueOfThisArrangement.error.Currency.length = Currency must be 100 characters or less" >> ../conf/messages.en
echo "whatIsTheExpectedValueOfThisArrangement.error.Amount.length = Amount must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhatIsTheExpectedValueOfThisArrangementUserAnswersEntry: Arbitrary[(WhatIsTheExpectedValueOfThisArrangementPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[WhatIsTheExpectedValueOfThisArrangementPage.type]";\
    print "        value <- arbitrary[WhatIsTheExpectedValueOfThisArrangement].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhatIsTheExpectedValueOfThisArrangementPage: Arbitrary[WhatIsTheExpectedValueOfThisArrangementPage.type] =";\
    print "    Arbitrary(WhatIsTheExpectedValueOfThisArrangementPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhatIsTheExpectedValueOfThisArrangement: Arbitrary[WhatIsTheExpectedValueOfThisArrangement] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        Currency <- arbitrary[String]";\
    print "        Amount <- arbitrary[String]";\
    print "      } yield WhatIsTheExpectedValueOfThisArrangement(Currency, Amount)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(WhatIsTheExpectedValueOfThisArrangementPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def whatIsTheExpectedValueOfThisArrangement: Option[Row] = userAnswers.get(WhatIsTheExpectedValueOfThisArrangementPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"whatIsTheExpectedValueOfThisArrangement.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(lit\"${answer.Currency} ${answer.Amount}\"),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.WhatIsTheExpectedValueOfThisArrangementController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"whatIsTheExpectedValueOfThisArrangement.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration WhatIsTheExpectedValueOfThisArrangement completed"
