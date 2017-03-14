package kutschi.de.httpplugin;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONException;

import java.util.Map;

import kutschi.de.httpplugin.model.Profile;
import kutschi.de.httpplugin.model.ProfileFactory;

/**
 * Created by seb on 09.03.17.
 */

public class HttpMainActivity extends AppCompatActivity {

    private ListView profileListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ProfileFactory.getInstance().setContext(getApplicationContext());
        try {
            ProfileFactory.getInstance().restore();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // List View
        profileListView = (ListView) findViewById(R.id.profileListView);
        ProfileListAdapter adapter = new ProfileListAdapter(this, ProfileFactory.getInstance().getProfiles());
        profileListView.setAdapter(adapter);
        profileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Empty intent for creation
                Intent intent = new Intent(getApplicationContext(), HttpProfileActivity.class);
                final Map.Entry<String, Profile> entry = (Map.Entry) parent.getAdapter().getItem(position);
                intent.putExtra("id", entry.getKey());
                intent.putExtra(Profile.class.getName(), entry.getValue());
                startActivity(intent);
            }
        });

        // Floating Action Button
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab_add);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Empty intent for creation
                Intent intent = new Intent(getApplicationContext(), HttpProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            ProfileFactory.getInstance().restore();
            ((ProfileListAdapter) profileListView.getAdapter()).notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            ProfileFactory.getInstance().persist();
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}

@TargetApi(Build.VERSION_CODES.M)
class ProfileListAdapter extends BaseAdapter {

    private final Context context;
    private final Map<String, Profile> profiles;
    private final LayoutInflater layoutInflater;

    ProfileListAdapter(Context context, @NonNull Map<String, Profile> profiles) {
        this.context = context;
        this.profiles = profiles;
        this.layoutInflater = context.getSystemService(LayoutInflater.class);
    }

    @Override
    public int getCount() {
        return profiles.size();
    }

    @Override
    public Object getItem(int position) {
        return profiles.entrySet().toArray()[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Map.Entry<String, Profile> entry = (Map.Entry<String, Profile>) getItem(position);
        Profile profile = entry.getValue();
        if (convertView == null) {
            convertView = this.layoutInflater.inflate(R.layout.profile_list_item, parent, false);
        }

        ((TextView) convertView.findViewById(R.id.tv_profile_list_item_description)).setText(profile.getDescription());
        ((TextView) convertView.findViewById(R.id.tv_profile_list_item_url)).setText(profile.getUrl());
        ((TextView) convertView.findViewById(R.id.tv_profile_list_item_method)).setText(getStringFromMethodNumber(profile.getMethod()));

        return convertView;
    }

    private String getStringFromMethodNumber(int methodNumber) {
        switch (methodNumber) {
            case Request.Method.GET:
                return "GET ";
            case Request.Method.POST:
                return "POST ";
            case Request.Method.PUT:
                return "PUT ";
            case Request.Method.DELETE:
                return "DELETE ";
            default:
                return "UNKNOWN ";
        }
    }
}
