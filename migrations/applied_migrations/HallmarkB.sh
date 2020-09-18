#!/bin/bash

echo ""
echo "Applying migration HallmarkB"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /hallmarkB                        controllers.HallmarkBController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /hallmarkB                        controllers.HallmarkBController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeHallmarkB                  controllers.HallmarkBController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeHallmarkB                  controllers.HallmarkBController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "hallmarkB.title = hallmarkB" >> ../conf/messages.en
echo "hallmarkB.heading = hallmarkB" >> ../conf/messages.en
echo "hallmarkB.option1 = Option 1" >> ../conf/messages.en
echo "hallmarkB.option2 = Option 2" >> ../conf/messages.en
echo "hallmarkB.checkYourAnswersLabel = hallmarkB" >> ../conf/messages.en
echo "hallmarkB.error.required = Select hallmarkB" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryHallmarkBUserAnswersEntry: Arbitrary[(HallmarkBPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[HallmarkBPage.type]";\
    print "        value <- arbitrary[HallmarkB].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryHallmarkBPage: Arbitrary[HallmarkBPage.type] =";\
    print "    Arbitrary(HallmarkBPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryHallmarkB: Arbitrary[HallmarkB] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(HallmarkB.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(HallmarkBPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def hallmarkB: Option[Row] = userAnswers.get(HallmarkBPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"hallmarkB.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(Html(answer.map(a => msg\"hallmarkB.$a\".resolve).mkString(\",<br>\"))),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.HallmarkBController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"hallmarkB.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration HallmarkB completed"
