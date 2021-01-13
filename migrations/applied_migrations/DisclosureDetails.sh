#!/bin/bash

echo ""
echo "Applying migration DisclosureDetails"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /disclosureDetails                       controllers.DisclosureDetailsController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "disclosureDetails.title = disclosureDetails" >> ../conf/messages.en
echo "disclosureDetails.heading = disclosureDetails" >> ../conf/messages.en

echo "Migration DisclosureDetails completed"
