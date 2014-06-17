package uk.ac.standrews.cs.digitising_scotland.tools.analysis;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import uk.ac.standrews.cs.digitising_scotland.tools.Utils;

/**
 * The Class MemoryMonitor uses {@link MemoryMXBean} to log system memory usage.
 * Memory usage is then written out to a file called "memoryUsage_" + a unique time.
 */
public class MemoryMonitor implements Runnable {

    /** The mxbean. */
    private MemoryMXBean mxbean;

    /** The date format. */
    private DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd HH_mm");

    /** The cal. */
    private Calendar cal = Calendar.getInstance();

    /** The running. */
    private boolean running = true;

    /** The time. */
    private int time = 0;

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {

        while (running) {

            if (mxbean == null) {
                mxbean = ManagementFactory.getMemoryMXBean();
            }

            while (true) {
                time++;
                Utils.writeToFile(time + "\t" + mxbean.getHeapMemoryUsage().getCommitted() + "\t" + mxbean.getHeapMemoryUsage().getUsed() + "\n", "target/memoryUsage_" + cal.getTime() + ".txt", true);
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * Instantiates a new memory monitor.
     */
    public MemoryMonitor() {

        //MemoryMXBean

        mxbean = ManagementFactory.getMemoryMXBean();
        System.out.println(dateFormat.format(cal.getTime()));
    }

    /**
     * Stop.
     */
    public void stop() {

        running = false;
    }

}
