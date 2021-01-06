#!/bin/bash

echo ""
echo "Applying migration ReporterIndividualEmailAddress"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /reporterIndividualEmailAddress                        controllers.ReporterIndividualEmailAddressController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /reporterIndividualEmailAddress                        controllers.ReporterIndividualEmailAddressController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeReporterIndividualEmailAddress                  controllers.ReporterIndividualEmailAddressController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeReporterIndividualEmailAddress                  controllers.ReporterIndividualEmailAddressController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "reporterIndividualEmailAddress.title = reporterIndividualEmailAddress" >> ../conf/messages.en
echo "reporterIndividualEmailAddress.heading = reporterIndividualEmailAddress" >> ../conf/messages.en
echo "reporterIndividualEmailAddress.checkYourAnswersLabel = reporterIndividualEmailAddress" >> ../conf/messages.en
echo "reporterIndividualEmailAddress.error.required = Select yes if reporterIndividualEmailAddress" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryReporterIndividualEmailAddressUserAnswersEntry: Arbitrary[(ReporterIndividualEmailAddressPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[ReporterIndividualEmailAddressPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryReporterIndividualEmailAddressPage: Arbitrary[ReporterIndividualEmailAddressPage.type] =";\
    print "    Arbitrary(ReporterIndividualEmailAddressPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(ReporterIndividualEmailAddressPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def reporterIndividualEmailAddress: Option[Row] = userAnswers.get(ReporterIndividualEmailAddressPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"reporterIndividualEmailAddress.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(yesOrNo(answer)),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.ReporterIndividualEmailAddressController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"reporterIndividualEmailAddress.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration ReporterIndividualEmailAddress completed"
