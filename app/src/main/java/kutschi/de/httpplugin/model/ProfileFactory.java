package kutschi.de.httpplugin.model;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import kutschi.de.httpplugin.R;

/**
 * This factory holds all profiles and is reponsible for storing and restoring them.
 *
 * Created by seb on 09.03.17.
 */

public class ProfileFactory {

    private final static String KEY_PROFILES = "profiles";

    private static final Uri CONTENT_URI = Uri.parse("content://de.egi.geofence.geozone.zonesContentProvider/zoneNames");
    private static final Uri CONTENT_BT_URI = Uri.parse("content://de.egi.geofence.geozone.bt.zonesContentProvider/zoneNames");
    private static final String CN_NAME = "name";

    private final static ProfileFactory INSTANCE = new ProfileFactory();
    private Map<String, Profile> profiles = new HashMap<>();

    private Context context;
    private SharedPreferences preferences;

    private ProfileFactory() {
        // hidden constructor
    }

    public static ProfileFactory getInstance() {
        return INSTANCE;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Map<String, Profile> getProfiles() {
        return Collections.unmodifiableMap(profiles);
    }

    public String addProfile(String id, Profile profile) {
        String key;
        key = id == null ? UUID.randomUUID().toString() : id;
        profiles.put(key, profile);
        return id;
    }

    public void removeProfile(String id) {
        profiles.remove(id);
    }

    public void restore() throws JSONException {
        this.preferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        final String jsonString = preferences.getString(KEY_PROFILES, null);
        if (jsonString == null) {
            return;
        }

        final JSONArray jsonArray = new JSONArray(jsonString);
        for (int idx = 0; idx < jsonArray.length(); idx++) {
            final JSONObject jsonObject = (JSONObject) jsonArray.get(idx);
            final String id = jsonObject.getString("id");
            final String value = jsonObject.getString("value");
            profiles.put(id, new Profile(value));
        }
    }

    public void persist() throws JSONException {
        final SharedPreferences.Editor editor = preferences.edit();
        JSONArray jsonArray = new JSONArray();
        for (Map.Entry<String, Profile> profileEntry : profiles.entrySet()) {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", profileEntry.getKey());
            jsonObject.put("value", profileEntry.getValue().toJsonString());
            jsonArray.put(jsonObject);
        }
        editor.putString(KEY_PROFILES, jsonArray.toString());
        editor.apply();
    }

    public List<String> getZoneNames() {
        ContentResolver contentResolver = context.getContentResolver();
        final List<String> zoneNames = getZonesFromContentProvider(contentResolver, CONTENT_URI);
        zoneNames.addAll(getZonesFromContentProvider(contentResolver, CONTENT_BT_URI));
        return zoneNames;
    }

    @NonNull
    private List<String> getZonesFromContentProvider(ContentResolver contentResolver, Uri contentUri) {
        final List<String> zoneNames = new ArrayList<>();
        try (Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String zoneName = cursor.getString(cursor.getColumnIndex(CN_NAME));
                    zoneNames.add(zoneName);
                } while (cursor.moveToNext());
            }
        }
        return zoneNames;
    }
}
