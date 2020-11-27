#!/bin/bash

echo ""
echo "Applying migration WhatIsTheImplementationDate"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whatIsTheImplementationDate                  controllers.WhatIsTheImplementationDateController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whatIsTheImplementationDate                  controllers.WhatIsTheImplementationDateController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhatIsTheImplementationDate                        controllers.WhatIsTheImplementationDateController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhatIsTheImplementationDate                        controllers.WhatIsTheImplementationDateController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whatIsTheImplementationDate.title = WhatIsTheImplementationDate" >> ../conf/messages.en
echo "whatIsTheImplementationDate.heading = WhatIsTheImplementationDate" >> ../conf/messages.en
echo "whatIsTheImplementationDate.hint = For example, 12 11 2007" >> ../conf/messages.en
echo "whatIsTheImplementationDate.checkYourAnswersLabel = WhatIsTheImplementationDate" >> ../conf/messages.en
echo "whatIsTheImplementationDate.error.required.all = Enter the whatIsTheImplementationDate" >> ../conf/messages.en
echo "whatIsTheImplementationDate.error.required.two = The whatIsTheImplementationDate" must include {0} and {1} >> ../conf/messages.en
echo "whatIsTheImplementationDate.error.required = The whatIsTheImplementationDate must include {0}" >> ../conf/messages.en
echo "whatIsTheImplementationDate.error.invalid = Enter a real WhatIsTheImplementationDate" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhatIsTheImplementationDateUserAnswersEntry: Arbitrary[(WhatIsTheImplementationDatePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[WhatIsTheImplementationDatePage.type]";\
    print "        value <- arbitrary[Int].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhatIsTheImplementationDatePage: Arbitrary[WhatIsTheImplementationDatePage.type] =";\
    print "    Arbitrary(WhatIsTheImplementationDatePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(WhatIsTheImplementationDatePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def whatIsTheImplementationDate: Option[Row] = userAnswers.get(WhatIsTheImplementationDatePage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"whatIsTheImplementationDate.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(Literal(answer.format(dateFormatter))),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.WhatIsTheImplementationDateController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"whatIsTheImplementationDate.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration WhatIsTheImplementationDate completed"
