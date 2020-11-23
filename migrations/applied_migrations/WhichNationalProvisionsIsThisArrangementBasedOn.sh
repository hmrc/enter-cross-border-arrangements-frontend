#!/bin/bash

echo ""
echo "Applying migration WhichNationalProvisionsIsThisArrangementBasedOn"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whichNationalProvisionsIsThisArrangementBasedOn                        controllers.WhichNationalProvisionsIsThisArrangementBasedOnController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whichNationalProvisionsIsThisArrangementBasedOn                        controllers.WhichNationalProvisionsIsThisArrangementBasedOnController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhichNationalProvisionsIsThisArrangementBasedOn                  controllers.WhichNationalProvisionsIsThisArrangementBasedOnController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhichNationalProvisionsIsThisArrangementBasedOn                  controllers.WhichNationalProvisionsIsThisArrangementBasedOnController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whichNationalProvisionsIsThisArrangementBasedOn.title = whichNationalProvisionsIsThisArrangementBasedOn" >> ../conf/messages.en
echo "whichNationalProvisionsIsThisArrangementBasedOn.heading = whichNationalProvisionsIsThisArrangementBasedOn" >> ../conf/messages.en
echo "whichNationalProvisionsIsThisArrangementBasedOn.checkYourAnswersLabel = whichNationalProvisionsIsThisArrangementBasedOn" >> ../conf/messages.en
echo "whichNationalProvisionsIsThisArrangementBasedOn.error.required = Enter whichNationalProvisionsIsThisArrangementBasedOn" >> ../conf/messages.en
echo "whichNationalProvisionsIsThisArrangementBasedOn.error.length = WhichNationalProvisionsIsThisArrangementBasedOn must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhichNationalProvisionsIsThisArrangementBasedOnUserAnswersEntry: Arbitrary[(WhichNationalProvisionsIsThisArrangementBasedOnPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[WhichNationalProvisionsIsThisArrangementBasedOnPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhichNationalProvisionsIsThisArrangementBasedOnPage: Arbitrary[WhichNationalProvisionsIsThisArrangementBasedOnPage.type] =";\
    print "    Arbitrary(WhichNationalProvisionsIsThisArrangementBasedOnPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(WhichNationalProvisionsIsThisArrangementBasedOnPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def whichNationalProvisionsIsThisArrangementBasedOn: Option[Row] = userAnswers.get(WhichNationalProvisionsIsThisArrangementBasedOnPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"whichNationalProvisionsIsThisArrangementBasedOn.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(lit\"$answer\"),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.WhichNationalProvisionsIsThisArrangementBasedOnController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"whichNationalProvisionsIsThisArrangementBasedOn.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration WhichNationalProvisionsIsThisArrangementBasedOn completed"
