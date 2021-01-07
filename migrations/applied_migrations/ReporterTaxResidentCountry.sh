#!/bin/bash

echo ""
echo "Applying migration ReporterTaxResidentCountry"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /reporterTaxResidentCountry                        controllers.ReporterTaxResidentCountryController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /reporterTaxResidentCountry                        controllers.ReporterTaxResidentCountryController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeReporterTaxResidentCountry                  controllers.ReporterTaxResidentCountryController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeReporterTaxResidentCountry                  controllers.ReporterTaxResidentCountryController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "reporterTaxResidentCountry.title = reporterTaxResidentCountry" >> ../conf/messages.en
echo "reporterTaxResidentCountry.heading = reporterTaxResidentCountry" >> ../conf/messages.en
echo "reporterTaxResidentCountry.checkYourAnswersLabel = reporterTaxResidentCountry" >> ../conf/messages.en
echo "reporterTaxResidentCountry.error.required = Enter reporterTaxResidentCountry" >> ../conf/messages.en
echo "reporterTaxResidentCountry.error.length = ReporterTaxResidentCountry must be 200 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryReporterTaxResidentCountryUserAnswersEntry: Arbitrary[(ReporterTaxResidentCountryPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[ReporterTaxResidentCountryPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryReporterTaxResidentCountryPage: Arbitrary[ReporterTaxResidentCountryPage.type] =";\
    print "    Arbitrary(ReporterTaxResidentCountryPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(ReporterTaxResidentCountryPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def reporterTaxResidentCountry: Option[Row] = userAnswers.get(ReporterTaxResidentCountryPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"reporterTaxResidentCountry.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(lit\"$answer\"),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.ReporterTaxResidentCountryController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"reporterTaxResidentCountry.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration ReporterTaxResidentCountry completed"
