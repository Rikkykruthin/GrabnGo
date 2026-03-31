#!/bin/bash

echo "Building Food Ordering App Backend..."
cd backend
mvn clean package
echo "WAR file created at: backend/target/food-ordering-app.war"
echo "Deploy this to your Tomcat webapps directory"
