#/usr/bin/env python
import sys
import re
import os
import time
import md5

import config
#import Util
#import amqplib.client_0_8 as amqp
import random

script1 = 'URL="%s"\n\
OUT=$0\n\
TIMEOUT="1 minutes"\n\
MPLAYER="%s"\n\
#schedule a job to force kill this script in TIMEOUT time\n\
mv $0 $0.done\n\
echo "terminateProcess.sh sleep" | at now+$TIMEOUT\n\
if [ $? != 0 ];then echo "Failed to schedule forced termination job.";exit 1;fi\n\
$MPLAYER -vo null -ao null -identify -cache-min 0 -frames 0 $URL>$OUT.started 2>&1\n\
\n\
#sleep 1800\n\
\n\
mv $OUT.started $OUT.finished'

class Message:
    id = None
    msg = None
    def __init__(self,id,msg):
        self.id = id
        self.msg = msg


class GetJobs:
    dir = None
    extension = None
    doneExtension = ".done"

    def __init__(self,dir,extension):
        self.dir = dir
        self.extension = extension
    
    #return messageid,msg like x.xml,blabla
    def getMsg(self):
        print self.dir
        for root,dirs,files in os.walk(self.dir):
            print root,dirs,files
            for f in files:
                if f.endswith(self.extension):
                    msg = None
                    file = open("%s/%s"%(root,f))
                    msg = file.read()
                    file.close()
                    m = Message(f,msg)
                    return m
                else:
                    print "Comparing with",f
        return None
        
    def ackMsg(self,m):
        os.rename("%s/%s" %(self.dir,m.id),"%s/%s%s" % (self.dir,m.id,self.doneExtension))


        
class Job:
    jid = None
    seed = None
    prefix = None
    patchid = None

    seedre      = re.compile("<seed>(.*?)</seed>")
    prefixre    = re.compile("<arc-prefix>(.*?)</arc-prefix>")
    patchre     = re.compile("<patch-for>[0-9]+</patch-for>")
    
    def __init__(self,msg):
                        
        mobj = self.seedre.search(msg)
        if mobj is not None:
            self.seed = mobj.group(1)

        self.prefix = "EA-LiWA.streaming"
        mobj = self.prefixre.search(msg)
        if mobj is not None:            
            self.prefix = mobj.group(1)

        self.patchid = 0
        mobj = self.patchre.search(msg)
        if mobj is not None:            
            self.patchid = int(mobj.group(1))
               
        m = md5.new()            
        m.update(self.seed)
        self.jid = m.hexdigest()
        
def identifyUrl():
    #(conn,channel) = Util.makeConnection(config.AMQPhost,config.AMQPuserid,config.AMQPpassword,config.AMQPvirtualHost,config.AMQPexchange,config.AMQPinQueue)
    
    #msg = Util.readMessage(conn,channel,config.AMQPinQueue)
    
    url = "rtsp://a426.r.akareal.net/ondemand/7/426/21547/v001/video.twofour.co.uk/cabinet_office/no10_films/QueenandPM_bb.rm"
    #url = "rtsp://a426.r.akareal.net/ondemand/7/426/21547/v001/video.twofour.co.uk/cabinet_office/no10_films/QueenandPM_bb.rm"
    #mms://wm-r0.vitalstreamcdn.com/twofour_vitalstream_com/cabinet_office/no10_films/QueenandPM_bb.wmv
    #mms://wm-r0.vitalstreamcdn.com/twofour_vitalstream_com/cabinet_office/no10_films/QueenandPM_bb.wmv
    #url = "rtsp://rmv8.bbc.net.uk/radio4/0600_wed.ra?BBC-UID=24985c878c31fbddeb765567f070290c405ff15570103043fb5aa6fb357e2386&SSO2-UID="
    msg = createMessage(url)
    if msg is not None:
        #print "Message:",msg
        job = Job(msg)
        print "JobId is:",job.jid
        print "Seed is ",job.seed
        print "Prefix is ",job.prefix
        
        if job.seed is None:
            return
        script=config.script2 % (job.seed,config.toolLocation)

        filename = config.scriptsLocation+"/script-%s-%s.sh" %((random.randint(0,65535)),job.jid)
        f = open(filename,"w")
        f.write(script)
        f.close()
        print "Created file",filename
    #channel.close()
    #conn.close()

def main():
    print "Using maximum memory:%s Mb" % config.maximumMemory
    print "Using maximum bandwidth:%s Mb" % config.maximumBandwith
    gj = GetJobs("/1/liwa/software/streaming/jobs/",".job")
    while True:
        m = gj.getMsg()
        if m is not None:
            job = Job(m.msg)
            print "JobId is:",job.jid
            print "Seed is ",job.seed
            print "Prefix is ",job.prefix
        
            if job.seed is None:
                return
            script=config.script2 % (job.seed,config.toolLocation,job.prefix)

            filename = config.scriptsLocation+"/script-%s-%s.sh" %((random.randint(0,65535)),job.jid)
            f = open(filename,"w")
            f.write(script)
            f.close()
            print "Created file",filename
            gj.ackMsg(m)
        else:
            print "No job found." 
        time.sleep(5)
            
if __name__ == "__main__":
    main()
    #fillQueueWithMessages("rtsp://rmv8.bbc.net.uk/radio4/0600_wed.ra?BBC-UID=24985c878c31fbddeb765567f070290c405ff15570103043fb5aa6fb357e2386&SSO2-UID=", 4)
    #identifyUrl()
    """
    import time
    for i in xrange(4):
        time.sleep(10)
        import new
        code = open("config.py").read()
        module = new.module("test")
        exec code in module.__dict__
        print module.x
    """
