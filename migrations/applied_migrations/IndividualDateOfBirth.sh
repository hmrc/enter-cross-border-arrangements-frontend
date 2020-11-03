#!/bin/bash

echo ""
echo "Applying migration IndividualDateOfBirth"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /individualDateOfBirth                  controllers.IndividualDateOfBirthController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /individualDateOfBirth                  controllers.IndividualDateOfBirthController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeIndividualDateOfBirth                        controllers.IndividualDateOfBirthController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeIndividualDateOfBirth                        controllers.IndividualDateOfBirthController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "individualDateOfBirth.title = IndividualDateOfBirth" >> ../conf/messages.en
echo "individualDateOfBirth.heading = IndividualDateOfBirth" >> ../conf/messages.en
echo "individualDateOfBirth.hint = For example, 12 11 2007" >> ../conf/messages.en
echo "individualDateOfBirth.checkYourAnswersLabel = IndividualDateOfBirth" >> ../conf/messages.en
echo "individualDateOfBirth.error.required.all = Enter the individualDateOfBirth" >> ../conf/messages.en
echo "individualDateOfBirth.error.required.two = The individualDateOfBirth" must include {0} and {1} >> ../conf/messages.en
echo "individualDateOfBirth.error.required = The individualDateOfBirth must include {0}" >> ../conf/messages.en
echo "individualDateOfBirth.error.invalid = Enter a real IndividualDateOfBirth" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualDateOfBirthUserAnswersEntry: Arbitrary[(IndividualDateOfBirthPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[IndividualDateOfBirthPage.type]";\
    print "        value <- arbitrary[Int].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualDateOfBirthPage: Arbitrary[IndividualDateOfBirthPage.type] =";\
    print "    Arbitrary(IndividualDateOfBirthPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(IndividualDateOfBirthPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def individualDateOfBirth: Option[Row] = userAnswers.get(IndividualDateOfBirthPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"individualDateOfBirth.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(Literal(answer.format(dateFormatter))),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.IndividualDateOfBirthController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"individualDateOfBirth.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration IndividualDateOfBirth completed"
