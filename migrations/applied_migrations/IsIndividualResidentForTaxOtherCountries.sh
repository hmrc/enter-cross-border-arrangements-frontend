#!/bin/bash

echo ""
echo "Applying migration IsIndividualResidentForTaxOtherCountries"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /isIndividualResidentForTaxOtherCountries                        controllers.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /isIndividualResidentForTaxOtherCountries                        controllers.IsIndividualResidentForTaxOtherCountriesController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeIsIndividualResidentForTaxOtherCountries                  controllers.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeIsIndividualResidentForTaxOtherCountries                  controllers.IsIndividualResidentForTaxOtherCountriesController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "isIndividualResidentForTaxOtherCountries.title = isIndividualResidentForTaxOtherCountries" >> ../conf/messages.en
echo "isIndividualResidentForTaxOtherCountries.heading = isIndividualResidentForTaxOtherCountries" >> ../conf/messages.en
echo "isIndividualResidentForTaxOtherCountries.checkYourAnswersLabel = isIndividualResidentForTaxOtherCountries" >> ../conf/messages.en
echo "isIndividualResidentForTaxOtherCountries.error.required = Select yes if isIndividualResidentForTaxOtherCountries" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIsIndividualResidentForTaxOtherCountriesUserAnswersEntry: Arbitrary[(IsIndividualResidentForTaxOtherCountriesPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[IsIndividualResidentForTaxOtherCountriesPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIsIndividualResidentForTaxOtherCountriesPage: Arbitrary[IsIndividualResidentForTaxOtherCountriesPage.type] =";\
    print "    Arbitrary(IsIndividualResidentForTaxOtherCountriesPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(IsIndividualResidentForTaxOtherCountriesPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def isIndividualResidentForTaxOtherCountries: Option[Row] = userAnswers.get(IsIndividualResidentForTaxOtherCountriesPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"isIndividualResidentForTaxOtherCountries.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(yesOrNo(answer)),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.IsIndividualResidentForTaxOtherCountriesController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"isIndividualResidentForTaxOtherCountries.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration IsIndividualResidentForTaxOtherCountries completed"
