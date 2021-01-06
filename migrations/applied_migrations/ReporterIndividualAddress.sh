#!/bin/bash

echo ""
echo "Applying migration ReporterIndividualAddress"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /reporterIndividualAddress                        controllers.ReporterIndividualAddressController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /reporterIndividualAddress                        controllers.ReporterIndividualAddressController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeReporterIndividualAddress                  controllers.ReporterIndividualAddressController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeReporterIndividualAddress                  controllers.ReporterIndividualAddressController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "reporterIndividualAddress.title = reporterIndividualAddress" >> ../conf/messages.en
echo "reporterIndividualAddress.heading = reporterIndividualAddress" >> ../conf/messages.en
echo "reporterIndividualAddress.checkYourAnswersLabel = reporterIndividualAddress" >> ../conf/messages.en
echo "reporterIndividualAddress.error.required = Enter reporterIndividualAddress" >> ../conf/messages.en
echo "reporterIndividualAddress.error.length = ReporterIndividualAddress must be 10 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryReporterIndividualAddressUserAnswersEntry: Arbitrary[(ReporterIndividualAddressPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[ReporterIndividualAddressPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryReporterIndividualAddressPage: Arbitrary[ReporterIndividualAddressPage.type] =";\
    print "    Arbitrary(ReporterIndividualAddressPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(ReporterIndividualAddressPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def reporterIndividualAddress: Option[Row] = userAnswers.get(ReporterIndividualAddressPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"reporterIndividualAddress.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(lit\"$answer\"),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.ReporterIndividualAddressController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"reporterIndividualAddress.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration ReporterIndividualAddress completed"
