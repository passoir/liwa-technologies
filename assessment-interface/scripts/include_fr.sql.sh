#!/bin/bash

# usage:
# sh ./include_fr.sql.sh ../final_data/newlangdet/fr_hostlangs.csv ../final_data/newlangdet/include_fr.sql
input=$1
output=$2

echo -n "" > $output
while read line;
do

    url=$(echo $line | sed 's/\/;.*//')	
    
    echo "---" >> $output
    echo "update f_host_table set \"Language\"='fr'" >> $output
    echo "where \"MainURL\"='${url}';" >> $output

done < $input
