package kutschi.de.httpplugin;

import android.content.Context;
import android.content.Intent;
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

import java.util.HashMap;
import java.util.Map;

import kutschi.de.httpplugin.model.Profile;
import kutschi.de.httpplugin.model.ProfileFactory;

/**
 * Created by seb on 28.02.17.
 */

public class HttpIntentBroadcastReceiver extends WakefulBroadcastReceiver {
    private static final String TAG = "HttpIntentBroadcastRec";

    @Override
    public void onReceive(final Context context, final Intent intent) {

        try {
            ProfileFactory.getInstance().restore();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final Map<String, Profile> profiles = ProfileFactory.getInstance().getProfiles();

        for (final Profile profile : profiles.values()) {
            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(context);
            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(profile.getMethod(), profile.getUrl().trim(),
                    new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(context, "String Success :" + response, Toast.LENGTH_LONG).show();
                            Log.d(TAG, "String Success :" + response);
                        }
                    },
                    new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(context, "String  Error In Request :" + error.toString(), Toast.LENGTH_LONG).show();
                            Log.d(TAG, "String  Error In Request :" + error.toString());

                        }
                    }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
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