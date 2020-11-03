#!/bin/bash

echo ""
echo "Applying migration IsIndividualPlaceOfBirthKnown"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /isIndividualPlaceOfBirthKnown                        controllers.IsIndividualPlaceOfBirthKnownController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /isIndividualPlaceOfBirthKnown                        controllers.IsIndividualPlaceOfBirthKnownController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeIsIndividualPlaceOfBirthKnown                  controllers.IsIndividualPlaceOfBirthKnownController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeIsIndividualPlaceOfBirthKnown                  controllers.IsIndividualPlaceOfBirthKnownController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "isIndividualPlaceOfBirthKnown.title = isIndividualPlaceOfBirthKnown" >> ../conf/messages.en
echo "isIndividualPlaceOfBirthKnown.heading = isIndividualPlaceOfBirthKnown" >> ../conf/messages.en
echo "isIndividualPlaceOfBirthKnown.checkYourAnswersLabel = isIndividualPlaceOfBirthKnown" >> ../conf/messages.en
echo "isIndividualPlaceOfBirthKnown.error.required = Select yes if isIndividualPlaceOfBirthKnown" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIsIndividualPlaceOfBirthKnownUserAnswersEntry: Arbitrary[(IsIndividualPlaceOfBirthKnownPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[IsIndividualPlaceOfBirthKnownPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIsIndividualPlaceOfBirthKnownPage: Arbitrary[IsIndividualPlaceOfBirthKnownPage.type] =";\
    print "    Arbitrary(IsIndividualPlaceOfBirthKnownPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(IsIndividualPlaceOfBirthKnownPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def isIndividualPlaceOfBirthKnown: Option[Row] = userAnswers.get(IsIndividualPlaceOfBirthKnownPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"isIndividualPlaceOfBirthKnown.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(yesOrNo(answer)),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.IsIndividualPlaceOfBirthKnownController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"isIndividualPlaceOfBirthKnown.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration IsIndividualPlaceOfBirthKnown completed"
