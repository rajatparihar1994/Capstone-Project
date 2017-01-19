package io.github.rajatparihar1994.indewas.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by rajpa on 28-Dec-16.
 */

public class inDewasSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static inDewasSyncAdapter inDewasSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (inDewasSyncAdapter == null) {
                inDewasSyncAdapter = new inDewasSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return inDewasSyncAdapter.getSyncAdapterBinder();
    }
}
