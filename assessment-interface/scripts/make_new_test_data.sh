#!/bin/bash -eu

#Usage:
# ./scripts/make_new_test_data.sh tmp_data


OUT_DIR=$1

echo '#UserID;HostURL;Date;Comment;IsLast;Language;HostingType;Adult;OtherProblem;Spam;News;Commercial;Educational;Discussion;Media;Database;Readability-Vis;Readability-Lang;Trustiness;Neutrality;Biased' > $OUT_DIR/a_history_table.csv
echo '1;http://snow.besttrick.com;1255262601;comm;0;french;normal;yes;no;no;yes;no;no;no;no;no;3;2;2;1;1' >> $OUT_DIR/a_history_table.csv
echo '1;http://snow.besttrick.com;1255272601;;1;english;normal;yes;yes;no;no;no;yes;no;no;yes;1;2;1;2;1' >> $OUT_DIR/a_history_table.csv
echo '1;http://snowbroader.eu;1255332901;;0;no-text;normal;yes;yes;no;yes;no;no;no;yes;yes;3;2;2;1;1' >> $OUT_DIR/a_history_table.csv
echo '1;http://snowbroader.eu;1255338901;;1;german;normal;yes;yes;yes;yes;yes;no;yes;yes;yes;3;2;2;1;1' >> $OUT_DIR/a_history_table.csv
echo '2;http://snow.besttrick.com;1255262601;comment;0;english;normal;yes;yes;no;yes;yes;no;no;no;no;3;2;1;1;2' >> $OUT_DIR/a_history_table.csv
echo '2;http://snow.besttrick.com;1255282601;comm;1;english;normal;yes;yes;no;yes;no;no;no;yes;yes;3;2;2;1;2' >> $OUT_DIR/a_history_table.csv
echo '2;http://snowbroader.eu;1255338901;;1;german;normal;yes;yes;no;yes;no;no;no;yes;no;3;2;2;1;1' >> $OUT_DIR/a_history_table.csv
echo '3;http://snowbroader.eu;1255332901;comm;0;english;normal;yes;yes;no;yes;no;no;no;no;yes;3;2;2;1;2' >> $OUT_DIR/a_history_table.csv
echo '3;http://snowbroader.eu;1255338901;;1;english;normal;yes;yes;no;yes;no;no;no;no;no;3;2;2;1;1' >> $OUT_DIR/a_history_table.csv
echo '4;http://snow.besttrick.com;1255262401;;0;english;normal;yes;yes;no;yes;no;no;no;no;no;2;3;2;2;1' >> $OUT_DIR/a_history_table.csv
echo '4;http://snow.besttrick.com;1255262601;;0;english;normal;yes;yes;no;no;no;no;no;no;yes;3;2;2;1;1' >> $OUT_DIR/a_history_table.csv
echo '4;http://snow.besttrick.com;1255292601;;1;english;normal;yes;yes;no;no;no;no;no;no;no;1;2;2;1;1' >> $OUT_DIR/a_history_table.csv
echo '4;http://snowbroader.eu;1255332901;;0;other;normal;yes;yes;no;yes;yes;no;no;yes;no;3;2;2;1;1' >> $OUT_DIR/a_history_table.csv
echo '4;http://snowbroader.eu;1255338901;commtz;1;dontknow-lang;normal;no;no;no;yes;no;no;no;no;no;3;1;2;2;1' >> $OUT_DIR/a_history_table.csv


echo '#FromHostURL;ToHostURL' > $OUT_DIR/host_link_table.csv
echo 'http://snowboarding.transworld.net;http://snow.besttrick.com' >> $OUT_DIR/host_link_table.csv
echo 'http://snowboarding.transworld.net;https://perfectmoney.com' >> $OUT_DIR/host_link_table.csv
echo 'http://snowboarding.transworld.net;http://soundcloud.com' >> $OUT_DIR/host_link_table.csv
echo 'http://snowbroader.eu;http://snow.besttrick.com' >> $OUT_DIR/host_link_table.csv
echo 'http://snowbroader.eu;https://perfectmoney.com' >> $OUT_DIR/host_link_table.csv
echo 'http://snowbroader.eu;http://soundcloud.com' >> $OUT_DIR/host_link_table.csv
echo 'http://snow.besttrick.com;tentop.co.uk' >> $OUT_DIR/host_link_table.csv
echo 'http://snow.besttrick.com;https://perfectmoney.com' >> $OUT_DIR/host_link_table.csv
echo 'http://snow.besttrick.com;http://soundcloud.com' >> $OUT_DIR/host_link_table.csv

echo '#UserID;UserName;Password;Language' > $OUT_DIR/user_table.csv
echo '1;a;x1;English' >> $OUT_DIR/user_table.csv
echo '2;b;x2;English' >> $OUT_DIR/user_table.csv
echo '3;c;x3;French' >> $OUT_DIR/user_table.csv
echo '4;d;x4;German' >> $OUT_DIR/user_table.csv
echo '5;e;x5;English' >> $OUT_DIR/user_table.csv


cat $OUT_DIR/host_table.csv | awk -v FS=";" 'BEGIN{ print "#HostID;PageURL"} NR>1{for(i=1;i<=10;++i)print $1";"$1}' > $OUT_DIR/host_pages_table.csv

