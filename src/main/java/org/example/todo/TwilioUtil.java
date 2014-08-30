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
        accountSid = System.getenv("TWILIO_ACCONT_SID");
        authToken = System.getenv("TWILIO_AUTH_TOKEN");
        fromPhoneNo = System.getenv("TWILIO_FROM_PHONE_NO");

        twilioClient = accountSid != null && authToken != null ?
                new TwilioRestClient(accountSid, authToken) : null;
    }

    public void sendSms(String msg) throws TwilioRestException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("From", "+14087286057"));

        MessageFactory messageFactory =
                twilioClient.getAccount().getMessageFactory();
        Message message = messageFactory.create(params);
        System.out.println(message.getSid());
    }
}
