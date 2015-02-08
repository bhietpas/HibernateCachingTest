package com.hietpas.hibernate.domain;

import com.hietpas.hibernate.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.stat.Statistics;
import org.hsqldb.server.Server;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.PrintWriter;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by Brad on 2/7/2015.
 */
public class CachingTest {

    //HSQL server for unit testing
    private static Server server;


    /**
     * Baseline Insert Test to make sure all config is setup correctly
     */
    @Test
    public void testInsert(){
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        //The item to save
        TestModel model = new TestModel();
        model.setTestData("Here is the data to save as part of this model");

        session.save(model);
        session.getTransaction().commit();

        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        List result = session.createQuery("from TestModel").list();
        assertTrue(result.size() > 0);

        session.close();
    }

    /**
     * Caching test
     */
    @Test
    public void testSecondLevelCaching(){
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Statistics stats = HibernateUtil.getSessionFactory().getStatistics();
        session.beginTransaction();

        //Confirm second level cache was never called.
        assertTrue(stats.getSecondLevelCacheHitCount() == 0);

        //The item to save
        TestModel model = new TestModel();
        model.setId(10);
        model.setTestData("Model1");

        session.save(model);
        session.getTransaction().commit();  //closes session

        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        //Confirm object was also put in second level cache but not retrieved
        assertTrue(stats.getSecondLevelCachePutCount() == 1);
        assertTrue(stats.getSecondLevelCacheHitCount() == 0);

        //Get model via hibernate get
        TestModel retrievedModel1 = (TestModel) session.get(TestModel.class, 10L);

        //Confirm it's the right object and was retrieved from second level cache
        assertTrue("Model1".equals(retrievedModel1.getTestData()));
        assertTrue(stats.getSecondLevelCacheHitCount() == 1);

        session.close();
    }

    /**
     * Starts the in memory hqsl database for the tests.
     */
    @BeforeClass
    public static void startTestDatabase(){
        server = new Server();
        server.setAddress("localhost");
        server.setDatabaseName(0, "mydb1");
        server.setDatabasePath(0, "mem:test1");
        server.setPort(1234);
        server.setTrace(true);
        server.setLogWriter(new PrintWriter(System.out));
        server.start();
    }

    /**
     * Shuts down the database after tests.
     */
    @AfterClass
    public static void stopTestDatabase(){

        server.stop();
    }

}
