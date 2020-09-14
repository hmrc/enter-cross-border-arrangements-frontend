#!/bin/bash

echo ""
echo "Applying migration HallmarkA"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /hallmarkA                        controllers.HallmarkAController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /hallmarkA                        controllers.HallmarkAController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeHallmarkA                  controllers.HallmarkAController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeHallmarkA                  controllers.HallmarkAController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "hallmarkA.title = hallmarkA" >> ../conf/messages.en
echo "hallmarkA.heading = hallmarkA" >> ../conf/messages.en
echo "hallmarkA.option1 = Option 1" >> ../conf/messages.en
echo "hallmarkA.option2 = Option 2" >> ../conf/messages.en
echo "hallmarkA.checkYourAnswersLabel = hallmarkA" >> ../conf/messages.en
echo "hallmarkA.error.required = Select hallmarkA" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryHallmarkAUserAnswersEntry: Arbitrary[(HallmarkAPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[HallmarkAPage.type]";\
    print "        value <- arbitrary[HallmarkA].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryHallmarkAPage: Arbitrary[HallmarkAPage.type] =";\
    print "    Arbitrary(HallmarkAPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryHallmarkA: Arbitrary[HallmarkA] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(HallmarkA.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(HallmarkAPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def hallmarkA: Option[Row] = userAnswers.get(HallmarkAPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"hallmarkA.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(Html(answer.map(a => msg\"hallmarkA.$a\".resolve).mkString(\",<br>\"))),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.HallmarkAController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"hallmarkA.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration HallmarkA completed"
