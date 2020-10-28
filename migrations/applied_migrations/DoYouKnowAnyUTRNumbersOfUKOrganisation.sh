#!/bin/bash

echo ""
echo "Applying migration DoYouKnowAnyUTRNumbersOfUKOrganisation"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /doYouKnowAnyUTRNumbersOfUKOrganisation                        controllers.DoYouKnowAnyUTRNumbersOfUKOrganisationController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /doYouKnowAnyUTRNumbersOfUKOrganisation                        controllers.DoYouKnowAnyUTRNumbersOfUKOrganisationController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeDoYouKnowAnyUTRNumbersOfUKOrganisation                  controllers.DoYouKnowAnyUTRNumbersOfUKOrganisationController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeDoYouKnowAnyUTRNumbersOfUKOrganisation                  controllers.DoYouKnowAnyUTRNumbersOfUKOrganisationController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "doYouKnowAnyUTRNumbersOfUKOrganisation.title = doYouKnowAnyUTRNumbersOfUKOrganisation" >> ../conf/messages.en
echo "doYouKnowAnyUTRNumbersOfUKOrganisation.heading = doYouKnowAnyUTRNumbersOfUKOrganisation" >> ../conf/messages.en
echo "doYouKnowAnyUTRNumbersOfUKOrganisation.checkYourAnswersLabel = doYouKnowAnyUTRNumbersOfUKOrganisation" >> ../conf/messages.en
echo "doYouKnowAnyUTRNumbersOfUKOrganisation.error.required = Select yes if doYouKnowAnyUTRNumbersOfUKOrganisation" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryDoYouKnowAnyUTRNumbersOfUKOrganisationUserAnswersEntry: Arbitrary[(DoYouKnowAnyUTRNumbersOfUKOrganisationPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[DoYouKnowAnyUTRNumbersOfUKOrganisationPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryDoYouKnowAnyUTRNumbersOfUKOrganisationPage: Arbitrary[DoYouKnowAnyUTRNumbersOfUKOrganisationPage.type] =";\
    print "    Arbitrary(DoYouKnowAnyUTRNumbersOfUKOrganisationPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(DoYouKnowAnyUTRNumbersOfUKOrganisationPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def doYouKnowAnyUTRNumbersOfUKOrganisation: Option[Row] = userAnswers.get(DoYouKnowAnyUTRNumbersOfUKOrganisationPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"doYouKnowAnyUTRNumbersOfUKOrganisation.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(yesOrNo(answer)),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.DoYouKnowAnyUTRNumbersOfUKOrganisationController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"doYouKnowAnyUTRNumbersOfUKOrganisation.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration DoYouKnowAnyUTRNumbersOfUKOrganisation completed"
