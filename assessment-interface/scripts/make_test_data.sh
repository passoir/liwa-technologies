#!/bin/bash -eu

#Usage:
# ./scripts/make_test_data.sh /mnt/info/archiv/liwa/first_10000.warc18.gz tmp_data
# ./scripts/make_test_data.sh /mnt/info/archiv/liwa/first_10000.warc18.gz tmp_data 1
#

WARC_GZ=$1

OUT_DIR=$2

if [ $# -ge 3 ] ; then
    ONLY_DERIVED=$3
else
    ONLY_DERIVED=0
fi

if [ "1" == "2" ] ; then :
fi

if [ "0" == "$ONLY_DERIVED" ] ; then :

    if [ -e $OUT_DIR ] ; then
	echo Directory of file '"'$OUT_DIR'"' exsits.
	exit 1
    fi

    mkdir -p $OUT_DIR
    
    for i in `zcat $WARC_GZ  | grep 'WARC-Target-URI:' | awk -v FS="http://" '{print $2}' | sed 's/;/%3B/g' | awk -v FS="\r" '{print $1}' | head -n10000` ; do 
	echo $i; 
	wget "http://uranus:8080/archive/*/$i" -O $OUT_DIR/tmp_warc -o $OUT_DIR/tmp_warc_out
	cat $OUT_DIR/tmp_warc  | grep "$i" | awk -v FS='"'  'NR>1{print $2}'
    done > $OUT_DIR/tmp_warc_all
    
    rm -r $OUT_DIR/tmp_warc $OUT_DIR/tmp_warc_out

fi

#cat $OUT_DIR/tmp_warc_all | grep archive  | awk -v FS="/" '{print $3,$4,$5}' | uniq -c | awk '$1>=2' > $OUT_DIR/min2.txt
#cat $OUT_DIR/tmp_warc_all | grep archive  | awk -v FS="/" '{print $3,$4,$5}' | uniq -c | awk '$1>=3' > $OUT_DIR/min3.txt
#cat $OUT_DIR/tmp_warc_all | grep archive  | awk -v FS="/" '{print $3,$4,$5}' | uniq -c | awk '$1>=4' > $OUT_DIR/min4.txt

cat $OUT_DIR/tmp_warc_all | grep "^/archive"  | awk -v FS="\r" '{print $1}' | awk -v FS="/" '{printf "%s",$3";"$4; for(i=5;i<=NF;++i)printf "%s","/" $i; print "" }' | sort | uniq > $OUT_DIR/url_time.txt

cat $OUT_DIR/url_time.txt | sort | uniq | awk -v FS=";" '{print $2}' | sort | uniq -c | sort -n | awk '$1>1 {print $1,$2}' > $OUT_DIR/multiple_url.csv



cat $OUT_DIR/url_time.txt | awk -v FS=";" '{n=split($2,a,"/"); host[ a[1] ]=$1; if(2==n && ""==a[2])main_page[a[1]]=$1;} END{for(i in main_page) print i";"i";" main_page[i]}' > $OUT_DIR/hosts.csv

cat $OUT_DIR/tmp_warc_all | grep archive  | awk -v FS="/" '{printf "%s",$4; for(i=5;i<=NF;++i)printf "%s","/" $i; print "" }' | sort | uniq -c  | sort -n | awk '$1>=2 && NF==2 {print $2}'  > $OUT_DIR/min2url.txt

zcat $WARC_GZ | grep 'WARC-Target-URI:' | awk -v FS="http://" '{print $2}' | grep -v ';' | awk -v FS="\r" '{print $1}' | awk -v OFS=";" 'BEGIN{print "host","url","time"} {print $1,$1,NR}' > $OUT_DIR/url.csv 

zcat $WARC_GZ | grep 'WARC-Target-URI:' | awk -v FS="http://" '{print $2}' | sed 's/;/%3B/g' | awk -v FS="\r" '{print $1}' | awk -v OFS=";" 'BEGIN{print "host","url","time"} {print $1,$1,NR}' > $OUT_DIR/url2.csv  

#bugos oldal: http://uranus:8080/archive/*/www.therookery.org.uk

cat $OUT_DIR/hosts.csv | awk -v FS=";" -v OFS=";" 'BEGIN{print "#MainURL" } 
{print $1}' > $OUT_DIR/host_table.csv


cat $OUT_DIR/host_table.csv | awk -v FS=";" 'BEGIN{ print "#HostID;PageURL"} NR>1{for(i=1;i<=10;++i)print $1";"$1}' > $OUT_DIR/host_pages_table.csv

#awk 'BEGIN{print mktime("2009 10 11 14 00 01")}'
#cat $OUT_DIR/a_history_table.csv  | awk -v FS=";" '{print strftime("%Y-%m-%d %H:%M:%S",$3)}'

cat $OUT_DIR/a_history_table.csv | awk -v FS=";" 'NR>=2{if(!($1"|"$2 in last_time)||last_time[$1"|"$2]<$3) {last_time[$1"|"$2]=$3; last_flag[$1"|"$2]=$5}} END{for(i in last_time) if(1!=last_flag[i]) print "ERROR:" ,i, last_time[i], last_flag[i];}'

