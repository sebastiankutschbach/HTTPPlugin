package kutschi.de.httpplugin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by seb on 28.02.17.
 */

public class HttpIntentBroadcastReceiver extends WakefulBroadcastReceiver {
    private static final String TAG = "HttpIntentBroadcastRec";
    private SharedPreferences preferences;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        preferences = context.getSharedPreferences("", Context.MODE_PRIVATE);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = preferences.getString(String.valueOf(R.id.txtUrl), "").trim();
        // Request a string response from the provided URL.
        final int method = preferences.getInt(String.valueOf(R.id.rgMethod), Request.Method.GET);
        StringRequest stringRequest = new StringRequest(method, url,
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
                }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                if(preferences.getBoolean(String.valueOf(R.id.swtAuthEnabled), false)) {
                    String username = preferences.getString(String.valueOf(R.id.txtUsername), "");
                    String password = preferences.getString(String.valueOf(R.id.txtPassword), "");
                    String creds = String.format("%s:%s", username, password);
                    String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                    params.put("Authorization", auth);
                }
                return params;
            }

            @Override
            public String getBodyContentType() {
                return preferences.getString(String.valueOf(R.id.spinnerContentType) + "/text", "text/plain");
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                if(method == Method.GET || method == Method.DELETE) {
                    return new byte[0];
                } else {
                    String payload;
                    if("1".equals(intent.getStringExtra("transition"))) { //entering
                        payload = preferences.getString(String.valueOf(R.id.txtPayloadZoneIn), "");
                    } else { //leaving
                        payload = preferences.getString(String.valueOf(R.id.txtPayloadZoneOut), "");
                    }
                    return payload.getBytes();
                }
            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
