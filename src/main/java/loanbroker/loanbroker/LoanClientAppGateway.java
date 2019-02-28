package loanbroker.loanbroker;

import mix.messaging.gateways.MessageReceiverGateway;
import mix.messaging.gateways.MessageSenderGateway;
import mix.messaging.requestreply.RequestReply;
import mix.model.bank.BankInterestRequest;
import mix.model.loan.LoanReply;
import mix.model.loan.LoanRequest;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

public class LoanClientAppGateway {
    MessageSenderGateway messageSenderGateway;
    MessageReceiverGateway messageReceiverGateway;

    LoanBrokerFrame loanBrokerFrame;

    public LoanClientAppGateway(LoanBrokerFrame loanBrokerFrame) {
        this.loanBrokerFrame = loanBrokerFrame;
    }

    public void onLoanRequestArrived() throws JMSException {
        messageReceiverGateway = new MessageReceiverGateway("LoanRequest");
        messageReceiverGateway.setListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                System.out.println("received message: " + message);
                Object object = null;
                try {
                    object = ((ObjectMessage) message).getObject();
                    LoanRequest loanRequest = (LoanRequest) object;
                    System.out.println(message.getIntProperty("aggregationID"));
                    loanBrokerFrame.add(loanRequest);
                    BankAppGateway sender = new BankAppGateway(loanBrokerFrame);
                    BankInterestRequest bankInterestRequest = new BankInterestRequest(loanRequest.getAmount(), loanRequest.getTime());
                    sender.sendBankRequest(loanRequest, bankInterestRequest, message.getJMSCorrelationID());

                    loanBrokerFrame.add(loanRequest, bankInterestRequest);
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void sendLoanReply(LoanReply loanReply, String correlation) throws JMSException {
        messageSenderGateway = new MessageSenderGateway("ReplyToClient");
        ObjectMessage message = messageSenderGateway.createMessage(loanReply);
        message.setJMSCorrelationID(correlation);
        messageSenderGateway.send(message);
    }
}
