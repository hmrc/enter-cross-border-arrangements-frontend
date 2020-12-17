#!/bin/bash

echo ""
echo "Applying migration WhatIsTaxpayersStartDateForImplementingArrangement"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whatIsTaxpayersStartDateForImplementingArrangement                  controllers.taxpayer.WhatIsTaxpayersStartDateForImplementingArrangementController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whatIsTaxpayersStartDateForImplementingArrangement                  controllers.taxpayer.WhatIsTaxpayersStartDateForImplementingArrangementController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhatIsTaxpayersStartDateForImplementingArrangement                        controllers.taxpayer.WhatIsTaxpayersStartDateForImplementingArrangementController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhatIsTaxpayersStartDateForImplementingArrangement                        controllers.taxpayer.WhatIsTaxpayersStartDateForImplementingArrangementController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whatIsTaxpayersStartDateForImplementingArrangement.title = WhatIsTaxpayersStartDateForImplementingArrangement" >> ../conf/messages.en
echo "whatIsTaxpayersStartDateForImplementingArrangement.heading = WhatIsTaxpayersStartDateForImplementingArrangement" >> ../conf/messages.en
echo "whatIsTaxpayersStartDateForImplementingArrangement.hint = For example, 12 11 2007" >> ../conf/messages.en
echo "whatIsTaxpayersStartDateForImplementingArrangement.checkYourAnswersLabel = WhatIsTaxpayersStartDateForImplementingArrangement" >> ../conf/messages.en
echo "whatIsTaxpayersStartDateForImplementingArrangement.error.required.all = Enter the whatIsTaxpayersStartDateForImplementingArrangement" >> ../conf/messages.en
echo "whatIsTaxpayersStartDateForImplementingArrangement.error.required.two = The whatIsTaxpayersStartDateForImplementingArrangement" must include {0} and {1} >> ../conf/messages.en
echo "whatIsTaxpayersStartDateForImplementingArrangement.error.required = The whatIsTaxpayersStartDateForImplementingArrangement must include {0}" >> ../conf/messages.en
echo "whatIsTaxpayersStartDateForImplementingArrangement.error.invalid = Enter a real WhatIsTaxpayersStartDateForImplementingArrangement" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "import pages.taxpayer._";\
    print "  implicit lazy val arbitraryWhatIsTaxpayersStartDateForImplementingArrangementUserAnswersEntry: Arbitrary[(WhatIsTaxpayersStartDateForImplementingArrangementPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[WhatIsTaxpayersStartDateForImplementingArrangementPage.type]";\
    print "        value <- arbitrary[Int].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "import pages.taxpayer._";\
    print "  implicit lazy val arbitraryWhatIsTaxpayersStartDateForImplementingArrangementPage: Arbitrary[WhatIsTaxpayersStartDateForImplementingArrangementPage.type] =";\
    print "    Arbitrary(WhatIsTaxpayersStartDateForImplementingArrangementPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"

awk '/import pages._/ {\
    print;\
    print"";\
    print "import pages.taxpayer._";\
   next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

awk '/val generators/ {\
    print;\
    print "    arbitrary[(WhatIsTaxpayersStartDateForImplementingArrangementPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/extends IndividualRows/ {\
     print;\
     print "";\
     print "import pages.taxpayer.WhatIsTaxpayersStartDateForImplementingArrangementPage";\
     print "  def whatIsTaxpayersStartDateForImplementingArrangement: Option[Row] = userAnswers.get(WhatIsTaxpayersStartDateForImplementingArrangementPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"whatIsTaxpayersStartDateForImplementingArrangement.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(Literal(answer.format(dateFormatter))),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = controllers.taxpayer.routes.WhatIsTaxpayersStartDateForImplementingArrangementController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"whatIsTaxpayersStartDateForImplementingArrangement.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration WhatIsTaxpayersStartDateForImplementingArrangement completed"
