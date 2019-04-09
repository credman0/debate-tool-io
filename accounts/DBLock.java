package org.debatetool.io.accounts;

public interface DBLock {
    DBLockResponse tryLock(byte[] hash);
    void unlock(byte[] hash);
    void unlockAll();
    void unlockAllExcept(byte[] hash);

    void unlockAllExcept(byte[]... hashes);
}
