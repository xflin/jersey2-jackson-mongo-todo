package org.example.todo;

import com.twilio.sdk.TwilioRestException;
import org.junit.Test;

import java.util.Date;

/**
 * This test depends on the following env vars to be set:
 *   TWILIO_ACCOUNT_SID, TWILIO_AUTH_TOKEN, TWILIO_FROM_PHONE_NO,
 *   TWILIO_TO_PHONE_NO
 *
 * NOTE: Otherwise it will silently pass without sending anything!
 *
 * This is useless as an automated unit test but a good tool to have for me
 * to try out twilio manually.
 */
public class TwilioTest {

    @Test
    public void sendSmsTest() throws TwilioRestException {
        TwilioUtil twilio = new TwilioUtil();
        String to = System.getenv("TWILIO_TO_PHONE_NO");
        if (to != null) twilio.sms(to, "twilio sms test @" + new Date());
    }
}
