#!/bin/bash

echo ""
echo "Applying migration YourDisclosureHasBeenDeleted"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /yourDisclosureHasBeenDeleted                       controllers.disclosure.YourDisclosureHasBeenDeletedController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "yourDisclosureHasBeenDeleted.title = yourDisclosureHasBeenDeleted" >> ../conf/messages.en
echo "yourDisclosureHasBeenDeleted.heading = yourDisclosureHasBeenDeleted" >> ../conf/messages.en

echo "Migration YourDisclosureHasBeenDeleted completed"
