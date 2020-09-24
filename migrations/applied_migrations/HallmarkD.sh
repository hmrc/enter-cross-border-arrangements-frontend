#!/bin/bash

echo ""
echo "Applying migration HallmarkD"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /hallmarkD                        controllers.HallmarkDController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /hallmarkD                        controllers.HallmarkDController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeHallmarkD                  controllers.HallmarkDController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeHallmarkD                  controllers.HallmarkDController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "hallmarkD.title = Which parts of hallmark D apply to this arrangement?" >> ../conf/messages.en
echo "hallmarkD.heading = Which parts of hallmark D apply to this arrangement?" >> ../conf/messages.en
echo "hallmarkD.d1 = D1: undermining reporting obligations including the Common Reporting Standard (CRS)" >> ../conf/messages.en
echo "hallmarkD.d2 = D2: obscuring beneficial ownership" >> ../conf/messages.en
echo "hallmarkD.checkYourAnswersLabel = Which parts of hallmark D apply to this arrangement?" >> ../conf/messages.en
echo "hallmarkD.error.required = Select hallmarkD" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryHallmarkDUserAnswersEntry: Arbitrary[(HallmarkDPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[HallmarkDPage.type]";\
    print "        value <- arbitrary[HallmarkD].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryHallmarkDPage: Arbitrary[HallmarkDPage.type] =";\
    print "    Arbitrary(HallmarkDPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryHallmarkD: Arbitrary[HallmarkD] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(HallmarkD.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(HallmarkDPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def hallmarkD: Option[Row] = userAnswers.get(HallmarkDPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"hallmarkD.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(Html(answer.map(a => msg\"hallmarkD.$a\".resolve).mkString(\",<br>\"))),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.HallmarkDController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"hallmarkD.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration HallmarkD completed"
