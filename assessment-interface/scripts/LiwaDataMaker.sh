#!/bin/bash

# LiwaDataMaker.sh

# usage: ./LiwaDataMaker DB_EXPORT.csv 30000_randoms.txt > newdata.csv

# example:
# sh ./LiwaDataMaker.sh ../../../F_ASSESSMENT_HISTORY_TABLE_7600.csv 30000_randoms.txt > nogit/F_ASSESSMENT_HISTORY_TABLE_rand_7600.csv

INPUT=$1
RANDOMFILE=$2

ROWNUM=`cat $INPUT | wc -l`

# using ' instead of "" within comments, and an extra space at the end
cat $INPUT | awk -v FS="\",\"" '{print $4}' | sed 's/""/'\''/g' |  awk '{print "\""$0" \""}' > ${INPUT}_comments.tmp

# splitting data : before-comment columns 
cat $INPUT | awk -v FS="\",\"" 'BEGIN { OFS="\",\"" } {print $1,$2,$3"\""}' > ${INPUT}_firstpart.tmp

# splitting data : after-comment columns 
cat $INPUT |  awk -v FS="\",\"" 'BEGIN { OFS="\",\"" } {print "\""$5,$6,$7,$8,$9,$10,$11,$12,$13,$14,$15,$16,$17,$18,$19,$20,$21,$22,$23}' > ${INPUT}_secondpart.tmp


# generating last column: random numbers
cat 30000_randoms.txt | head -n $ROWNUM | awk '{print "\""$0"\""}' > tmp_rand_numbers_quoted.txt

# pasting it together
#paste -d, $INPUT tmp_rand_numbers_quoted.txt | sed 's/0\.189865386187197/Random/' | sed -e '$d' | awk -v FS="\",\"" '{print $4}' | sed 's/""/'\''/g'

paste -d, ${INPUT}_firstpart.tmp ${INPUT}_comments.tmp ${INPUT}_secondpart.tmp tmp_rand_numbers_quoted.txt | sed 's/0\.189865386187197/Random/' | sed -e '$d' 