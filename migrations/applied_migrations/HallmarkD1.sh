#!/bin/bash

echo ""
echo "Applying migration HallmarkD1"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /hallmarkD1                        controllers.HallmarkD1Controller.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /hallmarkD1                        controllers.HallmarkD1Controller.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeHallmarkD1                  controllers.HallmarkD1Controller.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeHallmarkD1                  controllers.HallmarkD1Controller.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "hallmarkD1.title = Which parts of hallmark D1 apply to this arrangement?" >> ../conf/messages.en
echo "hallmarkD1.heading = Which parts of hallmark D1 apply to this arrangement?" >> ../conf/messages.en
echo "hallmarkD1.d1a = D1a: involves use of an account, product or investment that is not a Financial Account" >> ../conf/messages.en
echo "hallmarkD1.d1b = D1b: involves the transfer to or use of countries that are not bound by the automatic exchange of financial information" >> ../conf/messages.en
echo "hallmarkD1.checkYourAnswersLabel = Which parts of hallmark D1 apply to this arrangement?" >> ../conf/messages.en
echo "hallmarkD1.error.required = Select hallmarkD1" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryHallmarkD1UserAnswersEntry: Arbitrary[(HallmarkD1Page.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[HallmarkD1Page.type]";\
    print "        value <- arbitrary[HallmarkD1].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryHallmarkD1Page: Arbitrary[HallmarkD1Page.type] =";\
    print "    Arbitrary(HallmarkD1Page)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryHallmarkD1: Arbitrary[HallmarkD1] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(HallmarkD1.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(HallmarkD1Page.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def hallmarkD1: Option[Row] = userAnswers.get(HallmarkD1Page) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"hallmarkD1.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(Html(answer.map(a => msg\"hallmarkD1.$a\".resolve).mkString(\",<br>\"))),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.HallmarkD1Controller.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"hallmarkD1.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration HallmarkD1 completed"
