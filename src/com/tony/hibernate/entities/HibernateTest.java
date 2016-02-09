package com.tony.hibernate.entities;

import static org.junit.Assert.*;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
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
		
		StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder()
		        .configure( "hibernate.cfg.xml" )
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
	
	/**
	 * flush: Keeps database table's records are same as Session's cache. For keeping same, may send corresponding sql.
	 * 1. Invoke Transaction's commit() method:invoke session's flush() method first, then send transaction
	 * 2. flush() method may send SQL, but will not send transaction
	 * 3. Notice:
	 * 		a. Before "session" submits transaction or invokes session.flush(), 
	 * 
	 * 
	 */
	
	@Test
	public void testSessionFlush(){
		News news = session.get(News.class, 1);
		news.setAuthor("Oracle");
		
	}

	@Test
	public void testSessionCache() {
		
		News news = session.get(News.class, 1);
		System.out.println(news);
		
		News news2 = session.get(News.class, 1);
		System.out.println(news2);

	}

}
