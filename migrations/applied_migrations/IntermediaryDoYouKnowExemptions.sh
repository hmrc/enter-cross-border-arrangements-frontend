#!/bin/bash

echo ""
echo "Applying migration IntermediaryDoYouKnowExemptions"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /intermediaryDoYouKnowExemptions                        controllers.IntermediaryDoYouKnowExemptionsController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /intermediaryDoYouKnowExemptions                        controllers.IntermediaryDoYouKnowExemptionsController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeIntermediaryDoYouKnowExemptions                  controllers.IntermediaryDoYouKnowExemptionsController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeIntermediaryDoYouKnowExemptions                  controllers.IntermediaryDoYouKnowExemptionsController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "intermediaryDoYouKnowExemptions.title = intermediaryDoYouKnowExemptions" >> ../conf/messages.en
echo "intermediaryDoYouKnowExemptions.heading = intermediaryDoYouKnowExemptions" >> ../conf/messages.en
echo "intermediaryDoYouKnowExemptions.checkYourAnswersLabel = intermediaryDoYouKnowExemptions" >> ../conf/messages.en
echo "intermediaryDoYouKnowExemptions.error.required = Select yes if intermediaryDoYouKnowExemptions" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIntermediaryDoYouKnowExemptionsUserAnswersEntry: Arbitrary[(IntermediaryDoYouKnowExemptionsPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[IntermediaryDoYouKnowExemptionsPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIntermediaryDoYouKnowExemptionsPage: Arbitrary[IntermediaryDoYouKnowExemptionsPage.type] =";\
    print "    Arbitrary(IntermediaryDoYouKnowExemptionsPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(IntermediaryDoYouKnowExemptionsPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def intermediaryDoYouKnowExemptions: Option[Row] = userAnswers.get(IntermediaryDoYouKnowExemptionsPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"intermediaryDoYouKnowExemptions.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(yesOrNo(answer)),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.IntermediaryDoYouKnowExemptionsController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"intermediaryDoYouKnowExemptions.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration IntermediaryDoYouKnowExemptions completed"
