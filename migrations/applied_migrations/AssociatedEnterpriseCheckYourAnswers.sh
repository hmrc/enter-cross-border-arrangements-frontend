#!/bin/bash

echo ""
echo "Applying migration AssociatedEnterpriseCheckYourAnswers"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /associatedEnterpriseCheckYourAnswers                       controllers.AssociatedEnterpriseCheckYourAnswersController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "associatedEnterpriseCheckYourAnswers.title = associatedEnterpriseCheckYourAnswers" >> ../conf/messages.en
echo "associatedEnterpriseCheckYourAnswers.heading = associatedEnterpriseCheckYourAnswers" >> ../conf/messages.en

echo "Migration AssociatedEnterpriseCheckYourAnswers completed"
