package com.example.pace.timer;

import java.util.Calendar;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import edu.ucdavis.pace.timer.Timer;
import edu.ucdavis.pace.timer.TimerTask;

public class PaceTimerTest extends Activity {
	private TextView tv;
	private Timer mTimer;
	private TimerTask mTask = new TimerTask() {
		@Override
		public void run() {
			uiHandler.sendEmptyMessage(0);
		}
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mTimer = new Timer(PaceTimerTest.this);
		tv = (TextView) findViewById(R.id.status);

		Button button = (Button) findViewById(R.id.schedule);

		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mTimer.schedule(mTask);
			}
		});

	}

	private Handler uiHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			tv.setText("Called by PACE:" + Calendar.getInstance().getTime());
		}
	};

}
