#!/bin/bash

echo ""
echo "Applying migration ReporterOrganisationOrIndividual"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /reporterOrganisationOrIndividual                        controllers.ReporterOrganisationOrIndividualController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /reporterOrganisationOrIndividual                        controllers.ReporterOrganisationOrIndividualController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeReporterOrganisationOrIndividual                  controllers.ReporterOrganisationOrIndividualController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeReporterOrganisationOrIndividual                  controllers.ReporterOrganisationOrIndividualController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "reporterOrganisationOrIndividual.title = Are you reporting as an organisation or individual?" >> ../conf/messages.en
echo "reporterOrganisationOrIndividual.heading = Are you reporting as an organisation or individual?" >> ../conf/messages.en
echo "reporterOrganisationOrIndividual.organisation = Organisation" >> ../conf/messages.en
echo "reporterOrganisationOrIndividual.individual = Individual" >> ../conf/messages.en
echo "reporterOrganisationOrIndividual.checkYourAnswersLabel = Are you reporting as an organisation or individual?" >> ../conf/messages.en
echo "reporterOrganisationOrIndividual.error.required = Select reporterOrganisationOrIndividual" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryReporterOrganisationOrIndividualUserAnswersEntry: Arbitrary[(ReporterOrganisationOrIndividualPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[ReporterOrganisationOrIndividualPage.type]";\
    print "        value <- arbitrary[ReporterOrganisationOrIndividual].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryReporterOrganisationOrIndividualPage: Arbitrary[ReporterOrganisationOrIndividualPage.type] =";\
    print "    Arbitrary(ReporterOrganisationOrIndividualPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryReporterOrganisationOrIndividual: Arbitrary[ReporterOrganisationOrIndividual] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(ReporterOrganisationOrIndividual.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(ReporterOrganisationOrIndividualPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def reporterOrganisationOrIndividual: Option[Row] = userAnswers.get(ReporterOrganisationOrIndividualPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"reporterOrganisationOrIndividual.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(msg\"reporterOrganisationOrIndividual.$answer\"),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.ReporterOrganisationOrIndividualController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"reporterOrganisationOrIndividual.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration ReporterOrganisationOrIndividual completed"
