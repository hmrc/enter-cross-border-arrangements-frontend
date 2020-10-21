#!/bin/bash

echo ""
echo "Applying migration IndividualPlaceOfBirth"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /individualPlaceOfBirth                        controllers.IndividualPlaceOfBirthController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /individualPlaceOfBirth                        controllers.IndividualPlaceOfBirthController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeIndividualPlaceOfBirth                  controllers.IndividualPlaceOfBirthController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeIndividualPlaceOfBirth                  controllers.IndividualPlaceOfBirthController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "individualPlaceOfBirth.title = individualPlaceOfBirth" >> ../conf/messages.en
echo "individualPlaceOfBirth.heading = individualPlaceOfBirth" >> ../conf/messages.en
echo "individualPlaceOfBirth.checkYourAnswersLabel = individualPlaceOfBirth" >> ../conf/messages.en
echo "individualPlaceOfBirth.error.required = Enter individualPlaceOfBirth" >> ../conf/messages.en
echo "individualPlaceOfBirth.error.length = IndividualPlaceOfBirth must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualPlaceOfBirthUserAnswersEntry: Arbitrary[(IndividualPlaceOfBirthPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[IndividualPlaceOfBirthPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualPlaceOfBirthPage: Arbitrary[IndividualPlaceOfBirthPage.type] =";\
    print "    Arbitrary(IndividualPlaceOfBirthPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(IndividualPlaceOfBirthPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def individualPlaceOfBirth: Option[Row] = userAnswers.get(IndividualPlaceOfBirthPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"individualPlaceOfBirth.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(lit\"$answer\"),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.IndividualPlaceOfBirthController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"individualPlaceOfBirth.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration IndividualPlaceOfBirth completed"
