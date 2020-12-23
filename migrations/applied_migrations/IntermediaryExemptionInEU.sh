#!/bin/bash

echo ""
echo "Applying migration IntermediaryExemptionInEU"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /intermediaryExemptionInEU                        controllers.IntermediaryExemptionInEUController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /intermediaryExemptionInEU                        controllers.IntermediaryExemptionInEUController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeIntermediaryExemptionInEU                  controllers.IntermediaryExemptionInEUController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeIntermediaryExemptionInEU                  controllers.IntermediaryExemptionInEUController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "intermediaryExemptionInEU.title = Are you exempt from reporting in any of the EU member states?" >> ../conf/messages.en
echo "intermediaryExemptionInEU.heading = Are you exempt from reporting in any of the EU member states?" >> ../conf/messages.en
echo "intermediaryExemptionInEU.yes = Yes" >> ../conf/messages.en
echo "intermediaryExemptionInEU.no = No" >> ../conf/messages.en
echo "intermediaryExemptionInEU.checkYourAnswersLabel = Are you exempt from reporting in any of the EU member states?" >> ../conf/messages.en
echo "intermediaryExemptionInEU.error.required = Select intermediaryExemptionInEU" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIntermediaryExemptionInEUUserAnswersEntry: Arbitrary[(IntermediaryExemptionInEUPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[IntermediaryExemptionInEUPage.type]";\
    print "        value <- arbitrary[IntermediaryExemptionInEU].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIntermediaryExemptionInEUPage: Arbitrary[IntermediaryExemptionInEUPage.type] =";\
    print "    Arbitrary(IntermediaryExemptionInEUPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIntermediaryExemptionInEU: Arbitrary[IntermediaryExemptionInEU] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(IntermediaryExemptionInEU.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(IntermediaryExemptionInEUPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def intermediaryExemptionInEU: Option[Row] = userAnswers.get(IntermediaryExemptionInEUPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"intermediaryExemptionInEU.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(msg\"intermediaryExemptionInEU.$answer\"),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.IntermediaryExemptionInEUController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"intermediaryExemptionInEU.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration IntermediaryExemptionInEU completed"
