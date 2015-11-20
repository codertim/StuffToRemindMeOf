package timwforce.stufftoremindmeof;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.prefs.Preferences;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.view.View;


public class MainActivity extends Activity implements OnClickListener {
	private static final int            TEXT_VIEW_HEIGHT  = 75;
	private static final int            TEXT_VIEW_WIDTH   = 300;
	public static final  List<Reminder> reminders         = new ArrayList<Reminder>();
	private static       LinearLayout   ll                = null;
	public static final  String         MESSAGE_KEY       = "MESSAGE";
	public static final  String         MY_PREFS          = "MY_PREFS";
	private static final int            REQUEST_CODE      = 123;
	private static       ImageButton    voiceImageButton  = null;
	private static       View           currentDialogView = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// setContentView(R.layout.activity_main);
		
		// default minutes is 1, which is first in array, position 0
    	SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences(MY_PREFS, Activity.MODE_PRIVATE).edit();
    	editor.putInt("whichTimeSelected", 0);
    	editor.apply();
    	
		Log.d("MainActivity#onCreate", "voiceImageButton=" + voiceImageButton);

		setupLayout(ll);
	}
	
	
	private void setupLayout(LinearLayout ll) {
		ll = null;
		ll = new LinearLayout(this);
		
		// overall layout
		ll.setOrientation(LinearLayout.VERTICAL);
		// ll.setBackgroundColor(getResources().getColor(R.color.main_background));	
		ll.setBackgroundResource(R.drawable.drawable_gradient_background);

		// button for new reminder
		LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		buttonLayoutParams.setMargins(10, 20, 10, 2);
		Button newReminderButton = new Button(this);
		newReminderButton.setText(getResources().getText(R.string.new_button_label));
		newReminderButton.setTextColor(getResources().getColor(R.color.button_font));   // set button font color same as app background
		newReminderButton.setOnClickListener(this);
		newReminderButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16.0f);
		ll.addView(newReminderButton, buttonLayoutParams);
		

		// text 
		LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(TEXT_VIEW_WIDTH, TEXT_VIEW_HEIGHT);
		textLayoutParams.setMargins(10, 2, 10, 2);


		// line
		LinearLayout.LayoutParams separatorLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 5); // LinearLayout.LayoutParams.WRAP_CONTENT);
		View separatorView = new View(this);
		separatorView.setLayoutParams(separatorLayoutParams);
		separatorView.setBackgroundColor(Color.DKGRAY);
		ll.addView(separatorView);


		// add reminder list label
		TextView tvSubtitle = new TextView(this);
		tvSubtitle.setTypeface(null, Typeface.BOLD);
		tvSubtitle.setText(" :: Reminders           ");
		tvSubtitle.setTextColor(getResources().getColor(R.color.text_view_font_color));
		tvSubtitle.setTextSize(16.0f);
		// tvSubtitle.setBackgroundColor(Color.DKGRAY);
		tvSubtitle.setGravity(Gravity.CENTER);
		ll.addView(tvSubtitle, textLayoutParams);

		
		// add individual reminders
		for(Reminder reminder : reminders) {
			reminder.addTextViewToLayout(ll, this, textLayoutParams);
		}
		
		setContentView(ll);
	}
	
	private void askUserForNewReminder() {
		LayoutInflater layoutInflater = this.getLayoutInflater();
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.MyCustomAlertDialog));
		final View dialogView = layoutInflater.inflate(R.layout.dialog_new_message, null);
		currentDialogView = dialogView;
		alertDialogBuilder.setView(dialogView);
		alertDialogBuilder.setTitle("Select minutes");
		Log.d("onClick", "Calling setItems ...");

		alertDialogBuilder.setSingleChoiceItems(R.array.minutes_array, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	Log.d("setSingleChoiceItems", "which=" + which);
            	SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences(MY_PREFS, Activity.MODE_PRIVATE).edit();
            	editor.putInt("whichTimeSelected", which);
            	editor.apply();   // better backgrounding than  commit();
            }
		});

		alertDialogBuilder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
				// do nothing
			}
		
		});
		
		Log.d("onClick", "Setting cancelable");
		alertDialogBuilder.setCancelable(true);		
		
		// final View v = getLayoutInflater().inflate(R.layout.dialog_new_message, null);
		
		alertDialogBuilder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
				Log.d("onClick", "setPositiveButton#onClick - arg1 = " + arg1);
				EditText myEditText = (EditText) dialogView.findViewById(R.id.edit_text_message);
				Log.d("onClick", "myEditText: " + myEditText);
				Log.d("onClick", "myEditText.getText(): " + myEditText.getText());

				String message = myEditText.getText().toString();
				Log.d("onClick", "User entered message: " + message);
				reminders.add(new Reminder(message, 9));
				
				setupLayout(ll);
				
				dialog.cancel();
				
				Intent serviceIntent = new Intent(MainActivity.this, ReminderService.class);
				serviceIntent.putExtra(MainActivity.MESSAGE_KEY, message);
				startService(serviceIntent);

				// hide app if settings allow
				SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
				boolean isAppGoingToBackgroundOnCreate = sharedPrefs.getBoolean("minimizeOnCreate", false);
				Log.d("setPositiveButton", "Minimize from settings: " + Boolean.valueOf(isAppGoingToBackgroundOnCreate).toString());
				if(isAppGoingToBackgroundOnCreate) {
					hideApp(MainActivity.this);
				}
			}
		
		});


		voiceImageButton = (ImageButton) dialogView.findViewById(R.id.voice_button);
		voiceImageButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d("Voice Button - onClick", "Starting ...");
				
		        // disable if no recognition service is present
		        PackageManager pm = getPackageManager();
		        List<ResolveInfo> activities = pm.queryIntentActivities(
		                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		        if (activities.size() == 0)
		        {
		        	Log.i("Speech Button", "speech NOT enabled");
		            voiceImageButton.setEnabled(false);
		            // voiceImageButton.setText("Voice input not available");
		        } else {
		        	Log.i("Speech Button", "speech enabled");
		        	Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		        	intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		        	intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice recognition Demo...");
		        	startActivityForResult(intent, REQUEST_CODE);
		        }
			}
		});
		
		AlertDialog alertDialog = alertDialogBuilder.create();
		Log.d("onClick", "Created AlertDialog");

		alertDialog.show();
	}
	
	
	private void hideApp(Context context) {
		Intent startMain = new Intent(Intent.ACTION_MAIN);
		startMain.addCategory(Intent.CATEGORY_HOME);
		startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(startMain);
	}

	// click handler for speech input
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	String firstMatch = null;
    	
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
        {
            // Populate the wordsList with the String values the recognition engine thought it heard
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            Log.d("onActivityResult", "ArrayList matches size = " + matches.size());
            Log.d("onActivityResult", "ArrayList matches = " + matches);
            if(matches.size() > 0) {
            	firstMatch = matches.get(0);
            	Log.i("onActivityResult", "ArrayList matches first = " + firstMatch);
				EditText myEditText = (EditText) currentDialogView.findViewById(R.id.edit_text_message);
				myEditText.setText(firstMatch.trim());
            }


        }
        super.onActivityResult(requestCode, resultCode, data);
    }

	
	public void onClick(View v) {
		Log.d("MainActivity", "onClick v.getId() = " + v.getId());
		askUserForNewReminder();
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.action_settings:
			startActivity(new Intent(this, Prefs.class));
			return true;
		}
		return false;
	}

}
