#Maximum memory available to streaming tool, in megabytes
maximumMemory = "20"

#Maximum bandwith available to streaming tool, im megabytes per second
maximumBandwith = "5"

#mplayer location
toolLocation = "/1/liwa/mplayerinstall/bin/mplayer"
toolIdentifyArguments = "-vo null -ao null -identify -cache-min 0 -frames 0"
toolDownloadArguments = ""

#output file location
downloadLocation = "/1/liwa/software/streaming/download/"
scriptsLocation = "/1/liwa/software/streaming/download/scripts"




#AMQP parameters
AMQPhost                = "debug:8090"
AMQPuserid              = "liwa"
AMQPpassword            = "liwa"
AMQPvirtualHost         = "vhost1"
AMQPexchange            = "exchange1"
AMQPinQueue             = "streaming_in"
AMQPtreatingQueue       = "streaming_treating"

script2=r"""
URL="%s"
OUT=$0
DEFAULT_DOWNLOAD_TIMEOUT="60"
DEFAULT_IDENTIFY_TIMEOUT="1"
MPLAYER="%s"
#echo "our pid is $$"
mv $0 $0.started
#schedule a job to force kill this script in TIMEOUT time
$MPLAYER -vo null -ao null -identify -cache-min 0 -frames 0 $URL>$OUT.identify 2>&1 &
PID=$!
#echo "Created identify process with pid $PID"
TIMEOUT="$DEFAULT_IDENTIFY_TIMEOUT minutes"
echo "kill -9 $PID" | at now+$TIMEOUT > /dev/null
wait $PID
mv $OUT.identify $OUT.identified
mime=`sed -n '/^Stream\ mimetype:/ s/Stream\ mimetype:\ //p' $OUT.identified`
#get the length as an int, it is printed as float like 63.00
length=`sed -n '/^ID_LENGTH=/ s/ID_LENGTH=\([0-9]*\)[.][0-9]*/\1/p' $OUT.identified`
host=`sed -n '/^Connecting\ to\ server/ s/Connecting\ to\ server.*\[\([0-9.]*\)\].*/\1/p' $OUT.identified`
#echo "host=$host mime=$mime l=$length"
stopafter=$DEFAULT_DOWNLOAD_TIMEOUT
if [ "$length" != "0" ];then
    stopafter=`expr $length / 60 + 1`
fi
TIMEOUT="$stopafter minutes"
echo "$TIMEOUT"
#TIMEOUT="2 minutes"
#echo "Stopping this job after $TIMEOUT"
date=`date +"%%Y%%m%%d%%H%%M%%S"`
$MPLAYER -dumpstream -dumpfile $OUT.dump $URL >/dev/null 2>&1 &
PID=$!
#echo "Created download process with pid $PID"
echo "kill -9 $PID" | at now+$TIMEOUT > /dev/null
wait $PID
#echo "Done"
#./arcwriter.py -f $OUT.dump -a "%s" -d $date -i $host -u "$URL" -m $mime
mv $0.started $0.done
#now  make an arc
"""


job_skeleton = "<job>"\
"    <client>%s</client>\n"\
"    <command>%s</command>\n"\
"    <crawl-id>%d</crawl-id>\n"\
"    <patch-for>%d</patch-for>\n"\
"    <seeds>\n"\
"        <seed>%s</seed>\n"\
"    </seeds>\n"\
"    <scope>%s</scope>\n"\
"    <arc-prefix>%s</arc-prefix>\n"\
"    <politeness>2</politeness>\n"\
"    <follow-robots>false</follow-robots>\n"\
"</job>"
"""
used for debug purposes, fills the queue with jobs
"""
def createMessage(url,client="LiWA",crawlid=0,orderid=0,prefix="EA-streaming"):
    skel = job_skeleton % (client,"start",crawlid,orderid,url,"path",prefix)
    return skel
"""        	
def fillQueueWithMessages(url,count):
    skel = createMessage(url)
    (conn,channel) = Util.makeConnection(config.AMQPhost,config.AMQPuserid,config.AMQPpassword,config.AMQPvirtualHost,config.AMQPexchange,config.AMQPinQueue)
    """
    msg = readMessage(conn,channel,queue)
    if msg is not None:
    print "Message:",msg
    url = "rtsp://rmv8.bbc.net.uk/radio4/0600_wed.ra?BBC-UID=24985c878c31fbddeb765567f070290c405ff15570103043fb5aa6fb357e2386&SSO2-UID="
    """
    for i in range(0,count):
        msg = amqp.Message(content_type="text/plain",content_encoding="utf-8",delivery_mode=2,body = skel)
        Util.sendMessage(conn,channel,config.AMQPexchange,config.AMQPinQueue,msg)
        print "Published message:%d" % i
    channel.close()
    conn.close()
"""
