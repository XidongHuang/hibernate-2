package com.tony.hibernate.entities.n21.both;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.jdbc.Work;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HibernateTest {

	private SessionFactory sessionFactory;
	private Session session;
	private Transaction transation;

	@Before
	public void init() {

		Configuration configuration = new Configuration().configure();

		StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml")
				.build();
		Metadata metadata = new MetadataSources(standardRegistry).getMetadataBuilder().build();

		sessionFactory = metadata.getSessionFactoryBuilder().build();

		session = sessionFactory.openSession();
		transation = session.beginTransaction();
	}

	@After
	public void destory() {

		transation.commit();
		session.close();
		sessionFactory.close();

	}
	
	@Test
	public void testCascade(){
		
		Customer customer = session.get(Customer.class, 3);
		customer.getOrders().clear();
		
		
	}
	
	
	@Test
	public void testDelete(){
		
		//Cannot delete "one" side object, 
		//if it does not set up cascading and the "one" side is relating with many objects in "many" side
		Customer customer = session.get(Customer.class, 1);
		session.delete(customer);
		
		
	}

	@Test
	public void testUpdate() {

		Order order = session.get(Order.class, 1);
		order.getCustomer().setCustomerName("AAA");

	}

	@Test
	public void testUpdate2(){
		
		Customer customer = session.get(Customer.class, 1);
		customer.getOrders().iterator().next().setOrderName("GGG");
		
		
		
	}
	
	
	@Test
	public void testOne2ManyGet(){
		//1. For collection in "many" side, it will use delay loading
		Customer customer = session.get(Customer.class, 4);
		System.out.println(customer.getOrders().getClass());
		//2. It returns a Hibernate collection type
		//This type has delay loading and agent object storing functions
		System.out.println(customer.getCustomerName());
		
		//3. It may throw LazyInitialiaztionException, when session.close(); happen before the object is invoked.
		
		//4. It will initial when the elements in collection need to be used
	}
	
	
	
	@Test
	public void testMany2OneGet() {
		// 1. If query an object in "many" side, it will not query the
		// corresponding object in
		// "one" side, in default.
		Order order = session.get(Order.class, 1);
		System.out.println(order.getOrderName());

		System.out.println(order.getCustomer().getClass().getName());

		// session.close(); LazyInitializationException

		// 2. It will send a corresponding SQL to database when it is using the
		// relative object
		Customer customer = order.getCustomer();
		System.out.println(customer.getCustomerName());

		// 3. There maybe be a LazyInitialiaztionException when querying
		// Customer object,
		// from "many" to "one" side: session is closed in the process of
		// querying from "many" to "one"

		// 4. In default, the related Customer object is just an agent object
		// for Order object.

	}

	@Test
	public void testMany2OneSave() {

		Customer customer = new Customer();
		customer.setCustomerName("AA");

		Order order1 = new Order();
		order1.setOrderName("ORDER-1");

		Order order2 = new Order();
		order2.setOrderName("ORDER-2");

		// Setting up the relation
		order1.setCustomer(customer);
		order2.setCustomer(customer);

		customer.getOrders().add(order1);
		customer.getOrders().add(order2);
		
		// Execute save operation: Insert Customer first then Order. 3 INSERT, 2 UPDATE
		// SQL
		// Insert "one" side first then "many" side. Only INSERT sql
		// Because "one" side is relating with "many" side, so UPDATE will be more
		// Set inverse="true" in "one" side, so the "one" side does not maintenance the instance relation!
		// Tips: Set "inverse=true", and INSERT the "one" side first, then "many" side
		// Advantage: No extra UPDATE
		 session.save(customer);
		
//		 session.save(order1);
//		 session.save(order2);
//	
		
		//Insert "Order" then "Customer", 3 INSERT, 4 UPDATE
//		session.save(order1);
//		session.save(order2);
//
//		session.save(customer);

	}

}
