#!/bin/bash -xeu

# This script builds the LIWA project. Compiles java files into the tmp_build directory

# include config.sh or default_config.sh
if [ -f ./config.sh ]; then
    echo "Reading from 'config.sh'"
    . ./config.sh
else
    echo "Warning: you have no 'config.sh' file. Using 'default_config.sh'. "
    . ./default_config.sh
fi

# delete temporary build dir
rm -rf $TMP_BUILD_DIR
# re-create
mkdir $TMP_BUILD_DIR
# copy sources to temp dir
cp -r $APP_WEB_DIR/ $TMP_BUILD_DIR/$APP_NAME
# create target directory for the classes
mkdir $TMP_BUILD_DIR/$APP_NAME/WEB-INF/classes
# compile java files
# TODO: add to classpath external jars (oracle's for example)
javac -cp `ls ./AssessmentUserInterface/WebContent/../lib/*.jar | awk 'NR==1{printf $1} NR>=2{printf":"$i}'` `find $APP_SRC_DIR/ | grep java$` `find $APP_SRC_DIR/../test | grep java$` -d $TMP_BUILD_DIR/$APP_NAME/WEB-INF/classes

# copy config xml files to WEB-INF/classes so that the webapp can read it
cp scripts/sample_config.xml scripts/sample_config2.xml $TMP_BUILD_DIR/$APP_NAME/WEB-INF/classes/
cp scripts/sample_config.xml scripts/main_config.xml $TMP_BUILD_DIR/$APP_NAME/WEB-INF/classes/
cp AssessmentUserInterface/test/test_sample_config.xml $TMP_BUILD_DIR/$APP_NAME/WEB-INF/classes/test_sample_config.xml
cp AssessmentUserInterface/test/test_database_config.xml $TMP_BUILD_DIR/$APP_NAME/WEB-INF/classes/test_database_config.xml

# create bundle dir
mkdir $TMP_BUILD_DIR/$APP_NAME/WEB-INF/classes/de/l3s/liwa/assessment/bundle

# generate javascript
LIBS=`ls ./AssessmentUserInterface/WebContent/../lib/*.jar | awk 'NR==1{printf $1} NR>=2{printf":"$i}'`:tmp_build/liwa/WEB-INF/classes/
java -cp $LIBS de.l3s.liwa.assessment.MakeJavascript

# copy javascript
cp ./AssessmentUserInterface/WebContent/pages/formhandler.jsp $TMP_BUILD_DIR/liwa/pages/formhandler.jsp

echo "Done."

exit

