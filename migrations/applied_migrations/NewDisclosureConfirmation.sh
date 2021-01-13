#!/bin/bash

echo ""
echo "Applying migration NewDisclosureConfirmation"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /newDisclosureConfirmation                       controllers.NewDisclosureConfirmationController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "disclosureConfirmation.title = newDisclosureConfirmation" >> ../conf/messages.en
echo "disclosureConfirmation.heading = newDisclosureConfirmation" >> ../conf/messages.en

echo "Migration NewDisclosureConfirmation completed"
