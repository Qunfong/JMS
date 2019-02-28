package loanbroker.loanbroker;

import mix.messaging.gateways.MessageReceiverGateway;
import mix.messaging.gateways.MessageSenderGateway;
import mix.model.bank.BankInterestReply;
import mix.model.bank.BankInterestRequest;
import mix.model.loan.LoanReply;
import mix.model.loan.LoanRequest;
import org.apache.camel.RecipientList;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

public class BankAppGateway {
    MessageSenderGateway messageSenderGateway;
    MessageReceiverGateway messageReceiverGateway;

    LoanBrokerFrame loanBrokerFrame;

    public BankAppGateway(LoanBrokerFrame loanBrokerFrame) {
        this.loanBrokerFrame = loanBrokerFrame;
    }

    @RecipientList
    public void sendBankRequest(LoanRequest loanRequest, BankInterestRequest bankInterestRequest, String correlation) throws JMSException {
        messageSenderGateway = new MessageSenderGateway("ToBank");
        ObjectMessage message = messageSenderGateway.createMessage(bankInterestRequest);
        message.setJMSCorrelationID(correlation);
        messageSenderGateway.send(message);
        loanBrokerFrame.correlations.put(correlation, loanRequest);



        System.out.println(this.getClass().getName() + "has sent a message : " + bankInterestRequest);
    }

    public void onBankReplyArrived() {
        messageReceiverGateway = new MessageReceiverGateway("ToLoanbroker");
        messageReceiverGateway.setListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                System.out.println("received message: " + message);
                Object object = null;
                try {
                    object = ((ObjectMessage) message).getObject();
                    BankInterestReply bankInterestReply = (BankInterestReply) object;

                    System.out.println("received message: " + bankInterestReply.getInterest());

                    LoanRequest request = loanBrokerFrame.correlations.get(message.getJMSCorrelationID());
                    LoanClientAppGateway loanClientAppGateway = new LoanClientAppGateway(loanBrokerFrame);
                    LoanReply reply = new LoanReply(bankInterestReply.getInterest(), bankInterestReply.getQuoteId());
                    loanClientAppGateway.sendLoanReply(reply, message.getJMSCorrelationID());
                    loanBrokerFrame.add(request, bankInterestReply);
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
