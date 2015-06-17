package com.hackaholic.trainpanda.ServiceHandler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.hackaholic.trainpanda.ui.activities.MainActivity;
import com.hackaholic.trainpanda.ui.fragments.PNRFragment;


public class PnrSmsReceiver extends BroadcastReceiver {

	// Get the object of SmsManager
	final SmsManager sms = SmsManager.getDefault();

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

		// Retrieves a map of extended data from the intent.
		final Bundle bundle = intent.getExtras();

		try {

			if (bundle != null) {

				final Object[] pdusObj = (Object[]) bundle.get("pdus");

				for (int i = 0; i < pdusObj.length; i++) {

					SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
					String phoneNumber = currentMessage.getDisplayOriginatingAddress();

					String senderNum = phoneNumber;
					String message = currentMessage.getDisplayMessageBody();

					Log.i("SmsReceiver", "senderNum: " + senderNum + "; message: " + message);

					if(message.contains("PNR:")){
						
						int pos = message.lastIndexOf(",");
						//String start = message.substringBefore(message, ".");
						String newstr = message.substring(0, pos);
						
						String pnr = newstr.substring(newstr.lastIndexOf(":") + 1);
						
						 /*Intent in = new Intent();
					        in.setClassName("com.hackaholic.Receiver", "com.hackaholic.Activities.MainActivity");
					        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					        context.startActivity(in);*/
						
						PNRFragment.updateMessageBox(pnr);
						
						Intent myIntent = new Intent(context.getApplicationContext(), MainActivity.class);
						myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
						
						context.getApplicationContext().startActivity(myIntent);
						
						//PNRFragment.updateMessageBox(pnr);
												
					}//else Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
						
					
					

				} // end for loop
			} // bundle is null

		} catch (Exception e) {
			Log.e("SmsReceiver", "Exception smsReceiver" + e);

		}

	}

}
