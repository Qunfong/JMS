package loanclient.loanclient;

import mix.messaging.gateways.MessageReceiverGateway;
import mix.messaging.gateways.MessageSenderGateway;
import mix.messaging.requestreply.RequestReply;
import mix.model.loan.LoanReply;
import mix.model.loan.LoanRequest;
import mix.serializer.LoanSerializer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.HashMap;

public class LoanBrokerAppGateway {
    MessageSenderGateway messageSenderGateway;
    LoanSerializer loanSerializer;

    LoanReply loanReply;

    HashMap<String, LoanRequest> correlations;

    public LoanBrokerAppGateway(HashMap<String, LoanRequest> correlations) {
        this.correlations = correlations;
    }

    public void applyForLoan(LoanRequest loanRequest) throws JMSException {
        messageSenderGateway = new MessageSenderGateway("LoanRequest");
        ObjectMessage message = messageSenderGateway.createMessage(loanRequest);
        correlations.put(message.getJMSMessageID(), loanRequest);
        messageSenderGateway.send(messageSenderGateway.createMessage(loanRequest));

        System.out.println(this.getClass().getName() + "has sent a message : " + loanRequest);
    }


}
