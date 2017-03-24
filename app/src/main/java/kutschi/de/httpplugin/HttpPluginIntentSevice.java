package kutschi.de.httpplugin;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
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
 * This IntentService, executes the http request depending on the settings in the profiles
 * Created by seb on 28.02.17.
 */

public class HttpPluginIntentSevice extends IntentService {

    private static final String TAG = HttpPluginIntentSevice.class.getName();
    private static final String ACTION_EGIGEOZONE_EVENT = "de.egi.geofence.geozone.plugin.EVENT";

    public HttpPluginIntentSevice() {
        super("HttpPluginIntentSevice");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "onHandleIntent: Received intent from EgiGeoZone");
        String action = intent.getAction();
        if (ACTION_EGIGEOZONE_EVENT.equals(action)) {
            // Call Method to perform EgiGeoZone events
            doEvent(intent);
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        HttpIntentBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void doEvent(final Intent intent) {
        try {
            ProfileFactory.getInstance().restore();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final Map<String, Profile> profiles = ProfileFactory.getInstance().getProfiles();
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        for (final Profile profile : profiles.values()) {
            final List<String> activeZoneNames = Arrays.asList(profile.getZones());
            final String zone = intent.getStringExtra("zone_name");
            if (!activeZoneNames.contains(zone)) {
                return;
            }
            Log.d(TAG, "onReceive: profile contains entered/leaved zone");
            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(profile.getMethod(), profile.getUrl().trim(),
                    new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.succesful_sent), Toast.LENGTH_LONG).show();
                            Log.i(TAG, "Http request sucessfull sent: " + response);
                        }
                    },
                    new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "String  Error In Request :" + error.toString(), Toast.LENGTH_LONG).show();
                            Log.e(TAG, "String  Error In Request :" + error.getLocalizedMessage(), error);

                        }
                    }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    if (profile.getUsername() != null && !profile.getUsername().isEmpty() && profile.getPassword() != null && !profile.getUsername().isEmpty()) {
                        String creds = String.format("%s:%s", profile.getUsername(), profile.getPassword());
                        String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                        params.put("Authorization", auth);
                    }
                    return params;
                }

                @Override
                public String getBodyContentType() {
                    return profile.getContentType();
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    final int method = profile.getMethod();
                    if (method == Method.GET || method == Method.DELETE) {
                        return new byte[0];
                    } else {
                        String payload;
                        if ("1".equals(intent.getStringExtra("transition"))) { //entering
                            payload = profile.getEnteringContent();
                        } else { //leaving
                            payload = profile.getLeavingContent();
                        }
                        return payload.getBytes();
                    }
                }
            };
            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        }
    }
}
