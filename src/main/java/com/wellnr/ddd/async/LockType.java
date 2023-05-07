package com.wellnr.ddd.async;

public enum LockType {

    /**
     * Allow access to this method, even if the instance or global lock is active.
     */
    READ_ONLY,

    /**
     * Set an optimistic lock to the instance when the annotated method is called.
     */
    READ_WRITE_INSTANCE,

    /**
     * Set an optimistic lock to all instances when the annotated method is executed.
     */
    READ_WRITE_GLOBAL

}
