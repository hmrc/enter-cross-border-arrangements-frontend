#!/bin/bash

echo ""
echo "Applying migration EmailAddressForOrganisation"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /emailAddressForOrganisation                        controllers.EmailAddressForOrganisationController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /emailAddressForOrganisation                        controllers.EmailAddressForOrganisationController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeEmailAddressForOrganisation                  controllers.EmailAddressForOrganisationController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeEmailAddressForOrganisation                  controllers.EmailAddressForOrganisationController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "emailAddressForOrganisation.title = emailAddressForOrganisation" >> ../conf/messages.en
echo "emailAddressForOrganisation.heading = emailAddressForOrganisation" >> ../conf/messages.en
echo "emailAddressForOrganisation.checkYourAnswersLabel = emailAddressForOrganisation" >> ../conf/messages.en
echo "emailAddressForOrganisation.error.required = Enter emailAddressForOrganisation" >> ../conf/messages.en
echo "emailAddressForOrganisation.error.length = EmailAddressForOrganisation must be 254 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryEmailAddressForOrganisationUserAnswersEntry: Arbitrary[(EmailAddressForOrganisationPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[EmailAddressForOrganisationPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryEmailAddressForOrganisationPage: Arbitrary[EmailAddressForOrganisationPage.type] =";\
    print "    Arbitrary(EmailAddressForOrganisationPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(EmailAddressForOrganisationPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def emailAddressForOrganisation: Option[Row] = userAnswers.get(EmailAddressForOrganisationPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"emailAddressForOrganisation.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(lit\"$answer\"),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.EmailAddressForOrganisationController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"emailAddressForOrganisation.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration EmailAddressForOrganisation completed"
