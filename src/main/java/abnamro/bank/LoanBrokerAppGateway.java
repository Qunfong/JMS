package abnamro.bank;

import mix.messaging.gateways.MessageReceiverGateway;
import mix.messaging.gateways.MessageSenderGateway;
import mix.messaging.requestreply.RequestReply;
import mix.model.bank.BankInterestReply;
import mix.model.bank.BankInterestRequest;
import mix.model.loan.LoanReply;
import mix.model.loan.LoanRequest;
import mix.serializer.LoanSerializer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

public class LoanBrokerAppGateway {
    MessageSenderGateway messageSenderGateway;
    MessageReceiverGateway messageReceiverGateway;
    LoanSerializer loanSerializer;

    BankInterestRequest bankInterestRequest;
    JMSBankFrame jmsBankFrame;

    public LoanBrokerAppGateway(JMSBankFrame jmsBankFrame) {
        this.jmsBankFrame = jmsBankFrame;
    }

    public void sendReplyToBroker(BankInterestReply bankInterestReply, String correlationId) throws JMSException {
        messageSenderGateway = new MessageSenderGateway("ToLoanbroker");
        ObjectMessage message = messageSenderGateway.createMessage(bankInterestReply);
        message.setJMSCorrelationID(correlationId);
        messageSenderGateway.send(message);

        System.out.println(this.getClass().getName() + "has sent a message : " + bankInterestReply);
    }

    public void onBankIntererstRequestArrived() {
        messageReceiverGateway = new MessageReceiverGateway("ToBank");
        messageReceiverGateway.setListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                Object object = null;
                try {
                    System.out.println("received message: " + message);
                    object = ((ObjectMessage) message).getObject();
                    bankInterestRequest = (BankInterestRequest) object;
                    jmsBankFrame.add(bankInterestRequest, message.getJMSCorrelationID());
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
