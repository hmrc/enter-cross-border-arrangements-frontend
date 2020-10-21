#!/bin/bash

echo ""
echo "Applying migration IsIndividualAddressKnown"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /isIndividualAddressKnown                        controllers.IsIndividualAddressKnownController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /isIndividualAddressKnown                        controllers.IsIndividualAddressKnownController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeIsIndividualAddressKnown                  controllers.IsIndividualAddressKnownController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeIsIndividualAddressKnown                  controllers.IsIndividualAddressKnownController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "isIndividualAddressKnown.title = isIndividualAddressKnown" >> ../conf/messages.en
echo "isIndividualAddressKnown.heading = isIndividualAddressKnown" >> ../conf/messages.en
echo "isIndividualAddressKnown.checkYourAnswersLabel = isIndividualAddressKnown" >> ../conf/messages.en
echo "isIndividualAddressKnown.error.required = Select yes if isIndividualAddressKnown" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIsIndividualAddressKnownUserAnswersEntry: Arbitrary[(IsIndividualAddressKnownPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[IsIndividualAddressKnownPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIsIndividualAddressKnownPage: Arbitrary[IsIndividualAddressKnownPage.type] =";\
    print "    Arbitrary(IsIndividualAddressKnownPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(IsIndividualAddressKnownPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def isIndividualAddressKnown: Option[Row] = userAnswers.get(IsIndividualAddressKnownPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"isIndividualAddressKnown.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(yesOrNo(answer)),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.IsIndividualAddressKnownController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"isIndividualAddressKnown.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration IsIndividualAddressKnown completed"
