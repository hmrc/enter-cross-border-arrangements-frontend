#!/bin/bash

echo ""
echo "Applying migration HallmarkCategories"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /hallmarkCategories                        controllers.HallmarkCategoriesController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /hallmarkCategories                        controllers.HallmarkCategoriesController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeHallmarkCategories                  controllers.HallmarkCategoriesController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeHallmarkCategories                  controllers.HallmarkCategoriesController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "hallmarkCategories.title = hallmarkCategories" >> ../conf/messages.en
echo "hallmarkCategories.heading = hallmarkCategories" >> ../conf/messages.en
echo "hallmarkCategories.option1 = Option 1" >> ../conf/messages.en
echo "hallmarkCategories.option2 = Option 2" >> ../conf/messages.en
echo "hallmarkCategories.checkYourAnswersLabel = hallmarkCategories" >> ../conf/messages.en
echo "hallmarkCategories.error.required = Select hallmarkCategories" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryHallmarkCategoriesUserAnswersEntry: Arbitrary[(HallmarkCategoriesPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[HallmarkCategoriesPage.type]";\
    print "        value <- arbitrary[HallmarkCategories].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryHallmarkCategoriesPage: Arbitrary[HallmarkCategoriesPage.type] =";\
    print "    Arbitrary(HallmarkCategoriesPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryHallmarkCategories: Arbitrary[HallmarkCategories] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(HallmarkCategories.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(HallmarkCategoriesPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def hallmarkCategories: Option[Row] = userAnswers.get(HallmarkCategoriesPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"hallmarkCategories.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(Html(answer.map(a => msg\"hallmarkCategories.$a\".resolve).mkString(\",<br>\"))),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.HallmarkCategoriesController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"hallmarkCategories.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration HallmarkCategories completed"
