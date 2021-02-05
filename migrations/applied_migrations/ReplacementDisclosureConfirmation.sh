#!/bin/bash

echo ""
echo "Applying migration ReplacementDisclosureConfirmation"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /replacementDisclosureConfirmation                       controllers.ReplacementDisclosureConfirmationController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "replacementDisclosureConfirmation.title = replacementDisclosureConfirmation" >> ../conf/messages.en
echo "replacementDisclosureConfirmation.heading = replacementDisclosureConfirmation" >> ../conf/messages.en

echo "Migration ReplacementDisclosureConfirmation completed"
