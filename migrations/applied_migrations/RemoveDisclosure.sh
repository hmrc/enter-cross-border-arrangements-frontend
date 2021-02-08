#!/bin/bash

echo ""
echo "Applying migration RemoveDisclosure"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /removeDisclosure                        controllers.RemoveDisclosureController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /removeDisclosure                        controllers.RemoveDisclosureController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeRemoveDisclosure                  controllers.RemoveDisclosureController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeRemoveDisclosure                  controllers.RemoveDisclosureController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "removeDisclosure.title = removeDisclosure" >> ../conf/messages.en
echo "removeDisclosure.heading = removeDisclosure" >> ../conf/messages.en
echo "removeDisclosure.checkYourAnswersLabel = removeDisclosure" >> ../conf/messages.en
echo "removeDisclosure.error.required = Select yes if removeDisclosure" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryRemoveDisclosureUserAnswersEntry: Arbitrary[(RemoveDisclosurePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[RemoveDisclosurePage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryRemoveDisclosurePage: Arbitrary[RemoveDisclosurePage.type] =";\
    print "    Arbitrary(RemoveDisclosurePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(RemoveDisclosurePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def removeDisclosure: Option[Row] = userAnswers.get(RemoveDisclosurePage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"removeDisclosure.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(yesOrNo(answer)),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.RemoveDisclosureController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"removeDisclosure.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration RemoveDisclosure completed"
