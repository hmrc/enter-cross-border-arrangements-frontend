#!/bin/bash

echo ""
echo "Applying migration IntermediaryRole"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /intermediaryRole                        controllers.IntermediaryRoleController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /intermediaryRole                        controllers.IntermediaryRoleController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeIntermediaryRole                  controllers.IntermediaryRoleController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeIntermediaryRole                  controllers.IntermediaryRoleController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "intermediaryRole.title = As an intermediary, what i your role in this arrangement?" >> ../conf/messages.en
echo "intermediaryRole.heading = As an intermediary, what i your role in this arrangement?" >> ../conf/messages.en
echo "intermediaryRole.promoter = Promter" >> ../conf/messages.en
echo "intermediaryRole.serviceProvider = Service provider" >> ../conf/messages.en
echo "intermediaryRole.checkYourAnswersLabel = As an intermediary, what i your role in this arrangement?" >> ../conf/messages.en
echo "intermediaryRole.error.required = Select intermediaryRole" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIntermediaryRoleUserAnswersEntry: Arbitrary[(IntermediaryRolePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[IntermediaryRolePage.type]";\
    print "        value <- arbitrary[IntermediaryRole].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIntermediaryRolePage: Arbitrary[IntermediaryRolePage.type] =";\
    print "    Arbitrary(IntermediaryRolePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIntermediaryRole: Arbitrary[IntermediaryRole] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(IntermediaryRole.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(IntermediaryRolePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def intermediaryRole: Option[Row] = userAnswers.get(IntermediaryRolePage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"intermediaryRole.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(msg\"intermediaryRole.$answer\"),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.IntermediaryRoleController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"intermediaryRole.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration IntermediaryRole completed"
