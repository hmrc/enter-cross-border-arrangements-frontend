#!/bin/bash

echo ""
echo "Applying migration ReplaceOrDeleteADisclosure"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /replaceOrDeleteADisclosure                        controllers.ReplaceOrDeleteADisclosureController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /replaceOrDeleteADisclosure                        controllers.ReplaceOrDeleteADisclosureController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeReplaceOrDeleteADisclosure                  controllers.ReplaceOrDeleteADisclosureController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeReplaceOrDeleteADisclosure                  controllers.ReplaceOrDeleteADisclosureController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "replaceOrDeleteADisclosure.title = replaceOrDeleteADisclosure" >> ../conf/messages.en
echo "replaceOrDeleteADisclosure.heading = replaceOrDeleteADisclosure" >> ../conf/messages.en
echo "replaceOrDeleteADisclosure.arrangementID = arrangementID" >> ../conf/messages.en
echo "replaceOrDeleteADisclosure.disclosureID = disclosureID" >> ../conf/messages.en
echo "replaceOrDeleteADisclosure.checkYourAnswersLabel = replaceOrDeleteADisclosure" >> ../conf/messages.en
echo "replaceOrDeleteADisclosure.error.arrangementID.required = Enter arrangementID" >> ../conf/messages.en
echo "replaceOrDeleteADisclosure.error.disclosureID.required = Enter disclosureID" >> ../conf/messages.en
echo "replaceOrDeleteADisclosure.error.arrangementID.length = arrangementID must be 20 characters or less" >> ../conf/messages.en
echo "replaceOrDeleteADisclosure.error.disclosureID.length = disclosureID must be 20 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryReplaceOrDeleteADisclosureUserAnswersEntry: Arbitrary[(ReplaceOrDeleteADisclosurePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[ReplaceOrDeleteADisclosurePage.type]";\
    print "        value <- arbitrary[ReplaceOrDeleteADisclosure].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryReplaceOrDeleteADisclosurePage: Arbitrary[ReplaceOrDeleteADisclosurePage.type] =";\
    print "    Arbitrary(ReplaceOrDeleteADisclosurePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryReplaceOrDeleteADisclosure: Arbitrary[ReplaceOrDeleteADisclosure] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        arrangementID <- arbitrary[String]";\
    print "        disclosureID <- arbitrary[String]";\
    print "      } yield ReplaceOrDeleteADisclosure(arrangementID, disclosureID)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(ReplaceOrDeleteADisclosurePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def replaceOrDeleteADisclosure: Option[Row] = userAnswers.get(ReplaceOrDeleteADisclosurePage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"replaceOrDeleteADisclosure.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(lit\"${answer.arrangementID} ${answer.disclosureID}\"),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.ReplaceOrDeleteADisclosureController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"replaceOrDeleteADisclosure.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration ReplaceOrDeleteADisclosure completed"
