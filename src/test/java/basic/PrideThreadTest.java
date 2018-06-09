package basic;
/*******************************************************************************
 * Copyright (c) 2001-2007 The PriDE team and MATHEMA Software GmbH
 * All rights reserved. This toolkit and the accompanying materials 
 * are made available under the terms of the GNU Lesser General Public
 * License (LGPL) which accompanies this distribution, and is available
 * at http://pride.sourceforge.net/LGPL.html
 * 
 * Contributors:
 *     Jan Lessner, MATHEMA Software GmbH - JUnit test suite
 *******************************************************************************/

import de.mathema.pride.DatabaseFactory;
import de.mathema.pride.ResourceAccessor;
import org.junit.Test;

import java.sql.Date;
import java.util.HashSet;
import java.util.Iterator;

import static org.junit.Assume.assumeFalse;

/**
 * Test class for testing concurrent database access from multiple threads
 * This class creates a lot of customer records using {@link OPS_PER_THREAD} as
 * starting point for IDs to avoid conflicts with any data being created by
 * PrideBaseTest.
 * 
 * @author <a href="mailto:jan.lessner@mathema.de">Jan Lessner</a>
 */
@NeedsDBType(ResourceAccessor.DBType.ORACLE)
public class PrideThreadTest extends AbstractPrideTest {

    private static final int NUM_THREADS = 2;
    private static final int OPS_PER_THREAD = 100;
    private static final long MILLISECONDS_PER_DAY = 86400000L;
    
    private HashSet threads = new HashSet();
    private Exception lastThreadException;
    
	@Test
    public void testConcurrentWrite() throws Exception {
        String dbType = DatabaseFactory.getDatabase().getDBType();
        assumeFalse("Concurrent Write test does not work for MySQL", dbType.equalsIgnoreCase(ResourceAccessor.DBType.MYSQL));
        for (int i = 1; i < NUM_THREADS+1; i++) {
            Writer wr = new Writer(i*OPS_PER_THREAD, OPS_PER_THREAD);
            threads.add(wr);
        }
        runThreads();
        // Use a reader thread object for checking the data but without actually running as seperate thread
        new Reader(OPS_PER_THREAD, OPS_PER_THREAD * NUM_THREADS).run();
    }

	@Test
    public void testConcurrentRead() throws Exception {
        // Use a writer thread object for creating data but without actually running as seperate thread
        new Writer(OPS_PER_THREAD, OPS_PER_THREAD * NUM_THREADS).run();
        for (int i = 1; i < NUM_THREADS+1; i++) {
            Reader rd = new Reader(i*OPS_PER_THREAD, OPS_PER_THREAD);
            threads.add(rd);
        }
        runThreads();
        if (lastThreadException != null)
            throw lastThreadException;
    }
    
    private synchronized void runThreads() throws Exception {
        Iterator iter = threads.iterator();
        while (iter.hasNext()) {
            ((Thread) iter.next()).start();
        }
        wait(); //wait for last thread to call notify in removeThread()
    }

    private synchronized void removeThread(Thread thread) {
        threads.remove(thread);
        if (threads.isEmpty())
            notify(); // Notify main-thread to give feedback!
    }

    @Override
    public void tearDown() throws Exception {
        if (!isDBType(ResourceAccessor.DBType.POSTGRES)) // Posgres blocks on drop table - reason is unclear
            super.tearDown();
    }

    private class Writer extends Thread {
        private int startId;
        private int numIds;

        public Writer(int startId, int numIds) {
            this.startId = startId;
            this.numIds = numIds;
        }
        
        public void run() {
            try {
                long time = startId * MILLISECONDS_PER_DAY;
                for (int i = startId; i < startId + numIds; i++) {
                    Customer c = new Customer(i, "f#" + i, "l#" + i, new Boolean(i%2 > 0), new Date(time));
                    time += MILLISECONDS_PER_DAY;
                    DatabaseFactory.getDatabase().commit();
                }
            }
            catch(Exception x) {
                lastThreadException = x;
                fail(x.getMessage());
            }
            finally {
                removeThread(this);
            }
        }
    }
    
    private class Reader extends Thread {
        private int startId;
        private int numIds;

        public Reader(int startId, int numIds) {
            this.startId = startId;
            this.numIds = numIds;
        }
        
        public void run() {
            try {
                long time = startId * MILLISECONDS_PER_DAY;
                for (int i = startId; i < startId + numIds; i++) {
                    Customer c = new Customer(i);
                    assertEquals("f#" + i, c.getFirstName());
                    assertEquals("l#" + i, c.getLastName());
                    assertEquals(new Boolean(i%2 > 0), c.getActive());
                    // Comparing string representations of the dates works around time zone problems
                    assertEquals(time, c.getHireDate().getTime());
                    time += MILLISECONDS_PER_DAY;
                }
            }
            catch(Exception x) {
                lastThreadException = x;
            }
            finally {
                removeThread(this);
            }
        }
        
    }
    
}
