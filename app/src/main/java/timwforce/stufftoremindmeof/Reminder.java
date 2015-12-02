package timwforce.stufftoremindmeof;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Reminder {
	private String message;
	private int    minutes;
	private Date   startDateTime;
	
	public Reminder(String message, int minutes) {
		this.message = message;
		this.minutes = minutes;
		this.startDateTime = new Date();
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
	public void setMinutes(int minutes) { this.minutes = minutes; }
	
	
	// make reminder responsible for adding self to layout -- more OO oriented
	public void addTextViewToLayout(LinearLayout ll, Context context, 
									LinearLayout.LayoutParams textLayoutParams) {
		TextView tv = new TextView(context);
		tv.setTypeface(null, Typeface.BOLD);
		SimpleDateFormat fmt = new SimpleDateFormat("hh:mm");
		final SpannableStringBuilder sbSpannable = new SpannableStringBuilder(this.getMessage() + "\n - start time: " + fmt.format(startDateTime));
		sbSpannable.setSpan(new android.text.style.StyleSpan(Typeface.ITALIC), this.getMessage().length(), sbSpannable.length(), android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		tv.setText(sbSpannable);
		tv.setTextColor(context.getResources().getColor(R.color.text_view_font_color));
		tv.setTextSize(16.0f);
		ll.addView(tv, textLayoutParams);
	}
}
