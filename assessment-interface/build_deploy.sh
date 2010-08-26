#!/bin/bash -xeu

# This script builds and deploys the LIWA project. Compiles java files and copies everything inside apache-tomcat to make it work.

sh build.sh

sh deploy.sh

