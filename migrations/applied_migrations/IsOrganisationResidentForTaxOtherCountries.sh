#!/bin/bash

echo ""
echo "Applying migration IsOrganisationResidentForTaxOtherCountries"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /isOrganisationResidentForTaxOtherCountries                        controllers.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /isOrganisationResidentForTaxOtherCountries                        controllers.IsOrganisationResidentForTaxOtherCountriesController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeIsOrganisationResidentForTaxOtherCountries                  controllers.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeIsOrganisationResidentForTaxOtherCountries                  controllers.IsOrganisationResidentForTaxOtherCountriesController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "isOrganisationResidentForTaxOtherCountries.title = isOrganisationResidentForTaxOtherCountries" >> ../conf/messages.en
echo "isOrganisationResidentForTaxOtherCountries.heading = isOrganisationResidentForTaxOtherCountries" >> ../conf/messages.en
echo "isOrganisationResidentForTaxOtherCountries.checkYourAnswersLabel = isOrganisationResidentForTaxOtherCountries" >> ../conf/messages.en
echo "isOrganisationResidentForTaxOtherCountries.error.required = Select yes if isOrganisationResidentForTaxOtherCountries" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIsOrganisationResidentForTaxOtherCountriesUserAnswersEntry: Arbitrary[(IsOrganisationResidentForTaxOtherCountriesPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[IsOrganisationResidentForTaxOtherCountriesPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIsOrganisationResidentForTaxOtherCountriesPage: Arbitrary[IsOrganisationResidentForTaxOtherCountriesPage.type] =";\
    print "    Arbitrary(IsOrganisationResidentForTaxOtherCountriesPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(IsOrganisationResidentForTaxOtherCountriesPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def isOrganisationResidentForTaxOtherCountries: Option[Row] = userAnswers.get(IsOrganisationResidentForTaxOtherCountriesPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"isOrganisationResidentForTaxOtherCountries.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(yesOrNo(answer)),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.IsOrganisationResidentForTaxOtherCountriesController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"isOrganisationResidentForTaxOtherCountries.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration IsOrganisationResidentForTaxOtherCountries completed"
