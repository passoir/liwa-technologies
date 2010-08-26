package org.europarchive;
/**
 * @author gabriel
 * Utils functions to connect to AMQP capable servers, create a queue,
 * get and send AMQP messages
 */
import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConnectionParameters;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.GetResponse;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.AMQP.Exchange.DeclareOk;
import com.rabbitmq.client.AMQP.Queue.BindOk;

public class AMQPUtil {

	/**
	 * Obtain a connection to AMQP server
	 * @param host server machine address
	 * @param port port the AMQP server listens on
	 * @param user username
	 * @param pass password
	 * @param vhost the name of the virtual host this connection is to be done
	 * @return a valid connection
	 * @throws IOException
	 */
	public static Connection getConnection(String host,int port,String user,String pass,String vhost) throws IOException
	{
		Connection conn;
		ConnectionParameters params = new ConnectionParameters();
		
		params.setRequestedHeartbeat(0);
		params.setUsername(user);
		params.setPassword(pass);
		params.setVirtualHost(vhost);
		
			conn = new ConnectionFactory(params).newConnection(host, port);
			return conn;
	}

	/**
	 * Obtain a valid channel to communicate with AMQP server
	 * @param conn a connection to the server
	 * @return 
	 * @throws IOException
	 */
	public static Channel getChannel(Connection conn) throws IOException
	{
		com.rabbitmq.client.Channel ch = conn.createChannel();
		return ch;
	}
	
	/**
	 * create a queue on the server
	 * @param ch a channel connected to the server
	 * @param exchange the name of the AMQP exchange
	 * @param queueName the name of the queue
	 * @param routingKey the routing key
	 * @throws IOException
	 */
	public static void createQueue(Channel ch,String exchange,String queueName,String routingKey) throws IOException
	{
		DeclareOk res = ch.exchangeDeclare(exchange,"direct", true);
		com.rabbitmq.client.AMQP.Queue.DeclareOk res1 = ch.queueDeclare(queueName,true);
		//now bind this queue to a routing key
		BindOk res2 = ch.queueBind(queueName, exchange, routingKey);		
	}
	
	/**
	 * read a message from a queue
	 * @param channel
	 * @param exchange
	 * @param routingKey
	 * @return
	 * @throws IOException
	 */
	public static byte[] getMessage(Channel channel,String exchange,String routingKey) throws IOException
	{
		DeclareOk res = channel.exchangeDeclare(exchange,"direct", true);
		GetResponse delivery = channel.basicGet(routingKey, false);
		if ((delivery == null) || (delivery.getBody() == null)) 
		{			
			//logger.debug("nothing... retrying");
			//try { Thread.sleep(1000); } catch (Exception ex) {}
			//continue;
			//System.out.println("No AMQP message");
			//sendMessage("no message");
			return null;
		}
		
		return delivery.getBody();
	}
	
	/**
	 * Send a message to the queue, the message is persistent, text format
	 * @param channel
	 * @param exchange
	 * @param queue
	 * @param message
	 * @throws IOException
	 */
	public static void sendMessage(Channel channel,String exchange,String queue,byte[] message) throws IOException
	{
		//System.out.println("Sending mesage:"+message);
		channel.basicPublish(exchange, queue, MessageProperties.PERSISTENT_TEXT_PLAIN, message);
	}
}
