#!/bin/bash

echo ""
echo "Applying migration WhichExpectedInvolvedCountriesArrangement"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whichExpectedInvolvedCountriesArrangement                        controllers.WhichExpectedInvolvedCountriesArrangementController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whichExpectedInvolvedCountriesArrangement                        controllers.WhichExpectedInvolvedCountriesArrangementController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhichExpectedInvolvedCountriesArrangement                  controllers.WhichExpectedInvolvedCountriesArrangementController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhichExpectedInvolvedCountriesArrangement                  controllers.WhichExpectedInvolvedCountriesArrangementController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whichExpectedInvolvedCountriesArrangement.title = Which of these countries are expected to be involved in this arrangement?" >> ../conf/messages.en
echo "whichExpectedInvolvedCountriesArrangement.heading = Which of these countries are expected to be involved in this arrangement?" >> ../conf/messages.en
echo "whichExpectedInvolvedCountriesArrangement.unitedKingdom = UnitedKingdom" >> ../conf/messages.en
echo "whichExpectedInvolvedCountriesArrangement.austria = Austria" >> ../conf/messages.en
echo "whichExpectedInvolvedCountriesArrangement.checkYourAnswersLabel = Which of these countries are expected to be involved in this arrangement?" >> ../conf/messages.en
echo "whichExpectedInvolvedCountriesArrangement.error.required = Select whichExpectedInvolvedCountriesArrangement" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhichExpectedInvolvedCountriesArrangementUserAnswersEntry: Arbitrary[(WhichExpectedInvolvedCountriesArrangementPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[WhichExpectedInvolvedCountriesArrangementPage.type]";\
    print "        value <- arbitrary[WhichExpectedInvolvedCountriesArrangement].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhichExpectedInvolvedCountriesArrangementPage: Arbitrary[WhichExpectedInvolvedCountriesArrangementPage.type] =";\
    print "    Arbitrary(WhichExpectedInvolvedCountriesArrangementPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhichExpectedInvolvedCountriesArrangement: Arbitrary[WhichExpectedInvolvedCountriesArrangement] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(WhichExpectedInvolvedCountriesArrangement.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(WhichExpectedInvolvedCountriesArrangementPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def whichExpectedInvolvedCountriesArrangement: Option[Row] = userAnswers.get(WhichExpectedInvolvedCountriesArrangementPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"whichExpectedInvolvedCountriesArrangement.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(Html(answer.map(a => msg\"whichExpectedInvolvedCountriesArrangement.$a\".resolve).mkString(\",<br>\"))),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.WhichExpectedInvolvedCountriesArrangementController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"whichExpectedInvolvedCountriesArrangement.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration WhichExpectedInvolvedCountriesArrangement completed"
