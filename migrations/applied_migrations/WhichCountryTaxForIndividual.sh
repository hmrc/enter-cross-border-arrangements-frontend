#!/bin/bash

echo ""
echo "Applying migration WhichCountryTaxForIndividual"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whichCountryTaxForIndividual                        controllers.WhichCountryTaxForIndividualController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whichCountryTaxForIndividual                        controllers.WhichCountryTaxForIndividualController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhichCountryTaxForIndividual                  controllers.WhichCountryTaxForIndividualController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhichCountryTaxForIndividual                  controllers.WhichCountryTaxForIndividualController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whichCountryTaxForIndividual.title = whichCountryTaxForIndividual" >> ../conf/messages.en
echo "whichCountryTaxForIndividual.heading = whichCountryTaxForIndividual" >> ../conf/messages.en
echo "whichCountryTaxForIndividual.checkYourAnswersLabel = whichCountryTaxForIndividual" >> ../conf/messages.en
echo "whichCountryTaxForIndividual.error.required = Enter whichCountryTaxForIndividual" >> ../conf/messages.en
echo "whichCountryTaxForIndividual.error.length = WhichCountryTaxForIndividual must be 35 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhichCountryTaxForIndividualUserAnswersEntry: Arbitrary[(WhichCountryTaxForIndividualPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[WhichCountryTaxForIndividualPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhichCountryTaxForIndividualPage: Arbitrary[WhichCountryTaxForIndividualPage.type] =";\
    print "    Arbitrary(WhichCountryTaxForIndividualPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(WhichCountryTaxForIndividualPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def whichCountryTaxForIndividual: Option[Row] = userAnswers.get(WhichCountryTaxForIndividualPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"whichCountryTaxForIndividual.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(lit\"$answer\"),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.WhichCountryTaxForIndividualController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"whichCountryTaxForIndividual.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration WhichCountryTaxForIndividual completed"
