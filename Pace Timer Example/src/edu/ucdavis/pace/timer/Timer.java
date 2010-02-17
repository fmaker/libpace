package edu.ucdavis.pace.timer;

import java.util.Date;
import java.util.Vector;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;
import edu.ucdavis.pace.IPaceService;

public class Timer {
	private final String TAG = "Timer(PACE)";
	private Context mContext;
	private Vector<TimerTask> mTasks = new Vector<TimerTask>();
	
	final Object mMonitor = new Object(); 

	private IPaceService mService = null;

	public Timer(Context c) {
		mContext = c;
		
		mTasks = new Vector<TimerTask>();

	}
	
	class dThread extends Thread{
		TimerTask mTask;
		dThread(TimerTask t){
			this.mTask=t;
		}
		
		@Override
		public void run(){
			try {
				synchronized(mMonitor){
					mMonitor.wait();
				}
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			} 
			Log.d(TAG, "mService wait notified");
			try {
				mService.scheduleCallback(mTask);
			} catch (RemoteException e) {
				
			}
		}
	}

	public void schedule(TimerTask task) {
		Log.d(TAG, "schedule()");
		mTasks.add(task);
		if(mService == null){
			mContext.bindService(new Intent(IPaceService.class.getName()),
					mConnection, Context.BIND_AUTO_CREATE);
			
			new dThread(task).start();
			return;
		}
		try {
			mService.scheduleCallback(task);
		} catch (RemoteException e) {
			Log.d(TAG, "RemoteException");
			// In this case the service has crashed before we could even
			// do anything with it; we can count on soon being
			// disconnected (and then reconnected if it can be restarted)
			// so there is no need to do anything here.
		}
	}

	/**
	 * After a TimerTask object is GCed, its callback will stop automatically.
	 */
	public void cancel() {
		for (TimerTask task : mTasks) {
			try {
				mService.unscheduleCallback(task);
			} catch (RemoteException e) {
				// Nothing needs to be done.
			}
			task.cancel();
		}
		mTasks.clear();
	}

	/**
	 * Class for interacting with the main interface of the service.
	 */
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			Log.d(TAG, "onServiceConnected.");
			mService = IPaceService.Stub.asInterface(service);
			Log.d(TAG, "Connected.");

			Toast.makeText(mContext, "PACE service connected.",
					Toast.LENGTH_SHORT).show();
			synchronized(mMonitor){
				mMonitor.notifyAll();
			}
		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			mService = null;

			Log.d(TAG, "Disconnected.");

			Toast.makeText(mContext, "PACE service disconnected",
					Toast.LENGTH_SHORT).show();
		}
	};

	protected void finalize() throws Throwable {
		try {
			if (mService!=null) {
				cancel();
				// Detach our existing connection.
				mContext.unbindService(mConnection);
				mService=null;
				Log.d(TAG, "Unbinding.");
			}
		} finally {
			super.finalize();
		}
	}

	/**
	 * Functions to mimic Java Timer API
	 */
	public void schedule(TimerTask task, long delay) {

		schedule(task);
	}

	public void schedule(TimerTask task, Date time) {

		schedule(task);
	}

	public void schedule(TimerTask task, Date firstTime, long period) {

		schedule(task);
	}

	public void schedule(TimerTask task, long delay, long period) {

		schedule(task);
	}

	public void scheduleAtFixedRate(TimerTask task, long delay, long period) {

		schedule(task);
	}

	public void scheduleAtFixedRate(TimerTask task, Date firstTime, long period) {

		schedule(task);
	}

}
