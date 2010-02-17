package edu.ucdavis.pace.timer;

import edu.ucdavis.pace.IPaceServiceCallback;

public abstract class TimerTask extends IPaceServiceCallback.Stub implements Runnable {
	// ParcelUuid mParcelUUID;
	private boolean canceled = false;

	protected TimerTask() {
		// mParcelUUID = new ParcelUuid(UUID.randomUUID());
	}

	public boolean cancel() {
		if (canceled)
			return false; // Already canceled;
		canceled = true; // Cancel it
		return true;
	}

	public void timeout() {
		if (!canceled) {
			new Thread(this).run();
		}
	}

	public abstract void run();
}
