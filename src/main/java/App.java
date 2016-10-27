import org.apache.activemq.usage.SystemUsage;
import org.apache.log4j.Logger;

import static spark.Spark.*;
/**
 * Main program of Java API server.
 * Created by Tiancheng Zhao on 10/26/16.
 */
public class App
{
    static Logger logger = Logger.getLogger(App.class);
    public static int httpPort = 5678;
    public static int amqPort = 61616;
    public static String amqIP = "localhost";
    
    public static void main( String[] args )
    {
        logger.info(String.format("Server Start Running at %d", App.httpPort));

        AMQMessenger messenger;
        try {
            messenger = new AMQMessenger(amqIP, amqPort, "http2vh", "vh2http", "MrClue", "MrClueSub");
        } catch (Exception e) {
            logger.error(e);
            return;
        }

        port(App.httpPort);
        post("/init", (request, response) -> {
            return messenger.sendAndReceive(request.body());
        });

        post("/next", (request, response) -> {
            return messenger.sendAndReceive(request.body());
        });
    }
}