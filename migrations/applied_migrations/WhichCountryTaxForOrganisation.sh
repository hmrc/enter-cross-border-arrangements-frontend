#!/bin/bash

echo ""
echo "Applying migration WhichCountryTaxForOrganisation"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whichCountryTaxForOrganisation                        controllers.WhichCountryTaxForOrganisationController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whichCountryTaxForOrganisation                        controllers.WhichCountryTaxForOrganisationController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhichCountryTaxForOrganisation                  controllers.WhichCountryTaxForOrganisationController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhichCountryTaxForOrganisation                  controllers.WhichCountryTaxForOrganisationController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whichCountryTaxForOrganisation.title = whichCountryTaxForOrganisation" >> ../conf/messages.en
echo "whichCountryTaxForOrganisation.heading = whichCountryTaxForOrganisation" >> ../conf/messages.en
echo "whichCountryTaxForOrganisation.checkYourAnswersLabel = whichCountryTaxForOrganisation" >> ../conf/messages.en
echo "whichCountryTaxForOrganisation.error.required = Enter whichCountryTaxForOrganisation" >> ../conf/messages.en
echo "whichCountryTaxForOrganisation.error.length = WhichCountryTaxForOrganisation must be 20 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhichCountryTaxForOrganisationUserAnswersEntry: Arbitrary[(WhichCountryTaxForOrganisationPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[WhichCountryTaxForOrganisationPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhichCountryTaxForOrganisationPage: Arbitrary[WhichCountryTaxForOrganisationPage.type] =";\
    print "    Arbitrary(WhichCountryTaxForOrganisationPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(WhichCountryTaxForOrganisationPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def whichCountryTaxForOrganisation: Option[Row] = userAnswers.get(WhichCountryTaxForOrganisationPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"whichCountryTaxForOrganisation.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(lit\"$answer\"),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.WhichCountryTaxForOrganisationController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"whichCountryTaxForOrganisation.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration WhichCountryTaxForOrganisation completed"
