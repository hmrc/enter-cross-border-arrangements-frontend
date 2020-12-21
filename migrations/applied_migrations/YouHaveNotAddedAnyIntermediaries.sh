#!/bin/bash

echo ""
echo "Applying migration YouHaveNotAddedAnyIntermediaries"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /youHaveNotAddedAnyIntermediaries                        controllers.intermediaries.YouHaveNotAddedAnyIntermediariesController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /youHaveNotAddedAnyIntermediaries                        controllers.intermediaries.YouHaveNotAddedAnyIntermediariesController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeYouHaveNotAddedAnyIntermediaries                  controllers.intermediaries.YouHaveNotAddedAnyIntermediariesController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeYouHaveNotAddedAnyIntermediaries                  controllers.intermediaries.YouHaveNotAddedAnyIntermediariesController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "youHaveNotAddedAnyIntermediaries.title = You have not added any intermediaries" >> ../conf/messages.en
echo "youHaveNotAddedAnyIntermediaries.heading = You have not added any intermediaries" >> ../conf/messages.en
echo "youHaveNotAddedAnyIntermediaries.yesAddNow = Yes, add now" >> ../conf/messages.en
echo "youHaveNotAddedAnyIntermediaries.yesAddLater = Yes, add later" >> ../conf/messages.en
echo "youHaveNotAddedAnyIntermediaries.checkYourAnswersLabel = You have not added any intermediaries" >> ../conf/messages.en
echo "youHaveNotAddedAnyIntermediaries.error.required = Select youHaveNotAddedAnyIntermediaries" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryYouHaveNotAddedAnyIntermediariesUserAnswersEntry: Arbitrary[(pages.intermediaries.YouHaveNotAddedAnyIntermediariesPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[pages.intermediaries.YouHaveNotAddedAnyIntermediariesPage.type]";\
    print "        value <- arbitrary[models.intermediaries.YouHaveNotAddedAnyIntermediaries].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryYouHaveNotAddedAnyIntermediariesPage: Arbitrary[pages.intermediaries.YouHaveNotAddedAnyIntermediariesPage.type] =";\
    print "    Arbitrary(pages.intermediaries.YouHaveNotAddedAnyIntermediariesPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryYouHaveNotAddedAnyIntermediaries: Arbitrary[models.intermediaries.YouHaveNotAddedAnyIntermediaries] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(models.intermediaries.YouHaveNotAddedAnyIntermediaries.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(pages.intermediaries.YouHaveNotAddedAnyIntermediariesPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/extends IndividualRows/ {\
     print;\
     print "";\
     print "  def youHaveNotAddedAnyIntermediaries: Option[Row] = userAnswers.get(pages.intermediaries.YouHaveNotAddedAnyIntermediariesPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"youHaveNotAddedAnyIntermediaries.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(Html(answer.map(a => msg\"youHaveNotAddedAnyIntermediaries.$a\".resolve).mkString(\",<br>\"))),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = controllers.intermediaries.routes.YouHaveNotAddedAnyIntermediariesController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"youHaveNotAddedAnyIntermediaries.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration YouHaveNotAddedAnyIntermediaries completed"
