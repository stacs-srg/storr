package uk.ac.standrews.cs.storr.impl;

import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.utilities.archive.ErrorHandling;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Created by al on 23/03/2017.
 * Modelled on https://docs.oracle.com/javase/tutorial/essential/io/examples/WatchDir.java
 */
public class Watcher {
    private final WatchService watch_service;
    private final Map<WatchKey, IBucket> watched_buckets;

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }

    /**
     * Creates a WatchService and registers the given directory
     */
    Watcher() throws IOException { // Path dir) throws IOException {
        this.watch_service = FileSystems.getDefault().newWatchService();
        this.watched_buckets = new HashMap<WatchKey, IBucket>();
    }

    /**
     * Register the given directory with the WatchService
     */
    public void register(Path dir, IBucket b) throws IOException {
        WatchKey key = dir.register(watch_service, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        watched_buckets.put(key, b);
    }

    /**
     * Process all events for watched_paths queued to the watch_service
     */
    public void processEvents() {
        for (; ; ) {

            // wait for key to be signalled
            WatchKey key;
            try {
                key = watch_service.take();
            } catch (InterruptedException x) {
                return;
            }

            IBucket b = watched_buckets.get(key);

            if (b == null) {
                ErrorHandling.error("WatchKey not recognized");
                continue;
            }

            b.invalidateCache();
//            for (WatchEvent<?> event: key.pollEvents()) { don't need to do this - just invalidate the cache in the bucket
//            }

            // reset the key
            key.reset(); // returns a boolean which indicates if the key is valid but we are not bothered!

        }
    }

    public void startService() {
        // start the Watcher monitor
        Thread t = new Thread(new Runnable() {
            public void run() {
                processEvents();
            }
        });
        t.setDaemon(true);
        t.start();
    }
}
