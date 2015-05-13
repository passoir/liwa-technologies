# Introduction #

There are many tools, usually called _streaming media recorders_, allowing to record streaming audio and video content from the Internet. Most of them are commercial software, especially running on Microsoft Windows and few of them are really able to capture all kind of streams.The **Rich Media Capture** module (RMC), developed in LiWA, is designed to enhance the capturing capabilities of the crawler, with regards to different multimedia content types. The current version of Heritrix is mainly based on the HTTP/HTTPS protocols and it cannot treat other content transfer protocols widely used for the multimedia content, such as streaming.

# General Presentation #

The principle of the RMC module is to delegate the multimedia content retrieval to an external application (MPlayer or FLVStreamer) which is able to handle a larger spectrum of transfer protocols, such as real time protocols, widely used for video streaming.

The module is constructed as an external plugin for Heritrix. Using this approach, the identification and retrieval of streams is completely decoupled, allowing the use of more efficient tools to analyze video and audio content. At the same time, using the external tools helps in reducing the burden on the crawling process. The module is composed of several subcomponents that communicate through messages. We use an open standard communication protocol called _Advanced Message Queuing Protocol_ (AMQP).

![http://wiki.liwa-technologies.googlecode.com/hg/RichMediaCapture-architecture.jpg](http://wiki.liwa-technologies.googlecode.com/hg/RichMediaCapture-architecture.jpg)

The integration of the RMC module with Heritrix is shown in the figure above and the workflow of the messages can be summarized as follows:

  * the **plugin** connected to Heritrix detects the URLs referencing streaming resources and it constructs for each one of them an _AMQP message_;

  * this message is passed to a central **Messaging Server**. The role of the Messaging Server is to decouple the Heritrix crawler from the clustered streaming downloaders (i.e. the external capturing tools);

  * the messaging server stores the URLs in queues and when one of the streaming downloaders is available, it sends the next URL for processing. The downloading tools used are represented by the **MPlayer** and the **FLVStreamer**, grouped in external clusters.

The current version of the technology added a new transfer protocol (RTMP) to the list of real time protocols supported by the RMC module: RTSP, MMS and RTMP. An update of the Heritrix plugin was done accordingly, to be able to detect a broader list of URIs, now including those using RTMP.

# How to activate the plugin in Heritrix #

The plugin for Heritrix is written in Java and it represents an implementation of the "deciderules" external interface:

```
org.archive.crawler.deciderules.ExternalImplInterface
```

In order to use the plugin with Heritrix, there are 2 steps to be done:

  * modify the configuration file of the crawler:

```
conf/heritrix.properties
```

The property org.archive.net.UURIFactory.ignored-schemes should exclude streaming schemes, therefore the RTSP, MMS and RTMP schemes should be removed from the list of ignored schemes. Nevertheless, they should be added to the list of accepted schemes, like for instance:

```
org.archive.net.UURIFactory.schemes = http,https,dns,rtsp,mms,rtmp
```

  * add the plugin to the list of pre-fetch processors:

A new ExternalImplDecideRule should be added to the crawl order and the name of the implementation class should be org.europarchive.StreamingURI. The decision setting for this deciderule should be REJECT.

# How to configure and monitor the messaging server #

Based on the standard messaging protocol AMPQ (Advanced Message Queueing Protocol), the messaging system that we use is an open source platform called RabbitMQ (http://rabbitmq.com). The messaging server was installed and properly configured on the test bed machine, using several methods provided by the system's tool for user management (rabbitmqctl). We created a generic "liwa" user for handling the queues of video URLs to be downloaded. An important configuration aspect is represented by the permissions that have to be associated between the user and the virtual host dedicated for external connections:

```
rabbitmqctl add_user liwa liwa

rabbitmqctl set_permissions \-p vhost-liwa liwa ".*" ".*" ".*"
```

The messaging server creates a virtual host dedicated to the "liwa" user and the messages are organised by specific queues, based on the type of resources to download: jobs\_rtsp, jobs\_mms, jobs\_rtmp. The status of the queues can be checked on the test bed, using the system's API, e.g.:

```
rabbitmqctl list_queues \-p vhost-liwa
```

The downloading tools connect to the specific queues, download the video files sequentially and pack them into arc files.