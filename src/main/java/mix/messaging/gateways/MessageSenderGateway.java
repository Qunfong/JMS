package mix.messaging.gateways;

import mix.model.loan.LoanRequest;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.Serializable;
import java.util.Properties;

public class MessageSenderGateway {
    Connection connection;
    Session session;
    Destination destination;
    MessageProducer producer;


    public MessageSenderGateway(String channelName) {
        try {
            Properties props = new Properties();
            props.setProperty(Context.INITIAL_CONTEXT_FACTORY,					                  "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");

            props.put(("queue." + channelName), channelName);

            Context jndiContext = new InitialContext(props);
            ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext
                    .lookup("ConnectionFactory");

            ActiveMQConnectionFactory activeMQConnectionFactory = (ActiveMQConnectionFactory) connectionFactory;
            activeMQConnectionFactory.setTrustAllPackages(true);

            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // connect to the sender destination
            destination = (Destination) jndiContext.lookup(channelName);
            producer = session.createProducer(destination);

        } catch (NamingException | JMSException e) {
            e.printStackTrace();
        }
    }

    public ObjectMessage createMessage(Object object) throws JMSException {
        ObjectMessage objectMessage = session.createObjectMessage();
        objectMessage.setObject((Serializable) object);

        return objectMessage;
    }

    public void send(ObjectMessage message) throws JMSException {
        producer.send(message);
    }
}
