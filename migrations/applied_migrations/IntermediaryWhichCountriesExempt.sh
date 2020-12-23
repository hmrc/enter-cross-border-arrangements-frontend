#!/bin/bash

echo ""
echo "Applying migration IntermediaryWhichCountriesExempt"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /intermediaryWhichCountriesExempt                        controllers.IntermediaryWhichCountriesExemptController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /intermediaryWhichCountriesExempt                        controllers.IntermediaryWhichCountriesExemptController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeIntermediaryWhichCountriesExempt                  controllers.IntermediaryWhichCountriesExemptController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeIntermediaryWhichCountriesExempt                  controllers.IntermediaryWhichCountriesExemptController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "intermediaryWhichCountriesExempt.title = Which countries are you exempt from reporting in?" >> ../conf/messages.en
echo "intermediaryWhichCountriesExempt.heading = Which countries are you exempt from reporting in?" >> ../conf/messages.en
echo "intermediaryWhichCountriesExempt.austria = Austria" >> ../conf/messages.en
echo "intermediaryWhichCountriesExempt.belgium = Belgium" >> ../conf/messages.en
echo "intermediaryWhichCountriesExempt.checkYourAnswersLabel = Which countries are you exempt from reporting in?" >> ../conf/messages.en
echo "intermediaryWhichCountriesExempt.error.required = Select intermediaryWhichCountriesExempt" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIntermediaryWhichCountriesExemptUserAnswersEntry: Arbitrary[(IntermediaryWhichCountriesExemptPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[IntermediaryWhichCountriesExemptPage.type]";\
    print "        value <- arbitrary[IntermediaryWhichCountriesExempt].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIntermediaryWhichCountriesExemptPage: Arbitrary[IntermediaryWhichCountriesExemptPage.type] =";\
    print "    Arbitrary(IntermediaryWhichCountriesExemptPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIntermediaryWhichCountriesExempt: Arbitrary[IntermediaryWhichCountriesExempt] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(IntermediaryWhichCountriesExempt.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(IntermediaryWhichCountriesExemptPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def intermediaryWhichCountriesExempt: Option[Row] = userAnswers.get(IntermediaryWhichCountriesExemptPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"intermediaryWhichCountriesExempt.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(Html(answer.map(a => msg\"intermediaryWhichCountriesExempt.$a\".resolve).mkString(\",<br>\"))),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.IntermediaryWhichCountriesExemptController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"intermediaryWhichCountriesExempt.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration IntermediaryWhichCountriesExempt completed"
