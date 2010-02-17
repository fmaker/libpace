package edu.ucdavis.pace;

import java.util.Random;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

/**
 * 
 */
public class PaceService extends Service {
	private final String TAG = "PaceService";

	/**
     * 
     */
	final RemoteCallbackList<IPaceServiceCallback> mCallbacks = new RemoteCallbackList<IPaceServiceCallback>();

	NotificationManager mNM;

	private int notificationId;

	@Override
	public void onCreate() {
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		// Display a notification about us starting.
		notificationId = new Random().nextInt(65534);
		showNotification(notificationId);
		mHandler.sendEmptyMessage(TICK_MSG);
	}

	@Override
	public void onDestroy() {
		// Cancel the persistent notification.
		mNM.cancel(notificationId);

		// Tell the user we stopped.
		Toast.makeText(this, "Pace service stopped.", Toast.LENGTH_SHORT)
				.show();

		// Unregister all callbacks.
		mCallbacks.kill();

		// Remove the next pending message to increment the counter, stopping
		// the increment loop.
		mHandler.removeMessages(TICK_MSG);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// Select the interface to return. If your service only implements
		// a single interface, you can just return it here without checking
		// the Intent.
		if (IPaceService.class.getName().equals(intent.getAction())) {
			return mBinder;
		}
		return null;
	}

	/**
	 * The IPaceInterface is defined through AIDL
	 */
	private final IPaceService.Stub mBinder = new IPaceService.Stub() {

		public void scheduleCallback(IPaceServiceCallback cb)
				throws RemoteException {
			Log.d(TAG, "scheduleCallback()");
			if (cb != null)
				mCallbacks.register(cb);
		}

		public void unscheduleCallback(IPaceServiceCallback cb)
				throws RemoteException {
			if (cb != null)
				mCallbacks.unregister(cb);
		}
	};

	private static final int TICK_MSG = 1;

	/**
	 * Handler to schedule periodical job on self
	 */
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			// It is time callback clients!
			case TICK_MSG: {

				// Broadcast to clients that is due.
				final int N = mCallbacks.beginBroadcast();
				for (int i = 0; i < N; i++) {
					try {
						// ParcelUuid uuid = new ParcelUuid(UUID.randomUUID());
						mCallbacks.getBroadcastItem(i).timeout();
					} catch (RemoteException e) {
						// The RemoteCallbackList will take care of removing
						// the dead object for us.
					}
				}
				mCallbacks.finishBroadcast();

				// Repeat every 1 second.
				sendMessageDelayed(obtainMessage(TICK_MSG), 3 * 1000);
			}
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};

	/**
	 * Show a notification while this service is running.
	 */
	private void showNotification(int id) {
		// In this sample, we'll use the same text for the ticker and the
		// expanded notification
		CharSequence text = "Pace service started.";

		// Set the icon, scrolling text and timestamp
		Notification notification = new Notification(0, text, System
				.currentTimeMillis());

		// // The PendingIntent to launch our activity if the user selects this
		// notification
		// PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
		// new Intent(this, LocalPaceServiceController.class), 0);
		//
		// // Set the info for the views that show in the notification panel.
		// notification.setLatestEventInfo(this,
		// getText(R.string.pace_service_label),
		// text, contentIntent);

		// Send the notification.
		// We use a string id because it is a unique number. We use it later to
		// cancel.
		mNM.notify(id, notification);
	}
}
