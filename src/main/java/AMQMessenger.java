import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;

import javax.jms.*;
import java.util.UUID;


/**
 * A blocking version of AMQ message broker. It will send a message with a certain sendTopic
 * and return the results in a blocking fashion
 * Created by Tiancheng Zhao on 10/26/16.
 */
public class AMQMessenger {
    static Logger logger = Logger.getLogger(AMQMessenger.class);
    private String ip;
    private int port;
    private String sendTopic;
    private String recvTopic;
    private String subscribeName;
    private Connection connection;
    private Session session;

    public AMQMessenger(String ip, int port, String sendTopic,
                        String recvTopic, String clientID, String subscribeName) throws Exception
    {
        this.ip = ip;
        this.port = port;
        this.sendTopic = sendTopic;
        this.recvTopic = recvTopic;
        this.subscribeName = subscribeName;
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://"+this.ip+":"+this.port);
        this.connection = connectionFactory.createConnection();
        this.connection.setClientID(clientID);
        this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        this.connection.start();
    }

    /**
     * Run the instance and send the message to the given ip/port/sendTopic
     * The thread terminates right after the message is sent
     */
    private boolean send(String textMessage, String requestId) {
        String curSendTopic = this.sendTopic + "-" + requestId;
        try {
            // Create the destination (Topic or Queue)
            Destination destination = session.createTopic(curSendTopic);

            // Create a MessageProducer from the Session to the Topic or Queue
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            // Create a messages
            TextMessage message = session.createTextMessage(textMessage);

            // VHmsg property
            message.setStringProperty("ELVISH_SCOPE", "DEFAULT_SCOPE");
            message.setStringProperty("MESSAGE_PREFIX", "vr" + textMessage.split(" ", 2)[0]);

            // Tell the producer to send the message
            logger.info("Sent message: "+ textMessage + " with sendTopic: " + curSendTopic);
            producer.send(message);
            return true;
        }
        catch (Exception e) {
            logger.error(e.getStackTrace());
            return false;
        }
    }
    private String receive(String requestId) {
        try {
            // auto_ack means there is no ack. Pure async
            javax.jms.Topic clientTopic = session.createTopic(recvTopic+requestId);
            MessageConsumer consumer = session.createDurableSubscriber(clientTopic, subscribeName+requestId);
            Message message = consumer.receive();

            logger.debug("Get a new message");
            String messageText = "";
            if (message instanceof TextMessage) {
                TextMessage txtMsg = (TextMessage) message;
                messageText = txtMsg.getText();
            } else if(message instanceof BytesMessage) {
                BytesMessage btMsg = (BytesMessage) message;
                int msgLen = (int)btMsg.getBodyLength();
                byte [] msgBody = new byte[msgLen];
                btMsg.readBytes(msgBody);
                messageText = new String(msgBody, 0, msgLen);
            }
            logger.info("Message content is: " + messageText);
            return messageText;

        } catch (Exception e) {
            logger.error(e);
            return "Error: receive message failed.";
        }
    }

    public String sendAndReceive(String message) {
        // get a unique ID for topic
        String requestId = UUID.randomUUID().toString();
        if (this.send(message, requestId)) {
            return receive(requestId);
        } else {
            return "Error: send message failed.";
        }
    }

}