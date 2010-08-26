package org.europarchive;

/**
 * Plugin to Heritrix. Analyze all urls for streaming schemas.
 * In order to the plugin the file conf/heritrix.properties needs to be modified.
 * The property org.archive.net.UURIFactory.ignored-schemes should exclude streaming schemas,
 * so please remove rtsp,pnm,mms schema from the list of ignored schemes and add it to the list
 * of accepted schemes like for instance:
 * org.archive.net.UURIFactory.schemes = http, https, dns, invalid, rtsp, mms, pnm
 * The plugin should be connected to the pre-fetch processors list.
 * A new ExternalImplDecideRule should be added to the crawl order and the name of the 
 * implementation class should be org.europarchive.StreamingURI.
 * The decision setting should be REJECT.
 *  
 * @author gabriel
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

import org.archive.crawler.datamodel.CandidateURI;
import org.archive.crawler.deciderules.ExternalImplInterface;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

public class StreamingURI implements ExternalImplInterface
{
	/**
	 * AMQP message skeleton
	 */
	private static final String MESSAGE_SKELETON="<job>\n"+
"    <client>[$client]</client>\n"+
"    <command>[$command]</command>\n"+
"    <crawl-id>[$crawl-id]</crawl-id>\n"+
"    <patch-for>[$patch-for]</patch-for>\n"+
"    <seeds>\n"+
"        <seed>[$seed]</seed>\n"+
"    </seeds>\n"+
"    <scope>[$scope]</scope>\n"+
"    <arc-prefix>[$prefix]</arc-prefix>\n"+
"    <politeness>2</politeness>\n"+
"    <follow-robots>false</follow-robots>\n"+
"</job>";
	
	private static final Logger logger = Logger.getLogger(StreamingURI.class.getName());
	private static final String CONFIG_FILENAME = "config.properties";
	//property names
	private static final String PROPERTY_ACTIVE_SCHEMAS = "active_schemas";
	private static final String PROPERTY_AMQP_HOST = "amqp_host";
	private static final String PROPERTY_AMQP_PORT = "amqp_port";
	private static final String PROPERTY_AMQP_USER = "amqp_user";
	private static final String PROPERTY_AMQP_PASS = "amqp_pass";
	private static final String PROPERTY_AMQP_VHOST = "amqp_vhost";
	private static final String PROPERTY_AMQP_EXCHANGE = "amqp_exchange";
	private static final String PROPERTY_AMQP_QUEUE = "amqp_queue";
	
	private static String [] schemas;
	//this is needed as heritrix is not abording the treat of an url if an processor rejects it
	//it continues with the full chain
	private static String lastTreatedUri;
	private static String amqpHost;
	private static String amqpUser;
	private static String amqpPass;
	private static int amqpPort;
	private static String amqpVhost;
	private static String amqpExchange;
	private static String amqpQueue;
	private static Connection amqpConnection;
	private static Channel amqpChannel;
	
	public StreamingURI() throws IOException
	{
		java.io.FileWriter fw = new java.io.FileWriter("out.log",true);
		fw.write("instantiated streaming uri\n");
		fw.close();
		loadConfig();
	}
	
	/**
	 * Create the text message send to the streaming capture tool
	 * @param skel
	 * @param client
	 * @param command
	 * @param seed
	 * @param prefix
	 * @return
	 * @throws IOException
	 */
	private static String createMessage(String skel,String client,String command,String seed,String prefix) throws IOException
	{
		String res = skel.replace("[$client]", client);
		res = res.replace("[$command]", command);
		res = res.replace("[$seed]", seed);
		res = res.replace("[$prefix]", prefix);
		
		return res;
	}
	
	/**
	 * load configuration settings
	 * @throws IOException
	 */
	private void loadConfig() throws IOException
	{
		//TODO: take from the classpath the configuration file and make the connection to AMQP
		File f = new File(CONFIG_FILENAME);
		InputStream is = (f.exists())? new FileInputStream(CONFIG_FILENAME):
	            this.getClass().getResourceAsStream("/" + CONFIG_FILENAME);
	    if (is == null) {
	            throw new IOException("Failed to load properties file from" +
	                " filesystem or from classpath.");
	    }
		Properties p = new Properties();
		p.load(is);
		String activeSchemas = p.getProperty(PROPERTY_ACTIVE_SCHEMAS);
		amqpHost = p.getProperty(PROPERTY_AMQP_HOST);
		amqpPort = Integer.parseInt(p.getProperty(PROPERTY_AMQP_PORT));
		amqpUser = p.getProperty(PROPERTY_AMQP_USER);
		amqpPass = p.getProperty(PROPERTY_AMQP_PASS);
		amqpVhost = p.getProperty(PROPERTY_AMQP_VHOST);
		amqpExchange = p.getProperty(PROPERTY_AMQP_EXCHANGE);
		amqpQueue = p.getProperty(PROPERTY_AMQP_QUEUE);
		
		schemas = activeSchemas.split(",");
		p.store(System.out, "comment");
		connectAMQP();
	}
	
	/**
	 * obtain a connection to the AMQP server
	 * @throws IOException
	 */
	private void connectAMQP() throws IOException
	{
		if (amqpChannel != null) amqpChannel.close();
		if (amqpConnection != null) amqpConnection.close();
		amqpConnection = AMQPUtil.getConnection(amqpHost, amqpPort, amqpUser, amqpPass, amqpVhost);
		amqpChannel = AMQPUtil.getChannel(amqpConnection);
		AMQPUtil.createQueue(amqpChannel, amqpExchange, "streaming_in", "streaming_in");		
	}
	
	@Override
	public boolean evaluate(Object obj) {
	       try {
	            CandidateURI curi = (CandidateURI)obj;
	            String str = curi.getUURI().toString();
	            boolean result = false;
	            for (String schema:schemas)
	            {
	            	if (str.startsWith(schema+"://"))
		            {
		            	result = true;
		            	break;
		            }
	            }
	            
	            //not interested in this schema
	            if (!result)
	            	return false;
	            
	            try
	            {
	            	//check if we did not already treat this uri
	            	java.io.FileWriter fw = new java.io.FileWriter("out.log",true);
	            	if (!str.equals(lastTreatedUri))
	            	{			    		
			    		fw.write("Tested '" + str + " and result was " + result+"\n");			    		
			    		lastTreatedUri = str;
			    		String message = createMessage(MESSAGE_SKELETON, "LiWA", "start", str, "EA-streaming");
			    		AMQPUtil.sendMessage(amqpChannel, amqpExchange, amqpQueue, message.getBytes("UTF-8"));
	            	}
	            	else
	            	{
	            		fw.write("Already tested '" + str + "\n");
	            	}
	            	fw.close();
	            }catch(Exception ex)
	            {
	            	ex.printStackTrace();
	            }

	            return result;
	        } catch (ClassCastException e) {
	            // if not CrawlURI, always disregard
	            return false; 
	        }
	}

}
