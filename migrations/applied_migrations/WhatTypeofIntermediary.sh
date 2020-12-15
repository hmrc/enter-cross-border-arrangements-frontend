#!/bin/bash

echo ""
echo "Applying migration WhatTypeofIntermediary"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whatTypeofIntermediary                        controllers.intermediaries.WhatTypeofIntermediaryController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whatTypeofIntermediary                        controllers.intermediaries.WhatTypeofIntermediaryController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhatTypeofIntermediary                  controllers.intermediaries.WhatTypeofIntermediaryController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhatTypeofIntermediary                  controllers.intermediaries.WhatTypeofIntermediaryController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whatTypeofIntermediary.title = What type of intermediary is Carlson and Leonard Ltd?" >> ../conf/messages.en
echo "whatTypeofIntermediary.heading = What type of intermediary is Carlson and Leonard Ltd?" >> ../conf/messages.en
echo "whatTypeofIntermediary.promoter = Promoter" >> ../conf/messages.en
echo "whatTypeofIntermediary.serviceProvider = Service Provider" >> ../conf/messages.en
echo "whatTypeofIntermediary.checkYourAnswersLabel = What type of intermediary is Carlson and Leonard Ltd?" >> ../conf/messages.en
echo "whatTypeofIntermediary.error.required = Select whatTypeofIntermediary" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "import pages.intermediaries.WhatTypeofIntermediaryPage";\
    print "import models.intermediaries.WhatTypeofIntermediary";\
    print "  implicit lazy val arbitraryWhatTypeofIntermediaryUserAnswersEntry: Arbitrary[(WhatTypeofIntermediaryPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[WhatTypeofIntermediaryPage.type]";\
    print "        value <- arbitrary[WhatTypeofIntermediary].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "import pages.intermediaries.WhatTypeofIntermediaryPage";\
    print "  implicit lazy val arbitraryWhatTypeofIntermediaryPage: Arbitrary[WhatTypeofIntermediaryPage.type] =";\
    print "    Arbitrary(WhatTypeofIntermediaryPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "import models.intermediaries.WhatTypeofIntermediary";\
    print "  implicit lazy val arbitraryWhatTypeofIntermediary: Arbitrary[WhatTypeofIntermediary] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(WhatTypeofIntermediary.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/import pages._/ {\
    print;\
    print"";\
    print "import pages.intermediaries.WhatTypeofIntermediaryPage";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

awk '/val generators/ {\
    print;\
    print "    arbitrary[(WhatTypeofIntermediaryPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/extends IndividualRows/ {\
     print;\
     print "";\
     print "import pages.intermediaries.WhatTypeofIntermediaryPage";\
     print "  def whatTypeofIntermediary: Option[Row] = userAnswers.get(WhatTypeofIntermediaryPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"whatTypeofIntermediary.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(msg\"whatTypeofIntermediary.$answer\"),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = controllers.intermediaries.routes.WhatTypeofIntermediaryController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"whatTypeofIntermediary.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration WhatTypeofIntermediary completed"
