package com.ultimo.app;
import com.ultimo.util.*;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.Message;
import org.apache.activemq.ActiveMQConnection;

public class Warehouse {
	
	
		String name;
		int stock;
		String OrderID;
		int quantity;
		private static Session session;
		public static String url = "tcp://localhost:61616";
		public static String user =ActiveMQConnection.DEFAULT_USER;
		public static String password =  ActiveMQConnection.DEFAULT_PASSWORD;
		
		TopicProducer warehouseProducer;
		TopicConsumer warehouseConsumer;

		public Warehouse (String name, int stock) {

			this.name = name; 
			this.stock= stock;

		}
		public Warehouse () {
			
			
			
		}

		public void recieveAdvisoryOrder() { 
			try {
			 warehouseConsumer = new TopicConsumer();
			 warehouseConsumer.GetConnection(user,password,url);
			 warehouseConsumer.start();
			Message msg= warehouseConsumer.getMsg();
			this.quantity=msg.getIntProperty("Quantity");
			this.OrderID=msg.getStringProperty("OrderID");
		 	new Warehouse().canTakeOrder(quantity, OrderID);
			}
			
			catch (JMSException er)
			{
			}
			 
			 	
		}

		public void expressIntent(String OrderID) {
			try {
		
				warehouseProducer = new TopicProducer();
				warehouseProducer.GetConnection(user,password,url);
				Message Accepted = session.createMessage();
		   	 	Accepted.setStringProperty("Order ID", OrderID);
		   	 	Accepted.setStringProperty("Intent","Warehouse will complete Order");
		   
		   	 		
			}
			catch (JMSException re) 
			{
			}
		}

		public void canTakeOrder(int quantity,String OrderID) {
			
			 		
			 	if ( quantity <= this.stock) 
				{
				this.stock = stock-quantity;
				new Warehouse().expressIntent(OrderID);
				new Warehouse().orderAcceptable(OrderID);
				
				}
			else 
		{
			new Warehouse().orderUnacceptable(OrderID);
			}
			}	
		
		public  void orderAcceptable (String OrderID) {
		try {
			warehouseProducer = new TopicProducer();
			warehouseProducer.GetConnection(user,password,url);
			Message Accepted = session.createMessage();
	   	 	Accepted.setStringProperty("Order ID", OrderID);
	   	    Accepted.setStringProperty("Order Status", "Accepted");
	   	    warehouseProducer.sendMessage(Accepted);
	
		}
		catch (JMSException re) 
		{
		}
		
	}

		
		public void orderUnacceptable (String OrderID) {
			try {
				warehouseProducer = new TopicProducer();
				warehouseProducer.GetConnection(user,password,url);
				Message NotAccepted = session.createMessage();
		   	 	NotAccepted.setStringProperty("Order ID", OrderID);
		   	    NotAccepted.setStringProperty("Order Status", "Not Accepted. Not Enough Supplies");
		   	    warehouseProducer.sendMessage(NotAccepted);
			}
			catch (JMSException re) 
			{
			}
		
		}
		
	
		
		
}
