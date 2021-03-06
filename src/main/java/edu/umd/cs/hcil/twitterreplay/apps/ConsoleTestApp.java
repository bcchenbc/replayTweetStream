package edu.umd.cs.hcil.twitterreplay.apps;

import edu.umd.cs.hcil.twitterreplay.DateParser;
import edu.umd.cs.hcil.twitterreplay.GzippedFileReader;
import edu.umd.cs.hcil.twitterreplay.ProducerTask;
import edu.umd.cs.hcil.twitterreplay.senders.ConsoleSender;
import edu.umd.cs.hcil.twitterreplay.senders.Sender;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hello world!
 *
 */
public class ConsoleTestApp 
{
    private static final Logger LOGGER = 
            Logger.getLogger(ConsoleTestApp.class.getName());
    
    private static Date getFirstTime(GzippedFileReader reader) {
        Date firstTime = null;
        
        try {
            reader.open();

            for ( String jsonString : reader ) {
                firstTime = DateParser.getDateFromJson(jsonString);
                
                break;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting first time: {0}", e);
        }
        
        return firstTime;
    }
    
    public static void main( String[] args )
    {
        if ( args.length < 1 ) {
            System.err.println("Usage: App <input_file>");
            
            System.exit(-1);
        }
        
        String filename = args[0];
        System.out.println("Reading File: " + filename);
        
        createProducer(filename, new ConsoleSender());
    }
    
    public static void createProducer(String filename, Sender s) {
        GzippedFileReader reader = new GzippedFileReader(filename);
        Date firstTime = getFirstTime(reader);
        
        ProducerTask task = new ProducerTask(firstTime, 1, Calendar.SECOND, reader, s);
        
        ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(4);
        scheduler.scheduleAtFixedRate(task, 1, 1, TimeUnit.SECONDS);        
    }
}
