#!/bin/bash

JAVA=java
LIBS=`ls ./AssessmentUserInterface/WebContent//../lib/*.jar | awk 'NR==1{printf $1} NR>=2{printf":"$i}'`:tmp_build/liwa/WEB-INF/classes/


echo "Running : LoadHostTable ... "
$JAVA -cp $LIBS de.l3s.liwa.assessment.db.LoadLiwaHostTable liwa f_ final_data/host_table.csv
