#!/bin/bash

echo ""
echo "Applying migration DisclosureType"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /disclosureType                        controllers.DisclosureTypeController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /disclosureType                        controllers.DisclosureTypeController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeDisclosureType                  controllers.DisclosureTypeController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeDisclosureType                  controllers.DisclosureTypeController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "disclosureType.title = What type of disclosure would you like to make?" >> ../conf/messages.en
echo "disclosureType.heading = What type of disclosure would you like to make?" >> ../conf/messages.en
echo "disclosureType.dac6new = A new arrangement" >> ../conf/messages.en
echo "disclosureType.dac6add = An addition to an existing arrangement" >> ../conf/messages.en
echo "disclosureType.checkYourAnswersLabel = What type of disclosure would you like to make?" >> ../conf/messages.en
echo "disclosureType.error.required = Select disclosureType" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryDisclosureTypeUserAnswersEntry: Arbitrary[(DisclosureTypePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[DisclosureTypePage.type]";\
    print "        value <- arbitrary[DisclosureType].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryDisclosureTypePage: Arbitrary[DisclosureTypePage.type] =";\
    print "    Arbitrary(DisclosureTypePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryDisclosureType: Arbitrary[DisclosureType] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(DisclosureType.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(DisclosureTypePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def disclosureType: Option[Row] = userAnswers.get(DisclosureTypePage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"disclosureType.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(msg\"disclosureType.$answer\"),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.DisclosureTypeController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"disclosureType.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration DisclosureType completed"
