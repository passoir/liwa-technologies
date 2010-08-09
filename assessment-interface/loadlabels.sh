#!/bin/bash

JAVA=java
LIBS=`ls ./AssessmentUserInterface/WebContent//../lib/*.jar | awk 'NR==1{printf $1} NR>=2{printf":"$i}'`:tmp_build/liwa/WEB-INF/classes/


echo "Running : LoadLabels ... "
#$JAVA -cp $LIBS de.l3s.liwa.assessment.db.LoadLabels liwa f_ final_data/table_export_u27_29.csv
$JAVA -cp $LIBS de.l3s.liwa.assessment.db.LoadLabels liwa f_ final_data/data_27_29_allbutlast10.csv
