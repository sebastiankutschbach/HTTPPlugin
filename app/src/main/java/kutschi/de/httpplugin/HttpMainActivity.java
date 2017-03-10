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

import com.android.volley.Request;

import java.util.List;

import kutschi.de.httpplugin.model.Profile;
import kutschi.de.httpplugin.model.ProfilePrefs;

/**
 * Created by seb on 09.03.17.
 */

public class HttpMainActivity extends AppCompatActivity {

    private ProfilePrefs profilePrefs = new ProfilePrefs();
    private ListView profileListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        profilePrefs.restore();

        // List View
        profileListView = (ListView) findViewById(R.id.profileListView);
        ProfileListAdapter adapter = new ProfileListAdapter(this, profilePrefs.getProfiles());
        profileListView.setAdapter(adapter);
        profileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Empty intent for creation
                Intent intent = new Intent(getApplicationContext(), HttpProfileActivity.class);
                final Profile profile = (Profile) parent.getAdapter().getItem(position);
                intent.putExtra(Profile.class.getName(), profile);
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
    protected void onPause() {
        super.onPause();
        profilePrefs.persist();
    }
}

@TargetApi(Build.VERSION_CODES.M)
class ProfileListAdapter extends BaseAdapter {

    private final Context context;
    private final List<Profile> profiles;
    private final LayoutInflater layoutInflater;

    ProfileListAdapter(Context context, @NonNull List<Profile> profiles) {
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
        return profiles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Profile profile = (Profile) getItem(position);
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
