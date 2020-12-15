#!/bin/bash

echo ""
echo "Applying migration AssociatedEnterprisesType"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /associatedEnterprisesType                        controllers.AssociatedEnterprisesTypeController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /associatedEnterprisesType                        controllers.AssociatedEnterprisesTypeController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeAssociatedEnterprisesType                  controllers.AssociatedEnterprisesTypeController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeAssociatedEnterprisesType                  controllers.AssociatedEnterprisesTypeController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "associatedEnterprisesType.title = associatedEnterprisesType" >> ../conf/messages.en
echo "associatedEnterprisesType.heading = associatedEnterprisesType" >> ../conf/messages.en
echo "associatedEnterprisesType.checkYourAnswersLabel = associatedEnterprisesType" >> ../conf/messages.en
echo "associatedEnterprisesType.error.required = Select yes if associatedEnterprisesType" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAssociatedEnterprisesTypeUserAnswersEntry: Arbitrary[(AssociatedEnterprisesTypePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[AssociatedEnterprisesTypePage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAssociatedEnterprisesTypePage: Arbitrary[AssociatedEnterprisesTypePage.type] =";\
    print "    Arbitrary(AssociatedEnterprisesTypePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(AssociatedEnterprisesTypePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def associatedEnterprisesType: Option[Row] = userAnswers.get(AssociatedEnterprisesTypePage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"associatedEnterprisesType.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(yesOrNo(answer)),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.AssociatedEnterprisesTypeController.onPageLoad(CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"associatedEnterprisesType.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration AssociatedEnterprisesType completed"
