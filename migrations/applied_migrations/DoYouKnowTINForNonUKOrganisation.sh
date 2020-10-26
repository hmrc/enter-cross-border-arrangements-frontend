#!/bin/bash

echo ""
echo "Applying migration DoYouKnowTINForNonUKOrganisation"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /doYouKnowTINForNonUKOrganisation                        controllers.DoYouKnowTINForNonUKOrganisationController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /doYouKnowTINForNonUKOrganisation                        controllers.DoYouKnowTINForNonUKOrganisationController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeDoYouKnowTINForNonUKOrganisation                  controllers.DoYouKnowTINForNonUKOrganisationController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeDoYouKnowTINForNonUKOrganisation                  controllers.DoYouKnowTINForNonUKOrganisationController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "doYouKnowTINForNonUKOrganisation.title = doYouKnowTINForNonUKOrganisation" >> ../conf/messages.en
echo "doYouKnowTINForNonUKOrganisation.heading = doYouKnowTINForNonUKOrganisation" >> ../conf/messages.en
echo "doYouKnowTINForNonUKOrganisation.checkYourAnswersLabel = doYouKnowTINForNonUKOrganisation" >> ../conf/messages.en
echo "doYouKnowTINForNonUKOrganisation.error.required = Select yes if doYouKnowTINForNonUKOrganisation" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryDoYouKnowTINForNonUKOrganisationUserAnswersEntry: Arbitrary[(DoYouKnowTINForNonUKOrganisationPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[DoYouKnowTINForNonUKOrganisationPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryDoYouKnowTINForNonUKOrganisationPage: Arbitrary[DoYouKnowTINForNonUKOrganisationPage.type] =";\
    print "    Arbitrary(DoYouKnowTINForNonUKOrganisationPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(DoYouKnowTINForNonUKOrganisationPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def doYouKnowTINForNonUKOrganisation: Option[Row] = userAnswers.get(DoYouKnowTINForNonUKOrganisationPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"doYouKnowTINForNonUKOrganisation.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(yesOrNo(answer)),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.DoYouKnowTINForNonUKOrganisationController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"doYouKnowTINForNonUKOrganisation.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration DoYouKnowTINForNonUKOrganisation completed"
