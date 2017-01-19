package io.github.rajatparihar1994.indewas.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by rajpa on 28-Dec-16.
 */

public class inDewasAuthenticatorService extends Service {
    // Instance field that stores the authenticator object
    private inDewasAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new inDewasAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
