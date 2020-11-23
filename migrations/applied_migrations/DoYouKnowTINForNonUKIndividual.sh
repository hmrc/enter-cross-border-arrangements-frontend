#!/bin/bash

echo ""
echo "Applying migration DoYouKnowTINForNonUKIndividual"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /doYouKnowTINForNonUKIndividual                        controllers.DoYouKnowTINForNonUKIndividualController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /doYouKnowTINForNonUKIndividual                        controllers.DoYouKnowTINForNonUKIndividualController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeDoYouKnowTINForNonUKIndividual                  controllers.DoYouKnowTINForNonUKIndividualController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeDoYouKnowTINForNonUKIndividual                  controllers.DoYouKnowTINForNonUKIndividualController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "doYouKnowTINForNonUKIndividual.title = doYouKnowTINForNonUKIndividual" >> ../conf/messages.en
echo "doYouKnowTINForNonUKIndividual.heading = doYouKnowTINForNonUKIndividual" >> ../conf/messages.en
echo "doYouKnowTINForNonUKIndividual.checkYourAnswersLabel = doYouKnowTINForNonUKIndividual" >> ../conf/messages.en
echo "doYouKnowTINForNonUKIndividual.error.required = Select yes if doYouKnowTINForNonUKIndividual" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryDoYouKnowTINForNonUKIndividualUserAnswersEntry: Arbitrary[(DoYouKnowTINForNonUKIndividualPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[DoYouKnowTINForNonUKIndividualPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryDoYouKnowTINForNonUKIndividualPage: Arbitrary[DoYouKnowTINForNonUKIndividualPage.type] =";\
    print "    Arbitrary(DoYouKnowTINForNonUKIndividualPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(DoYouKnowTINForNonUKIndividualPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def doYouKnowTINForNonUKIndividual: Option[Row] = userAnswers.get(DoYouKnowTINForNonUKIndividualPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"doYouKnowTINForNonUKIndividual.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(yesOrNo(answer)),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.DoYouKnowTINForNonUKIndividualController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"doYouKnowTINForNonUKIndividual.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration DoYouKnowTINForNonUKIndividual completed"
