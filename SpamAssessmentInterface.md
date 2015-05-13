# Introduction #

The Assessment Interface is a Web service provided to users (assessors) to enable assessment of the crawled Web hosts. Human assessment is important in order to gather annotated data about Web hosts (the gathered features will help us to decide
whether or not the host is worth being archived).

Web Content Quality has various and often subjective aspects.
Some characteristics that may affect the quality of a Web page or host:
  * the genre of the Web site (editorial, news, commercial, educational, or Web spam and more)
  * readability
  * authoritativeness
  * trustworthiness
  * neutrality

By default the Interface is configured to let the users decide on these specific aspects, but these can be changed.
In general, we tried to write a flexible code, so many features can be modified via an xml configuration file, without re-writing and re-compiling the java code (but the
servlet has to be re-started on any change).


# How to set up the underlying servers #

## Servlet container ##

We tested the application with Apache Tomcat (Version 6.0.20). Help to set it up:

http://tomcat.apache.org/tomcat-6.0-doc/setup.html

Our script, "deploy.sh" has to be modified to copy the appropriate files into your installed Tomcat's "webapps" directory.

## Database ##

The interface needs a database to get the hosts' data from and to store the assessments to. The current release works with an Oracle database, but a new version using PostgeSQL is being developed.

# How to start / restart / stop the service #

You can use Tomcat's manager, or stop and start Apache Tomcat:

```
[apache-tomcat-dir]/bin/shutdown.sh
[apache-tomcat-dir]/bin/startup.sh
```

# How to configure the Assessment Interface #

All the configuration variables are in the "scripts/main\_config.xml" file.
You have to describe your database connection, the Wayback Browser's address (where the actual pages come from), and the required attributes (aspects of assessment, with the possible values).