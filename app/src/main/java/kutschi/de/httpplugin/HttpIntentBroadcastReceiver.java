package kutschi.de.httpplugin;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kutschi.de.httpplugin.model.Profile;
import kutschi.de.httpplugin.model.ProfileFactory;

/**
 * This broadcast receiver receives the intent from EgiGeoZone.
 *
 * Created by seb on 28.02.17.
 */

public class HttpIntentBroadcastReceiver extends WakefulBroadcastReceiver {
    private static final String TAG = "HttpIntentBroadcastRec";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        intent.setClass(context, HttpPluginIntentSevice.class);
        Log.i(TAG, "onReceive: Starting wakeful service @" + SystemClock.elapsedRealtime());
        startWakefulService(context, intent);
    }
}