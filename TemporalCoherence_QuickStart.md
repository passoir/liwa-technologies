# Running on Linux #

Download the software from the repository:
```
$ hg clone https://liwa-technologies.googlecode.com/hg/ liwa-technologies
```

**Note:** If you get an error like: "abort: HTTP Error 404: Not Found", it might be a configuration problem for https.
> Try instead http:
```
$ hg clone http://liwa-technologies.googlecode.com/hg/ liwa-technologies
```

Change to the downloaded directory:
```
$ cd liwa-technologies/temporal-coherence/Heritrix3
```

Define the classpath of Heritrix3 with the libraries to run the software:
```
$ CLASSPATH=`echo $(ls lib/*.jar)":heritrix-3.0.0/bin" | sed -e 's/ /:/g'`
```

Start Heritrix3 with the temporal coherence module:
```
$ java -Xmx512M -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled -XX:+CMSPermGenSweepingEnabled \ 
-classpath $CLASSPATH org.liwa.coherence.HeritrixLauncher \
-a password -p 8843 >& heritrix.log                                     
```

**Note:** You might need to check first if the port is not used by another application:
```
$ netstat -l | grep 8443
```


This starts the Heritrix crawler, and starts the Web interface to it. One
may access the interface with a favorite browser at
http://localhost:8443/engine

# Configuring a Job #

The Heritrix3 with coherence module is ready to start crawl jobs. The
Heritrix crawler with the coherence module is a extensively configurable
software: every crawl job comes with a configuration instructing Heritrix
how crawling will be performed. Creating a configuration involves two
steps: (i) creating a job from the Web interface with a help of a Web
browser, and (ii) changing the parameters of the configuration by editing
the configuration file.

## Create a Job from the Web Interface ##

To create a job open the Web interface to Heritrix3 at

https://localhost:8443/engine

with your favorite Web browser. The Web page lists all the jobs and
configuration profiles available. The _selective_ profile is the default
configuration profile for the temporal coherence module and will be used to
create new jobs. Make a job based on this profile in the following way: (i)
click on the _selective_ profile (opens its Web page) and (ii) enter the
name of a new job (e.g., _test-job_ in the field _copy job to_, press
button _copy_. This creates a configuration directory in
`liwa-technologies/temporal-coherence/Heritrix3/jobs/test-job` of the
software package.

## Configuration of a Job ##

Configuration of the jobs is done by editing `liwa-technologies/temporal-coherence/Heritrix3/jobs/test-job/crawler-beans.cxml`  file. One may also edit the file in the Web interface (follow the _edit_
link at the beginning of the Web page of the job). The file is a Spring
configuration file and all the configuration objects are expressed as
Spring beans and their properties.

These three settings comprise the minimum that needs to be changed to
configure the crawl:
  1. Metadata settings
  1. Seeds settings
  1. Database settings

### Metadata Settings ###
Two settings needs to be changed in the bean _simpleOverrides_:the property _metadata.operatorContactUrl_ should point to the contact URL
of the crawling operator. The property _metadata.jobName_ must be the same
as the name specified when the job was created.  For example, one may set
the values to the properties in the following way:
```
metadata.operatorContactUrl=http://www.mpi-inf.mpg.de/departments/d5/
metadata.jobName=test-job
```

### Seeds ###
The coherence module of Heritrix crawls inputs the list of sitemaps, and
crawls all URLs from each sitemap. The input sitemaps are set in in the
_sitemaps.textSource.value_ of the _longerOverrides_ bean. Each sitemap URL
must be placed on a separate line. Here is an example:
```
<prop key="seeds.textSource.value">
        # URLS HERE 
	http://www.cnn.com/sitemap_index.xml
	http://www.cnn.com/sitemap_static.xml
</prop>
```

### Database Settings ###
The temporal coherence module writes the crawl data into a PostgreSQL
database with a specific DB schema. The database schema can be creating
using the SQL commands provided in `temporal-coherence/sql-scripts/liwa-coherence.sql` file.

The connection settings to the PostgreSQL DB are set in the
_connectionPool_ bean. One must set the server (name or IP address), the
user, the password, and the DB name. The other (four) parameters define the
connection and the pooling mechanisms and is safe to leave the default
values. Example of the DB configuration is given below:
```
<bean id="connectionPool"
	class="org.liwa.coherence.db.ConnectionPool">
	<property name="serverName" value="serverName" />
	<property name="user" value="user" />
	<property name="password" value="password" />
        <property name="databaseName" value="liwa_coherence" />
        <property name="dataSourceName" value="Coherence Data Source" />
	<property name="maxConnections" value="200" />
	<property name="dataSourceClass" value="org.postgresql.ds.PGPoolingDataSource"/> 
</bean>
```