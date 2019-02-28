package loanclient.loanclient;


import mix.messaging.gateways.MessageReceiverGateway;
import mix.model.loan.LoanReply;
import mix.model.loan.LoanRequest;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

public class LoanReplyBroker {

    private LoanClientFrame loanClientFrame;

    MessageReceiverGateway messageReceiverGateway;

    public LoanReplyBroker(LoanClientFrame loanClientFrame) {
        this.loanClientFrame = loanClientFrame;
    }

    public void onLoanReplyArrived() {
        messageReceiverGateway = new MessageReceiverGateway("ReplyToClient");
        messageReceiverGateway.setListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                System.out.println("received message: " + message);
                Object object = null;
                try {

                    object = ((ObjectMessage) message).getObject();
                    LoanReply loanReply = (LoanReply) object;
                    LoanRequest loanRequest = loanClientFrame.correlations.get(message.getJMSCorrelationID());

                    loanClientFrame.add(loanRequest, loanReply);
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
