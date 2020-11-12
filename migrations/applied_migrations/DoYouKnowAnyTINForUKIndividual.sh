#!/bin/bash

echo ""
echo "Applying migration DoYouKnowAnyTINForUKIndividual"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /doYouKnowAnyTINForUKIndividual                        controllers.DoYouKnowAnyTINForUKIndividualController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /doYouKnowAnyTINForUKIndividual                        controllers.DoYouKnowAnyTINForUKIndividualController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeDoYouKnowAnyTINForUKIndividual                  controllers.DoYouKnowAnyTINForUKIndividualController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeDoYouKnowAnyTINForUKIndividual                  controllers.DoYouKnowAnyTINForUKIndividualController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "doYouKnowAnyTINForUKIndividual.title = doYouKnowAnyTINForUKIndividual" >> ../conf/messages.en
echo "doYouKnowAnyTINForUKIndividual.heading = doYouKnowAnyTINForUKIndividual" >> ../conf/messages.en
echo "doYouKnowAnyTINForUKIndividual.checkYourAnswersLabel = doYouKnowAnyTINForUKIndividual" >> ../conf/messages.en
echo "doYouKnowAnyTINForUKIndividual.error.required = Select yes if doYouKnowAnyTINForUKIndividual" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryDoYouKnowAnyTINForUKIndividualUserAnswersEntry: Arbitrary[(DoYouKnowAnyTINForUKIndividualPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[DoYouKnowAnyTINForUKIndividualPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryDoYouKnowAnyTINForUKIndividualPage: Arbitrary[DoYouKnowAnyTINForUKIndividualPage.type] =";\
    print "    Arbitrary(DoYouKnowAnyTINForUKIndividualPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(DoYouKnowAnyTINForUKIndividualPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def doYouKnowAnyTINForUKIndividual: Option[Row] = userAnswers.get(DoYouKnowAnyTINForUKIndividualPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"doYouKnowAnyTINForUKIndividual.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(yesOrNo(answer)),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.DoYouKnowAnyTINForUKIndividualController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"doYouKnowAnyTINForUKIndividual.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration DoYouKnowAnyTINForUKIndividual completed"
