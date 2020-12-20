#!/bin/bash

echo ""
echo "Applying migration DisclosureMarketable"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /disclosureMarketable                        controllers.DisclosureMarketableController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /disclosureMarketable                        controllers.DisclosureMarketableController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeDisclosureMarketable                  controllers.DisclosureMarketableController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeDisclosureMarketable                  controllers.DisclosureMarketableController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "disclosureMarketable.title = disclosureMarketable" >> ../conf/messages.en
echo "disclosureMarketable.heading = disclosureMarketable" >> ../conf/messages.en
echo "disclosureMarketable.checkYourAnswersLabel = disclosureMarketable" >> ../conf/messages.en
echo "disclosureMarketable.error.required = Select yes if disclosureMarketable" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryDisclosureMarketableUserAnswersEntry: Arbitrary[(DisclosureMarketablePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[DisclosureMarketablePage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryDisclosureMarketablePage: Arbitrary[DisclosureMarketablePage.type] =";\
    print "    Arbitrary(DisclosureMarketablePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(DisclosureMarketablePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def disclosureMarketable: Option[Row] = userAnswers.get(DisclosureMarketablePage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"disclosureMarketable.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(yesOrNo(answer)),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.DisclosureMarketableController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"disclosureMarketable.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration DisclosureMarketable completed"
