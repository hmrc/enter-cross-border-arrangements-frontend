#!/bin/bash

echo ""
echo "Applying migration ExemptCountries"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /exemptCountries                        controllers.ExemptCountriesController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /exemptCountries                        controllers.ExemptCountriesController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeExemptCountries                  controllers.ExemptCountriesController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeExemptCountries                  controllers.ExemptCountriesController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "exemptCountries.title = exemptCountries" >> ../conf/messages.en
echo "exemptCountries.heading = exemptCountries" >> ../conf/messages.en
echo "exemptCountries.unitedKingdom = United Kingdom" >> ../conf/messages.en
echo "exemptCountries.austria = Austria" >> ../conf/messages.en
echo "exemptCountries.checkYourAnswersLabel = exemptCountries" >> ../conf/messages.en
echo "exemptCountries.error.required = Select exemptCountries" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryExemptCountriesUserAnswersEntry: Arbitrary[(ExemptCountriesPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[ExemptCountriesPage.type]";\
    print "        value <- arbitrary[ExemptCountries].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryExemptCountriesPage: Arbitrary[ExemptCountriesPage.type] =";\
    print "    Arbitrary(ExemptCountriesPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryExemptCountries: Arbitrary[ExemptCountries] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(ExemptCountries.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(ExemptCountriesPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def exemptCountries: Option[Row] = userAnswers.get(ExemptCountriesPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"exemptCountries.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(Html(answer.map(a => msg\"exemptCountries.$a\".resolve).mkString(\",<br>\"))),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.ExemptCountriesController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"exemptCountries.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration ExemptCountries completed"
