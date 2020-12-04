#!/bin/bash

echo ""
echo "Applying migration IsAssociatedEnterpriseAffected"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /isAssociatedEnterpriseAffected                        controllers.IsAssociatedEnterpriseAffectedController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /isAssociatedEnterpriseAffected                        controllers.IsAssociatedEnterpriseAffectedController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeIsAssociatedEnterpriseAffected                  controllers.IsAssociatedEnterpriseAffectedController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeIsAssociatedEnterpriseAffected                  controllers.IsAssociatedEnterpriseAffectedController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "isAssociatedEnterpriseAffected.title = isAssociatedEnterpriseAffected" >> ../conf/messages.en
echo "isAssociatedEnterpriseAffected.heading = isAssociatedEnterpriseAffected" >> ../conf/messages.en
echo "isAssociatedEnterpriseAffected.checkYourAnswersLabel = isAssociatedEnterpriseAffected" >> ../conf/messages.en
echo "isAssociatedEnterpriseAffected.error.required = Select yes if isAssociatedEnterpriseAffected" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIsAssociatedEnterpriseAffectedUserAnswersEntry: Arbitrary[(IsAssociatedEnterpriseAffectedPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[IsAssociatedEnterpriseAffectedPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIsAssociatedEnterpriseAffectedPage: Arbitrary[IsAssociatedEnterpriseAffectedPage.type] =";\
    print "    Arbitrary(IsAssociatedEnterpriseAffectedPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(IsAssociatedEnterpriseAffectedPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def isAssociatedEnterpriseAffected: Option[Row] = userAnswers.get(IsAssociatedEnterpriseAffectedPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"isAssociatedEnterpriseAffected.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(yesOrNo(answer)),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.IsAssociatedEnterpriseAffectedController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"isAssociatedEnterpriseAffected.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration IsAssociatedEnterpriseAffected completed"
