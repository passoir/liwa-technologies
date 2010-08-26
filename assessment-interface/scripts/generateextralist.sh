old:
cat final_data/12000-with-random1.txt-singlelabel-en-interesting.noblogs | awk -v FS=";" -v OFS=";" '{user1=0; user2=0; if ($25<0.25) {user1=12; user2=6 } else if ($25<0.5) {user1=6; user2=8} else if ($25<0.75) {user1=8;user2=14} else {user1=14;user2=12} ; print user1,"http://"$1,0 ; print user2,"http://"$1,0}' >final_data/extralist_double_noblogs.csv

better:
cat final_data/12000-with-random1.txt-singlelabel-en-interesting.noblogs | awk -v FS=";" -v OFS=";" '{user=$2; user2=0; if (user==6 || user==13) {user2=14 } else if (user==12) {user2=8} else if (user==14 || user==8) {user2=6} else {user2=12} ; print user2,"http://"$1,0 }' >final_data/extralist_noblogs_new.csv

 cat final_data/12000-with-random1.txt-singlelabel-en-interesting.blogs | awk -v FS=";" -v OFS=";" '{user=$2; user2=0; if (user==8 && $25<0.68) {user2=14} else if (user==14) {user2=12} else if (user==6 && $25<0.88) {user2=8} else {user2=6} ; print user2,"http://"$1,0 }' >final_data/extralist_blogs_new.csv

cat final_data/extralist_noblogs_new.csv final_data/extralist_blogs_new.csv > final_data/extralist_all_new.csv

