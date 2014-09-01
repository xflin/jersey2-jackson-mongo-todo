package org.example.todo;

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Message;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class TwilioUtil {
    private final String accountSid, authToken, fromPhoneNo;
    private final TwilioRestClient twilioClient;

    public TwilioUtil() {
        accountSid = System.getenv("TWILIO_ACCOUNT_SID");
        authToken = System.getenv("TWILIO_AUTH_TOKEN");
        fromPhoneNo = System.getenv("TWILIO_FROM_PHONE_NO");

        twilioClient = accountSid != null && authToken != null ?
                new TwilioRestClient(accountSid, authToken) : null;
    }

    /**
     * Sends SMS message to the specified phone#.
     *
     * @param toPhone The target phone#.
     * @param msg The message body.
     * @return SID of the message.
     * @throws TwilioRestException When there a problem sending the message.
     */
    public String sms(String toPhone, String msg) throws TwilioRestException {
        String msgSid = null;
        if (twilioClient != null && fromPhoneNo != null) {
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("To", toPhone));
            params.add(new BasicNameValuePair("From", fromPhoneNo));
            params.add(new BasicNameValuePair("Body", msg));

            MessageFactory messageFactory =
                    twilioClient.getAccount().getMessageFactory();
            Message message = messageFactory.create(params);
            msgSid = message.getSid();
        }
        return msgSid;
    }
}
