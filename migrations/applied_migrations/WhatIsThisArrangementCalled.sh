#!/bin/bash

echo ""
echo "Applying migration WhatIsThisArrangementCalled"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whatIsThisArrangementCalled                        controllers.WhatIsThisArrangementCalledController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whatIsThisArrangementCalled                        controllers.WhatIsThisArrangementCalledController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhatIsThisArrangementCalled                  controllers.WhatIsThisArrangementCalledController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhatIsThisArrangementCalled                  controllers.WhatIsThisArrangementCalledController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whatIsThisArrangementCalled.title = whatIsThisArrangementCalled" >> ../conf/messages.en
echo "whatIsThisArrangementCalled.heading = whatIsThisArrangementCalled" >> ../conf/messages.en
echo "whatIsThisArrangementCalled.checkYourAnswersLabel = whatIsThisArrangementCalled" >> ../conf/messages.en
echo "whatIsThisArrangementCalled.error.required = Enter whatIsThisArrangementCalled" >> ../conf/messages.en
echo "whatIsThisArrangementCalled.error.length = WhatIsThisArrangementCalled must be 400 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhatIsThisArrangementCalledUserAnswersEntry: Arbitrary[(WhatIsThisArrangementCalledPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[WhatIsThisArrangementCalledPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhatIsThisArrangementCalledPage: Arbitrary[WhatIsThisArrangementCalledPage.type] =";\
    print "    Arbitrary(WhatIsThisArrangementCalledPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(WhatIsThisArrangementCalledPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def whatIsThisArrangementCalled: Option[Row] = userAnswers.get(WhatIsThisArrangementCalledPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"whatIsThisArrangementCalled.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(lit\"$answer\"),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.WhatIsThisArrangementCalledController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"whatIsThisArrangementCalled.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration WhatIsThisArrangementCalled completed"
