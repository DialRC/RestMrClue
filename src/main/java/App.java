import org.apache.log4j.Logger;
import static spark.Spark.*;
/**
 * Main program of Java API server.
 * Created by Tiancheng Zhao on 10/26/16.
 */
public class App
{
    static Logger logger = Logger.getLogger(App.class);
    public static int PORT = 5678;

    public static void main( String[] args )
    {
        logger.info(String.format("Server Start Running at %d", App.PORT));
        port(App.PORT);

        post("/init", (request, response) ->
                "Hello World: " + request.body()
        );

        post("/next", (request, response) -> {
            return "Next here";
        });
    }
}