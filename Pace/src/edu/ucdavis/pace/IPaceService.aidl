 

package edu.ucdavis.pace;

import edu.ucdavis.pace.IPaceServiceCallback;

/**
 * Example of a secondary interface associated with a service.  (Note that
 * the interface itself doesn't impact, it is just a matter of how you
 * retrieve it from the service.)
 */
interface IPaceService {
    /**
     * Sechedule a callback with task UUID;
     */
    void scheduleCallback(IPaceServiceCallback cb);
    
    /**
     * This demonstrates the basic types that you can use as parameters
     * and return values in AIDL.
     */
    void unscheduleCallback(IPaceServiceCallback cb);
}
