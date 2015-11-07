package timwforce.stufftoremindmeof;


import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;


public class StartServiceAsyncTask extends AsyncTask<Object, Void, String> {
	private Context context = null;
	
	@Override
	protected String doInBackground(Object... parameter) {
		Log.d("StartServiceAsyncTask#doInBackground", "Starting ...");
		int msToWait = 0;

		String msg = (String) parameter[0];
		context = (Context) parameter[1];
		
		Log.d("StartServiceAsyncTask#doInBackground", "message = " + msg);

		SharedPreferences mySharedPreferences = context.getApplicationContext().getSharedPreferences(MainActivity.MY_PREFS, Activity.MODE_PRIVATE);
		int whichTimeSelected = mySharedPreferences.getInt("whichTimeSelected", 0);
		Log.d("StartServiceAsyncTask#doInBackground", "SharedPreferences - whichTimeSelected = " + whichTimeSelected);
		String [] minutesArray = context.getResources().getStringArray(R.array.minutes_array);
		String minutesText = minutesArray[whichTimeSelected];
		int minutes = Integer.parseInt(minutesText);
		Log.d("StartServiceAsyncTask#doInBackground", "minutes (int) = " + minutes);
		
		msToWait += minutes * (60 * 1000);
		Log.d("StartServiceAsyncTask#doInBackground", "msToWait = " + msToWait);
		
		try {
			Thread.sleep(msToWait);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		tellUser(msg, context);

		return msg;
	}
	
	@Override
	protected void onPostExecute(String msg) {
		// toast
		Toast toast = Toast.makeText(this.context, msg, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.BOTTOM, 0, 0);
		toast.show();
	}
	
	private void tellUser(String msg, Context context) {
		// notification
		sendNotification(msg, context);		
	}
	
	
	private void sendNotification(String msg, Context context) {
		NotificationManager mNotificationManager = 
				(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification();
		// notification.icon = android.R.drawable.stat_notify_sync;
		notification.icon = android.R.drawable.stat_sys_warning;
		notification.tickerText = "Reminder";
		notification.when = System.currentTimeMillis();
		// notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
		notification.flags = Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_AUTO_CANCEL;
		notification.defaults |= Notification.DEFAULT_SOUND;
		//  notification.defaults |= Notification.DEFAULT_VIBRATE;
		// notification.vibrate = new long[]{100, 500, 100, 500, 100};
		
		// #ff1900 (dark orange)
		// #ff3300 (web-safe orange)
		// #ff2d00 (medium orange)
		// #ff4000 (medium orange)
		// #ff5300 (light orange)
		notification.ledARGB = Color.parseColor("#FF4000");
		notification.ledOffMS = 0;
		notification.ledOnMS = 1;
		

		// Intent intent = new Intent(this, MainActivity.class);
		notification.setLatestEventInfo(context, "Reminder", msg,
				PendingIntent.getActivity(context, 0, null, 0));
	            // PendingIntent.getActivity(this, 0, new Intent(), 0));
        		// PendingIntent.getActivity(this, 1, intent, 0));
		mNotificationManager.notify("test" + System.currentTimeMillis(), 100, notification);
		//NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
		//		.setSmallIcon(R.drawable.notification_icon)
		//		.setContentTitle("My Reminder")
		//		.setContentText("Go to the bank!");		
		
		// Vibrate
		Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(500);
	}

}
