#!/bin/bash

echo ""
echo "Applying migration iIsOrganisationAddressUk"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /iIsOrganisationAddressUk                        controllers.iIsOrganisationAddressUkController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /iIsOrganisationAddressUk                        controllers.iIsOrganisationAddressUkController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeiIsOrganisationAddressUk                  controllers.iIsOrganisationAddressUkController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeiIsOrganisationAddressUk                  controllers.iIsOrganisationAddressUkController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "iIsOrganisationAddressUk.title = iIsOrganisationAddressUk" >> ../conf/messages.en
echo "iIsOrganisationAddressUk.heading = iIsOrganisationAddressUk" >> ../conf/messages.en
echo "iIsOrganisationAddressUk.checkYourAnswersLabel = iIsOrganisationAddressUk" >> ../conf/messages.en
echo "iIsOrganisationAddressUk.error.required = Select yes if iIsOrganisationAddressUk" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryiIsOrganisationAddressUkUserAnswersEntry: Arbitrary[(iIsOrganisationAddressUkPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[iIsOrganisationAddressUkPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryiIsOrganisationAddressUkPage: Arbitrary[iIsOrganisationAddressUkPage.type] =";\
    print "    Arbitrary(iIsOrganisationAddressUkPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(iIsOrganisationAddressUkPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def iIsOrganisationAddressUk: Option[Row] = userAnswers.get(iIsOrganisationAddressUkPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"iIsOrganisationAddressUk.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(yesOrNo(answer)),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.iIsOrganisationAddressUkController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"iIsOrganisationAddressUk.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration iIsOrganisationAddressUk completed"
