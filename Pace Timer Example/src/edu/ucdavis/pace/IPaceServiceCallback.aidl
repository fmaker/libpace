 

package edu.ucdavis.pace;



/**
 * Callback interface used by IPaceService to send
 * synchronous notifications back to its clients.
 */
interface IPaceServiceCallback {
    /**
     * Called when PACE decide its time to sync
     */
    void timeout();
}
