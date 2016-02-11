package com.tony.hibernate.entities;

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
	
	
	@Test
	public void testComponent(){
		Worker worker = new Worker();
		Pay pay = new Pay();
		
		pay.setMonthlyPay(1000);
		pay.setYearPay(80000);
		pay.setVocationWithPay(5000);
		
		worker.setName("ABCD");
		worker.setPay(pay);
		session.save(worker);
		
	}
	
	
	
	@Test
	public void testBlob() throws IOException, SQLException{
//		News news = new News();
//		news.setAuthor("cc");
//		news.setContent("CONTENT");
//		news.setDate(new Date());
//		news.setDesc("DESC");
//		news.setTitle("CC");
//		
//		
//		
//		InputStream stream = new FileInputStream("favicon.ico");
//		Blob image = Hibernate.getLobCreator(session).createBlob(stream, stream.available());
//		news.setImage(image);
//		
//		session.save(news);
		
		News news = session.get(News.class, 1);
		Blob image = news.getImage();
		
		InputStream in = image.getBinaryStream();
		System.out.println(in.available());
		
		
	}
	
	
	@Test
	public void testPropertyUpdate(){
		News news = session.get(News.class, 1);
		news.setTitle("aaaa");
		
		System.out.println(news.getDesc());
		System.out.println(news.getDate().getClass());
		
	}
	
	
	@Test
	public void testIdGenerator() throws InterruptedException{
		News news = new News("AA", "aa", new Date());
		session.save(news);
		
//		Thread.sleep(5000);
	}
	
	
	@Test
	public void testDynamicUpdate(){
		News news = session.get(News.class, 1);
		news.setAuthor("ABCD");
		
		
	}
	
	
	
	@Test
	public void testDoWork(){
		session.doWork(new Work() {
			
			@Override
			public void execute(Connection connection) throws SQLException {
				System.out.println(connection);

				//Invoke process of saving
			}
		});
		
		
	}
	
	
	/**
	 * evict: delete a specific persist object from session cache
	 * 
	 * 
	 */
	@Test
	public void testEvict(){
		
		News news1 = session.get(News.class, 1);
		News news2 = session.get(News.class, 2);
		
		news1.setTitle("AA");
		news2.setTitle("BB");
		
		session.evict(news1);
	}
	
	
	/**
	 * delete: execute delete operation. If OID equals a record in database, it will run deletion.
	 * If OID do not have the corresponding record, it throws an Exception.
	 * 
	 * Set the configuration file of hibernate, "hibernate.use_identifier_rollback" is "true"
	 * to let the deleted object's OID to be set to null 
	 * 
	 */
	@Test
	public void testDelete(){
		//Dissociate object
//		News news = new News();
//		news.setId(2);
		
		//Persist object
		News news = session.get(News.class, 5);
		session.delete(news);
	}
	
	
	
	/**
	 * Notice:
	 * 1. If OID is not null, but database does not have the corresponding record. It throws an Exception.
	 * 2. OID's value equal the value of id's unsaved-value, it will be considered a dissociate object 
	 * 
	 */
	
	@Test
	public void testSaveOrUpdate(){
		News news = new News("FF", "ff", new Date());
		news.setId(11);
		session.saveOrUpdate(news);
		
		
	}
	
	
	
	/**
	 * update:
	 * 1. If update a persist object, it is not necessary to invoke update() method.
	 * 	  Because session will execute flush() method then transaction running commit() method
	 * 
	 * 2. Updating a dissociate object needs to invoke update() method individually.
	 * 	  It can persist a dissociate object.
	 * 
	 * Notice:
	 * 1. No matter the updating dissociate objects whether are the same as database records,
	 * 	  update() method will send UPDATE sql to database.
	 * 
	 * 	  How can we let update() method do not send sql blindly?
	 * 	  Answer: Set "select-before-update=true" in .hbm.xml file's class.(But don't set this attribute usually)
	 * 
	 * 2. If database tables do not have corresponding records, but invoke update() method, it will throw exception
	 * 
	 * 3. If update() method relates to a dissociate object but there is a persist
	 * 	  object with a same OID in the Session, then it will throw an exception.
	 * 	  Because there are no two same OID object in the cache of Session
	 */
	
	@Test
	public void testUpdate(){
		News news = session.get(News.class, 2);
		
		transation.commit();
		session.close();
		
		session = sessionFactory.openSession();
		transation = session.beginTransaction();
		
		
		News news2 = session.get(News.class, 2);
		session.clear();
		
//		news.setAuthor("SUN");
		
		session.update(news);
		
		
	}
	
	

	/**
	 * get VS load:
	 * 
	 * 1. Execute "get" method: It will be loading immediately. (Immediately Query)
	 * 	  But "load" method will not, it will return an agent object instead of searching
	 * 	  (Delay Query)
	 * 
	 * 2. If there are no corresponding records in data table and "Session" is closed
	 * 	  "get" returns "null"
	 * 	  "load" throws an "exception" if it tries to instance the object;
	 * 	   it will be fine if it does not use the object.
	 * 
	 * 3. "load" method may throw "LazyInitializationException"
	 * 	   Because "Session" is closed before agent object being instance
	 */
	
	@Test
	public void testLoad(){
		
		
		News news = session.load(News.class, 10);
		System.out.println(news.getClass().getName());
		
//		session.close();
//		
//		System.out.println(news);
		
		
	}
	
	
	@Test
	public void testGet(){
		News news = session.get(News.class, 2);
//		session.close();
		System.out.println(news);
		
		
	}
	
	
	
	/**
	 * The difference between persist() and save()
	 * 
	 * If an object has "id" before invoking persist(), then it will not execute
	 * INSERT sql, instead of throwing an exception
	 * 
	 */
	@Test
	public void testPersist(){
		
		News news = new News();
		news.setTitle("EE");
		news.setAuthor("ee");
		news.setDate(new Date());
		news.setId(200);
		
		
		session.persist(news);
		
	}
	
	
	
	
	/**
	 * 1. save() method:
	 * a) Change a transient object to persist object 
	 * b) Assign a ID to objects
	 * c) send a INSERT sql when doing flush() method
	 * d) 
	 * 
	 */
	@Test
	public void testSave(){
		
		News news = new News();
//		news.setTitle("AA");
//		news.setAuthor("aa");
		news.setTitle("BB");
		news.setAuthor("bb");
		news.setId(100);
		news.setDate(new Date());
		System.out.println(news);
		session.save(news);
		System.out.println(news);
		news.setId(15);
	}
	
	
	/**
	 * 
	 * clear(): clear cache
	 * 
	 * 
	 */
	@Test
	public void testClear(){
		News news1 =  session.get(News.class, 2);
		
		session.clear();
		
		News news2 =  session.get(News.class, 2);
		
		
	}
	
	
	@Test
	public void testRefresh(){
		News news = session.get(News.class, 2);
		System.out.println(news);

		session.refresh(news);
		System.out.println(news);
		
		
	}
	
	
	/**
	 * flush: Keeps database table's records are same as Session's cache. For keeping same, may send corresponding sql.
	 * 1. Invoke Transaction's commit() method:invoke session's flush() method first, then send transaction
	 * 2. flush() method may send SQL, but will not send transaction
	 * 3. Notice: Before "session" submits transaction or invokes session.flush(), it may operate flush() action.
	 * 		a.  For the last data tables' record,it will be executing HQL or QBC first. 
	 * 		b. If record's id is generated by "native", then after invoke save(), it will sent INSERT sql right away.
	 * 			Because after save, there must ensure the object's ID existing.  
	 * 
	 */
	@Test
	public void testSessionFlush2(){
		News news = new News("Java","SUN",new Date());
		session.save(news);
		
		
	}
	
	
	@Test
	public void testSessionFlush(){
		News news = session.get(News.class, 1);
		news.setAuthor("SUN");
//		
//		session.flush();
//		System.out.println("flush");
		
		News news2 = (News) session.createCriteria(News.class).uniqueResult();
		
		System.out.println(news2);
		
		
	}

	@Test
	public void testSessionCache() {
		
		News news = session.get(News.class, 1);
		System.out.println(news);
		
		News news2 = session.get(News.class, 1);
		System.out.println(news2);

	}

}
