#!/bin/bash

echo ""
echo "Applying migration ReporterIndividualSelectAddress"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /reporterIndividualSelectAddress                        controllers.ReporterIndividualSelectAddressController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /reporterIndividualSelectAddress                        controllers.ReporterIndividualSelectAddressController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeReporterIndividualSelectAddress                  controllers.ReporterIndividualSelectAddressController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeReporterIndividualSelectAddress                  controllers.ReporterIndividualSelectAddressController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "reporterIndividualSelectAddress.title = What is your address?" >> ../conf/messages.en
echo "reporterIndividualSelectAddress.heading = What is your address?" >> ../conf/messages.en
echo "reporterIndividualSelectAddress.1 = 2" >> ../conf/messages.en
echo "reporterIndividualSelectAddress.3 = 4" >> ../conf/messages.en
echo "reporterIndividualSelectAddress.checkYourAnswersLabel = What is your address?" >> ../conf/messages.en
echo "reporterIndividualSelectAddress.error.required = Select reporterIndividualSelectAddress" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryReporterIndividualSelectAddressUserAnswersEntry: Arbitrary[(ReporterIndividualSelectAddressPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[ReporterIndividualSelectAddressPage.type]";\
    print "        value <- arbitrary[ReporterIndividualSelectAddress].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryReporterIndividualSelectAddressPage: Arbitrary[ReporterIndividualSelectAddressPage.type] =";\
    print "    Arbitrary(ReporterIndividualSelectAddressPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryReporterIndividualSelectAddress: Arbitrary[ReporterIndividualSelectAddress] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(ReporterIndividualSelectAddress.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(ReporterIndividualSelectAddressPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def reporterIndividualSelectAddress: Option[Row] = userAnswers.get(ReporterIndividualSelectAddressPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"reporterIndividualSelectAddress.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(msg\"reporterIndividualSelectAddress.$answer\"),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.ReporterIndividualSelectAddressController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"reporterIndividualSelectAddress.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration ReporterIndividualSelectAddress completed"
