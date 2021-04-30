#!/bin/bash

echo ""
echo "Applying migration Summary"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /summary                       controllers.SummaryController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "summary.title = summary" >> ../conf/messages.en
echo "summary.heading = summary" >> ../conf/messages.en

echo "Migration Summary completed"
