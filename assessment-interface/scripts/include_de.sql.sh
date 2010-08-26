#!/bin/bash


# usage: 
# sh ./include_de.sql.sh ../final_data/newlangdet/de_hostlangs.csv ../final_data/newlangdet/include_de.sql
input=$1
output=$2

echo -n "" > $output
while read line;
do

    url=$(echo $line | sed 's/\/;.*//')	
    
    echo "---" >> $output
    echo "update f_host_table set \"Language\"='de'" >> $output
    echo "where \"MainURL\"='${url}';" >> $output

done < $input
