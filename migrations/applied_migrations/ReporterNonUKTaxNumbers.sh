#!/bin/bash

echo ""
echo "Applying migration ReporterNonUKTaxNumbers"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /reporterNonUKTaxNumbers                        controllers.ReporterNonUKTaxNumbersController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /reporterNonUKTaxNumbers                        controllers.ReporterNonUKTaxNumbersController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeReporterNonUKTaxNumbers                  controllers.ReporterNonUKTaxNumbersController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeReporterNonUKTaxNumbers                  controllers.ReporterNonUKTaxNumbersController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "reporterNonUKTaxNumbers.title = reporterNonUKTaxNumbers" >> ../conf/messages.en
echo "reporterNonUKTaxNumbers.heading = reporterNonUKTaxNumbers" >> ../conf/messages.en
echo "reporterNonUKTaxNumbers.checkYourAnswersLabel = reporterNonUKTaxNumbers" >> ../conf/messages.en
echo "reporterNonUKTaxNumbers.error.required = Enter reporterNonUKTaxNumbers" >> ../conf/messages.en
echo "reporterNonUKTaxNumbers.error.length = ReporterNonUKTaxNumbers must be 200 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryReporterNonUKTaxNumbersUserAnswersEntry: Arbitrary[(ReporterNonUKTaxNumbersPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[ReporterNonUKTaxNumbersPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryReporterNonUKTaxNumbersPage: Arbitrary[ReporterNonUKTaxNumbersPage.type] =";\
    print "    Arbitrary(ReporterNonUKTaxNumbersPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(ReporterNonUKTaxNumbersPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def reporterNonUKTaxNumbers: Option[Row] = userAnswers.get(ReporterNonUKTaxNumbersPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"reporterNonUKTaxNumbers.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(lit\"$answer\"),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.ReporterNonUKTaxNumbersController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"reporterNonUKTaxNumbers.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration ReporterNonUKTaxNumbers completed"
