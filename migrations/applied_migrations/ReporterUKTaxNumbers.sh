#!/bin/bash

echo ""
echo "Applying migration ReporterUKTaxNumbers"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /reporterUKTaxNumbers                        controllers.ReporterUKTaxNumbersController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /reporterUKTaxNumbers                        controllers.ReporterUKTaxNumbersController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeReporterUKTaxNumbers                  controllers.ReporterUKTaxNumbersController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeReporterUKTaxNumbers                  controllers.ReporterUKTaxNumbersController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "reporterUKTaxNumbers.title = reporterUKTaxNumbers" >> ../conf/messages.en
echo "reporterUKTaxNumbers.heading = reporterUKTaxNumbers" >> ../conf/messages.en
echo "reporterUKTaxNumbers.checkYourAnswersLabel = reporterUKTaxNumbers" >> ../conf/messages.en
echo "reporterUKTaxNumbers.error.required = Enter reporterUKTaxNumbers" >> ../conf/messages.en
echo "reporterUKTaxNumbers.error.length = ReporterUKTaxNumbers must be 200 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryReporterUKTaxNumbersUserAnswersEntry: Arbitrary[(ReporterUKTaxNumbersPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[ReporterUKTaxNumbersPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryReporterUKTaxNumbersPage: Arbitrary[ReporterUKTaxNumbersPage.type] =";\
    print "    Arbitrary(ReporterUKTaxNumbersPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(ReporterUKTaxNumbersPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def reporterUKTaxNumbers: Option[Row] = userAnswers.get(ReporterUKTaxNumbersPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"reporterUKTaxNumbers.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(lit\"$answer\"),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.ReporterUKTaxNumbersController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"reporterUKTaxNumbers.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration ReporterUKTaxNumbers completed"
