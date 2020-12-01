#!/bin/bash

echo ""
echo "Applying migration GiveDetailsOfThisArrangement"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /giveDetailsOfThisArrangement                        controllers.GiveDetailsOfThisArrangementController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /giveDetailsOfThisArrangement                        controllers.GiveDetailsOfThisArrangementController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeGiveDetailsOfThisArrangement                  controllers.GiveDetailsOfThisArrangementController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeGiveDetailsOfThisArrangement                  controllers.GiveDetailsOfThisArrangementController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "giveDetailsOfThisArrangement.title = giveDetailsOfThisArrangement" >> ../conf/messages.en
echo "giveDetailsOfThisArrangement.heading = giveDetailsOfThisArrangement" >> ../conf/messages.en
echo "giveDetailsOfThisArrangement.checkYourAnswersLabel = giveDetailsOfThisArrangement" >> ../conf/messages.en
echo "giveDetailsOfThisArrangement.error.required = Enter giveDetailsOfThisArrangement" >> ../conf/messages.en
echo "giveDetailsOfThisArrangement.error.length = GiveDetailsOfThisArrangement must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryGiveDetailsOfThisArrangementUserAnswersEntry: Arbitrary[(GiveDetailsOfThisArrangementPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[GiveDetailsOfThisArrangementPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryGiveDetailsOfThisArrangementPage: Arbitrary[GiveDetailsOfThisArrangementPage.type] =";\
    print "    Arbitrary(GiveDetailsOfThisArrangementPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(GiveDetailsOfThisArrangementPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def giveDetailsOfThisArrangement: Option[Row] = userAnswers.get(GiveDetailsOfThisArrangementPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"giveDetailsOfThisArrangement.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(lit\"$answer\"),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.GiveDetailsOfThisArrangementController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"giveDetailsOfThisArrangement.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration GiveDetailsOfThisArrangement completed"
