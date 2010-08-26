import re
import sys
import amqplib.client_0_8 as amqp

def makeConnection(host,user,password,virtualHost,exchange,queue):
    conn = amqp.Connection(host, userid=user, password=password,virtual_host=virtualHost, ssl=False)
    ch = conn.channel(channel_id=1)
    ch.access_request('/data', active=True, read=True)

    ch.exchange_declare(exchange, 'direct',passive=True, auto_delete=False)
    qname, _, _ = ch.queue_declare(queue,passive=True,auto_delete=False)
    ch.queue_bind(queue, exchange,routing_key=queue)

    return (conn,ch)

def readMessage(connection,channel,queue):
    msg  = channel.basic_get(queue=queue)
    if msg is not None:
        print "Got:",msg.body
        channel.basic_ack(msg.delivery_tag)    
        return msg.body
    return None

def sendMessage(connection,channel,exchange,rkey,msg):
    channel.basic_publish(msg,exchange=exchange,routing_key=rkey)

def testAMQP():
    host        = "debug:8090"
    userid      = "liwa"
    password    = "liwa"
    virtualH    = "vhost1"
    exchange    = "exchange1"
    queue       = "streaming_in"
    (conn,channel) = makeConnection(host,userid,password,virtualH,exchange,queue)
    
    msg = readMessage(conn,channel,queue)
    if msg is not None:
        print "Message:",msg
    url = "rtsp://rmv8.bbc.net.uk/radio4/0600_wed.ra?BBC-UID=24985c878c31fbddeb765567f070290c405ff15570103043fb5aa6fb357e2386&SSO2-UID="
    msg = amqp.Message(content_type="text/plain",content_encoding="utf-8",delivery_mode=2,body = "mesaj:"+url)
    sendMessage(conn,channel,exchange,queue,msg)
    
    channel.close()
    conn.close()

def makeCommand(url):
    return '/1/liwa/streaming/MPlayer/mplayer -vo null -ao null -identify -cache-min 0 -frames 0 "%s"' % url

def identifyStream(output):
    bite_rate = re.compile(r"^ID_LENGTH=([0-9]+[.][0-9]+).", re.S|re.I)



#mplayer -vo null -ao null -identify -cache-min 0 -frames 0 "rtsp://rmv8.bbc.net.uk/radio4/0600_wed.ra?BBC-UID=24985c878c31fbddeb765567f070290c405ff15570103043fb5aa6fb357e2386&SSO2-UID="
#mplayer -really-quiet -dumpstream -dumpfile "a.out"  "rtsp://rmv8.bbc.net.uk/radio4/0600_wed.ra?BBC-UID=24985c878c31fbddeb765567f070290c405ff15570103043fb5aa6fb357e2386&SSO2-UID="
if __name__ == '__main__':
    testAMQP()
    print "main"
