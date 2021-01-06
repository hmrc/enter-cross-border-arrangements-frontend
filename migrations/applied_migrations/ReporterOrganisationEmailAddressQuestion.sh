#!/bin/bash

echo ""
echo "Applying migration ReporterOrganisationEmailAddressQuestion"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /reporterOrganisationEmailAddressQuestion                        controllers.ReporterOrganisationEmailAddressQuestionController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /reporterOrganisationEmailAddressQuestion                        controllers.ReporterOrganisationEmailAddressQuestionController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeReporterOrganisationEmailAddressQuestion                  controllers.ReporterOrganisationEmailAddressQuestionController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeReporterOrganisationEmailAddressQuestion                  controllers.ReporterOrganisationEmailAddressQuestionController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "reporterOrganisationEmailAddressQuestion.title = reporterOrganisationEmailAddressQuestion" >> ../conf/messages.en
echo "reporterOrganisationEmailAddressQuestion.heading = reporterOrganisationEmailAddressQuestion" >> ../conf/messages.en
echo "reporterOrganisationEmailAddressQuestion.checkYourAnswersLabel = reporterOrganisationEmailAddressQuestion" >> ../conf/messages.en
echo "reporterOrganisationEmailAddressQuestion.error.required = Select yes if reporterOrganisationEmailAddressQuestion" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryReporterOrganisationEmailAddressQuestionUserAnswersEntry: Arbitrary[(ReporterOrganisationEmailAddressQuestionPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[ReporterOrganisationEmailAddressQuestionPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryReporterOrganisationEmailAddressQuestionPage: Arbitrary[ReporterOrganisationEmailAddressQuestionPage.type] =";\
    print "    Arbitrary(ReporterOrganisationEmailAddressQuestionPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(ReporterOrganisationEmailAddressQuestionPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def reporterOrganisationEmailAddressQuestion: Option[Row] = userAnswers.get(ReporterOrganisationEmailAddressQuestionPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"reporterOrganisationEmailAddressQuestion.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(yesOrNo(answer)),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.ReporterOrganisationEmailAddressQuestionController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"reporterOrganisationEmailAddressQuestion.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration ReporterOrganisationEmailAddressQuestion completed"
