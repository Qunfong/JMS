package mix.serializer;

import com.owlike.genson.Genson;
import mix.model.loan.LoanReply;
import mix.model.loan.LoanRequest;

public class LoanSerializer {
    Genson genson;

    public String requestToString(LoanRequest loanRequest) {
        String request = genson.serialize(loanRequest);
        return request;
    }

    public LoanRequest requestFromString(String str) {
        LoanRequest loanRequest = genson.deserialize(str, LoanRequest.class);
        return loanRequest;
    }

    public String replyToString(LoanReply loanReply) {
        String request = genson.serialize(loanReply);
        return request;
    }

    public LoanReply replyFromString(String str) {
        LoanReply loanReply = genson.deserialize(str, LoanReply.class);
        return loanReply;
    }


}
