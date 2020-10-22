#!/bin/bash

echo ""
echo "Applying migration ContactEmailAddressForOrganisation"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /contactEmailAddressForOrganisation                        controllers.ContactEmailAddressForOrganisationController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /contactEmailAddressForOrganisation                        controllers.ContactEmailAddressForOrganisationController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeContactEmailAddressForOrganisation                  controllers.ContactEmailAddressForOrganisationController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeContactEmailAddressForOrganisation                  controllers.ContactEmailAddressForOrganisationController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "emailAddressQuestionForOrganisation.title = contactEmailAddressForOrganisation" >> ../conf/messages.en
echo "emailAddressQuestionForOrganisation.heading = contactEmailAddressForOrganisation" >> ../conf/messages.en
echo "emailAddressQuestionForOrganisation.checkYourAnswersLabel = contactEmailAddressForOrganisation" >> ../conf/messages.en
echo "emailAddressQuestionForOrganisation.error.required = Select yes if contactEmailAddressForOrganisation" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryContactEmailAddressForOrganisationUserAnswersEntry: Arbitrary[(ContactEmailAddressForOrganisationPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[ContactEmailAddressForOrganisationPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryContactEmailAddressForOrganisationPage: Arbitrary[ContactEmailAddressForOrganisationPage.type] =";\
    print "    Arbitrary(ContactEmailAddressForOrganisationPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(ContactEmailAddressForOrganisationPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def contactEmailAddressForOrganisation: Option[Row] = userAnswers.get(ContactEmailAddressForOrganisationPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"emailAddressQuestionForOrganisation.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(yesOrNo(answer)),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.ContactEmailAddressForOrganisationController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"emailAddressQuestionForOrganisation.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration ContactEmailAddressForOrganisation completed"
