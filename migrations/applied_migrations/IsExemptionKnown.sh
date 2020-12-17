#!/bin/bash

echo ""
echo "Applying migration IsExemptionKnown"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /isExemptionKnown                        controllers.IsExemptionKnownController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /isExemptionKnown                        controllers.IsExemptionKnownController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeIsExemptionKnown                  controllers.IsExemptionKnownController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeIsExemptionKnown                  controllers.IsExemptionKnownController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "isExemptionKnown.title = isExemptionKnown" >> ../conf/messages.en
echo "isExemptionKnown.heading = isExemptionKnown" >> ../conf/messages.en
echo "isExemptionKnown.yes = Yes" >> ../conf/messages.en
echo "isExemptionKnown.no = No" >> ../conf/messages.en
echo "isExemptionKnown.checkYourAnswersLabel = isExemptionKnown" >> ../conf/messages.en
echo "isExemptionKnown.error.required = Select isExemptionKnown" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIsExemptionKnownUserAnswersEntry: Arbitrary[(IsExemptionKnownPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[IsExemptionKnownPage.type]";\
    print "        value <- arbitrary[IsExemptionKnown].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIsExemptionKnownPage: Arbitrary[IsExemptionKnownPage.type] =";\
    print "    Arbitrary(IsExemptionKnownPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIsExemptionKnown: Arbitrary[IsExemptionKnown] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(IsExemptionKnown.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(IsExemptionKnownPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def isExemptionKnown: Option[Row] = userAnswers.get(IsExemptionKnownPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"isExemptionKnown.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(msg\"isExemptionKnown.$answer\"),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.IsExemptionKnownController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"isExemptionKnown.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration IsExemptionKnown completed"
