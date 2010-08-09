#!/bin/bash -xeu

# This script deploys the LIWA project. Copies everything inside apache-tomcat to make it work.

# include config.sh or default_config.sh
if [ -f ./config.sh ]; then
    echo "Reading from 'config.sh'"
    . ./config.sh
else
    echo "Warning: you have no 'config.sh' file. Using 'default_config.sh'. "
    . ./default_config.sh
fi

# copy properties file
cp $APP_SRC_DIR/de/l3s/liwa/assessment/bundle/messages.properties  $TMP_BUILD_DIR/$APP_NAME/WEB-INF/classes/de/l3s/liwa/assessment/bundle/

set +e
cp -r $TMP_BUILD_DIR/$APP_NAME $TOMCAT_DIR/webapps/
set -e

cp $APP_WEB_DIR/../lib/client.jar $TOMCAT_DIR/webapps/$APP_NAME/WEB-INF/lib/

cp $APP_WEB_DIR/../lib/junit.jar $TOMCAT_DIR/webapps/$APP_NAME/WEB-INF/lib/

cp $APP_WEB_DIR/../lib/servlet-api.jar $TOMCAT_DIR/webapps/$APP_NAME/WEB-INF/lib/

echo "Done."

exit

