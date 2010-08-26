#!/bin/bash

JAVA=java
LIBS=`ls ./AssessmentUserInterface/WebContent//../lib/*.jar | awk 'NR==1{printf $1} NR>=2{printf":"$i}'`:tmp_build/liwa/WEB-INF/classes/

echo "Testing : TestCsvReader ... "
$JAVA -cp $LIBS de.l3s.liwa.assessment.TestCsvReader 

echo "Testing : TestParseXml ... "
$JAVA -cp $LIBS de.l3s.liwa.assessment.TestParseXml 

echo "Testing : TestConnectToDb ... "
$JAVA -cp $LIBS de.l3s.liwa.assessment.db.TestConnectToDb 

echo "Testing : TestLoadCsv ... "
$JAVA -cp $LIBS de.l3s.liwa.assessment.db.TestLoadCsv

# this is not a test, this is needed to be able to test codes that rely on a database
echo "Running : LoadLiwaTables ... "
$JAVA -cp $LIBS de.l3s.liwa.assessment.db.LoadLiwaTables liwa test_ tmp_data

echo "Testing : TestSnapshotManager ... "
$JAVA -cp $LIBS de.l3s.liwa.assessment.TestSnapshotManager

echo "Testing : TestDatasources ... "
$JAVA -cp $LIBS de.l3s.liwa.assessment.TestDataSources

# this is not a test, this is needed to be able to test codes that rely on a database
# run for the second time to update the requirement rules
echo "Running : LoadLiwaTables ... "
#$JAVA -cp $LIBS de.l3s.liwa.assessment.db.LoadLiwaTables liwa test_ tmp_data



