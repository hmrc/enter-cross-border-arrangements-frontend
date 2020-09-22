#!/bin/bash

echo ""
echo "Applying migration MainBenefitProblem"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /mainBenefitProblem                       controllers.MainBenefitProblemController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "mainBenefitProblem.title = mainBenefitProblem" >> ../conf/messages.en
echo "mainBenefitProblem.heading = mainBenefitProblem" >> ../conf/messages.en

echo "Migration MainBenefitProblem completed"
