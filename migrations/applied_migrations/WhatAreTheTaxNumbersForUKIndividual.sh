#!/bin/bash

echo ""
echo "Applying migration WhatAreTheTaxNumbersForUKIndividual"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whatAreTheTaxNumbersForUKIndividual                        controllers.WhatAreTheTaxNumbersForUKIndividualController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whatAreTheTaxNumbersForUKIndividual                        controllers.WhatAreTheTaxNumbersForUKIndividualController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhatAreTheTaxNumbersForUKIndividual                  controllers.WhatAreTheTaxNumbersForUKIndividualController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhatAreTheTaxNumbersForUKIndividual                  controllers.WhatAreTheTaxNumbersForUKIndividualController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whatAreTheTaxNumbersForUKIndividual.title = whatAreTheTaxNumbersForUKIndividual" >> ../conf/messages.en
echo "whatAreTheTaxNumbersForUKIndividual.heading = whatAreTheTaxNumbersForUKIndividual" >> ../conf/messages.en
echo "whatAreTheTaxNumbersForUKIndividual.checkYourAnswersLabel = whatAreTheTaxNumbersForUKIndividual" >> ../conf/messages.en
echo "whatAreTheTaxNumbersForUKIndividual.error.required = Enter whatAreTheTaxNumbersForUKIndividual" >> ../conf/messages.en
echo "whatAreTheTaxNumbersForUKIndividual.error.length = WhatAreTheTaxNumbersForUKIndividual must be 35 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhatAreTheTaxNumbersForUKIndividualUserAnswersEntry: Arbitrary[(WhatAreTheTaxNumbersForUKIndividualPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[WhatAreTheTaxNumbersForUKIndividualPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhatAreTheTaxNumbersForUKIndividualPage: Arbitrary[WhatAreTheTaxNumbersForUKIndividualPage.type] =";\
    print "    Arbitrary(WhatAreTheTaxNumbersForUKIndividualPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(WhatAreTheTaxNumbersForUKIndividualPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def whatAreTheTaxNumbersForUKIndividual: Option[Row] = userAnswers.get(WhatAreTheTaxNumbersForUKIndividualPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"whatAreTheTaxNumbersForUKIndividual.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(lit\"$answer\"),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.WhatAreTheTaxNumbersForUKIndividualController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"whatAreTheTaxNumbersForUKIndividual.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration WhatAreTheTaxNumbersForUKIndividual completed"
