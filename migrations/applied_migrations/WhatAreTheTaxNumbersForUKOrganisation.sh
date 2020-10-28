#!/bin/bash

echo ""
echo "Applying migration WhatAreTheTaxNumbersForUKOrganisation"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whatAreTheTaxNumbersForUKOrganisation                        controllers.WhatAreTheTaxNumbersForUKOrganisationController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whatAreTheTaxNumbersForUKOrganisation                        controllers.WhatAreTheTaxNumbersForUKOrganisationController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhatAreTheTaxNumbersForUKOrganisation                  controllers.WhatAreTheTaxNumbersForUKOrganisationController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhatAreTheTaxNumbersForUKOrganisation                  controllers.WhatAreTheTaxNumbersForUKOrganisationController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whatAreTheTaxNumbersForUKOrganisation.title = whatAreTheTaxNumbersForUKOrganisation" >> ../conf/messages.en
echo "whatAreTheTaxNumbersForUKOrganisation.heading = whatAreTheTaxNumbersForUKOrganisation" >> ../conf/messages.en
echo "whatAreTheTaxNumbersForUKOrganisation.checkYourAnswersLabel = whatAreTheTaxNumbersForUKOrganisation" >> ../conf/messages.en
echo "whatAreTheTaxNumbersForUKOrganisation.error.required = Enter whatAreTheTaxNumbersForUKOrganisation" >> ../conf/messages.en
echo "whatAreTheTaxNumbersForUKOrganisation.error.length = WhatAreTheTaxNumbersForUKOrganisation must be 10 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhatAreTheTaxNumbersForUKOrganisationUserAnswersEntry: Arbitrary[(WhatAreTheTaxNumbersForUKOrganisationPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[WhatAreTheTaxNumbersForUKOrganisationPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhatAreTheTaxNumbersForUKOrganisationPage: Arbitrary[WhatAreTheTaxNumbersForUKOrganisationPage.type] =";\
    print "    Arbitrary(WhatAreTheTaxNumbersForUKOrganisationPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(WhatAreTheTaxNumbersForUKOrganisationPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def whatAreTheTaxNumbersForUKOrganisation: Option[Row] = userAnswers.get(WhatAreTheTaxNumbersForUKOrganisationPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"whatAreTheTaxNumbersForUKOrganisation.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(lit\"$answer\"),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.WhatAreTheTaxNumbersForUKOrganisationController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"whatAreTheTaxNumbersForUKOrganisation.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration WhatAreTheTaxNumbersForUKOrganisation completed"
