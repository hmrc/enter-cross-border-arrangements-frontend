#!/bin/bash

echo ""
echo "Applying migration EmailAddressQuestionForIndividual"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /emailAddressQuestionForIndividual                        controllers.EmailAddressQuestionForIndividualController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /emailAddressQuestionForIndividual                        controllers.EmailAddressQuestionForIndividualController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeEmailAddressQuestionForIndividual                  controllers.EmailAddressQuestionForIndividualController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeEmailAddressQuestionForIndividual                  controllers.EmailAddressQuestionForIndividualController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "emailAddressQuestionForIndividual.title = emailAddressQuestionForIndividual" >> ../conf/messages.en
echo "emailAddressQuestionForIndividual.heading = emailAddressQuestionForIndividual" >> ../conf/messages.en
echo "emailAddressQuestionForIndividual.checkYourAnswersLabel = emailAddressQuestionForIndividual" >> ../conf/messages.en
echo "emailAddressQuestionForIndividual.error.required = Select yes if emailAddressQuestionForIndividual" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryEmailAddressQuestionForIndividualUserAnswersEntry: Arbitrary[(EmailAddressQuestionForIndividualPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[EmailAddressQuestionForIndividualPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryEmailAddressQuestionForIndividualPage: Arbitrary[EmailAddressQuestionForIndividualPage.type] =";\
    print "    Arbitrary(EmailAddressQuestionForIndividualPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(EmailAddressQuestionForIndividualPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def emailAddressQuestionForIndividual: Option[Row] = userAnswers.get(EmailAddressQuestionForIndividualPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"emailAddressQuestionForIndividual.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(yesOrNo(answer)),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.EmailAddressQuestionForIndividualController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"emailAddressQuestionForIndividual.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration EmailAddressQuestionForIndividual completed"
