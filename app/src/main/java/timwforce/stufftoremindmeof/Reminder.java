package timwforce.stufftoremindmeof;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Reminder {
	private String message;
	private int    minutes;
	
	public Reminder(String message, int minutes) {
		this.message = message;
		this.minutes = minutes;
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public int getMinutes() {
		return minutes;
	}
	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}
	
	
	// make reminder responsible for adding self to layout -- more OO oriented
	public void addTextViewToLayout(LinearLayout ll, Context context, 
									LinearLayout.LayoutParams textLayoutParams) {
		TextView tv = new TextView(context);
		tv.setTypeface(null, Typeface.BOLD);
		tv.setText(this.getMessage());
		tv.setTextColor(context.getResources().getColor(R.color.text_view_font_color));
		tv.setTextSize(16.0f);
		ll.addView(tv, textLayoutParams);
	}
}
