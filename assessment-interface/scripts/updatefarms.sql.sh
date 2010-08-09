#!/bin/bash

input=$1
output=$2

echo -n "" > $output
while read line;
do

    urlending=$(echo $line)	
    
    echo "---" >> $output
    echo "update f_host_table set \"Language\"='farm'" >> $output
    echo "where \"MainURL\" like '%${urlending}';" >> $output

done < $input
