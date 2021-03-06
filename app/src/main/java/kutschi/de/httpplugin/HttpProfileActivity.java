package kutschi.de.httpplugin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import kutschi.de.httpplugin.model.Profile;
import kutschi.de.httpplugin.model.ProfileFactory;

public class HttpProfileActivity extends AppCompatActivity {

    private static final String TAG = HttpProfileActivity.class.getName();

    private String id;
    private Profile profile;

    private EditText descriptionText;
    private EditText urlText;

    private GridLayout glZones;

    private RadioGroup methodGroup;
    private Switch authEnabledSwitch;
    private LinearLayout authGroup;
    private EditText userNameText;

    private EditText passwordText;
    private LinearLayout contentTypeGroup;
    private Spinner contentType;
    private EditText payloadInText;
    private EditText payloadOutText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        id = getIntent().getStringExtra("id");
        profile = (Profile) getIntent().getSerializableExtra(Profile.class.getName());
        Log.i(TAG, "onCreate: Restoring profile from intent");
        if (profile == null) {
            Log.d(TAG, "onCreate: no profile was found, creating a new (empty) one");
            profile = new Profile("New Profile", "", Request.Method.GET, new String[0]);
        }
        findViewsByIdAndRestoreValues(profile);


        // Authentification Group ausblenden, wenn nicht benötigt
        authEnabledSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i(TAG, "onCheckedChanged: Authentification changed to " + (isChecked ? "ON" : "OFF"));
                authGroup.setEnabled(isChecked);
                setVisibilityOfAuthentificationGroup(isChecked);
            }
        });

        // Content Group ausblenden, wenn nicht benötigt
        methodGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.i(TAG, "onCheckedChanged: HTTP method was changed. Updating visibility of content group");
                RadioButton checkedRadioButton = (RadioButton) group.findViewById(group.getCheckedRadioButtonId());
                setVisibilityOfContentType(checkedRadioButton);
            }
        });

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab_profile_save);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Save Button was pressed.");
                profile.setDescription(descriptionText.getText().toString());
                profile.setUrl(urlText.getText().toString());
                for (int idx = 0; idx < 4; idx++) {
                    if (((RadioButton) methodGroup.getChildAt(idx)).isChecked()) {
                        profile.setMethod(idx);
                        break;
                    }
                }

                List<String> activeZoneNames = new ArrayList<>();
                for (int idx = 0; idx < glZones.getChildCount(); idx++) {
                    final CheckBox childAt = (CheckBox) glZones.getChildAt(idx);
                    if (childAt.isChecked()) {
                        activeZoneNames.add(String.valueOf(childAt.getText()));
                    }
                }
                profile.setZones(activeZoneNames.toArray(new String[activeZoneNames.size()]));

                if (authEnabledSwitch.isChecked()) {
                    profile.setUsername(userNameText.getText().toString());
                    profile.setPassword(passwordText.getText().toString());
                }
                if (profile.getMethod() == Request.Method.PUT || profile.getMethod() == Request.Method.POST) {
                    profile.setContentType(contentType.getSelectedItem().toString());
                    profile.setEnteringContent(payloadInText.getText().toString());
                    profile.setLeavingContent(payloadOutText.getText().toString());
                }

                Log.d(TAG, "onClick: Adding profile to the list");
                id = ProfileFactory.getInstance().addProfile(id, profile);
                try {
                    Log.d(TAG, "onClick: Persisting profile");
                    ProfileFactory.getInstance().persist();
                    Log.d(TAG, "onClick: Persisting successful");
                    Toast.makeText(getApplicationContext(), getString(R.string.succesful_saved), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    Log.e(TAG, "onClick: Persisting failed. ExceptionMessage: " + e.getLocalizedMessage(), e);
                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }

    private void setVisibilityOfAuthentificationGroup(boolean isChecked) {
        authGroup.setVisibility(isChecked ? View.VISIBLE : View.GONE);
    }

    private void setVisibilityOfContentType(RadioButton checkedRadioButton) {
        Log.d(TAG, "setVisibilityOfContentType: " + checkedRadioButton.getText() + " was chosen");
        if (checkedRadioButton.getText().equals(getString(R.string.radioButtonGet)) || checkedRadioButton.getText().equals(getString(R.string.radioButtonDelete))) {
            contentTypeGroup.setEnabled(false);
            contentTypeGroup.setVisibility(View.GONE);
            Log.d(TAG, "setVisibilityOfContentType: Content type group set to invisible.");
        } else {
            contentTypeGroup.setEnabled(true);
            contentTypeGroup.setVisibility(View.VISIBLE);
            Log.d(TAG, "setVisibilityOfContentType: Content type group set to visible.");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_plugin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int itemId = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (itemId == R.id.action_info) {
            Intent info = new Intent(this, Info.class);
            startActivity(info);
            return true;
        } else if (itemId == R.id.action_delete) {
            Log.i(TAG, "onOptionsItemSelected: Delete menu entry was pressed. Removing profile from list");
            ProfileFactory.getInstance().removeProfile(id);
            try {
                Log.d(TAG, "onOptionsItemSelected: Persisting list with profile removed.");
                ProfileFactory.getInstance().persist();
            } catch (JSONException e) {
                Log.e(TAG, "onOptionsItemSelected: Persisting failed. Message: " + e.getLocalizedMessage(), e);
            }
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void findViewsByIdAndRestoreValues(Profile profile) {
        descriptionText = (EditText) findViewById(R.id.txtDescription);
        descriptionText.setText(profile.getDescription());

        urlText = (EditText) findViewById(R.id.txtUrl);
        urlText.setText(profile.getUrl());

        glZones = (GridLayout) findViewById(R.id.GlZones);
        final List<String> zoneNames = ProfileFactory.getInstance().getZoneNames();
        final List<String> zones = Arrays.asList(profile.getZones());
        for (String zoneName : zoneNames) {
            final CheckBox checkBox = new CheckBox(this);
            checkBox.setText(zoneName);
            checkBox.setChecked(zones.contains(zoneName));
            glZones.addView(checkBox);
        }

        final String username = profile.getUsername();
        final String password = profile.getPassword();
        authEnabledSwitch = (Switch) findViewById(R.id.swtAuthEnabled);
        authEnabledSwitch.setChecked((username != null && !username.isEmpty()) || (password != null && !password.isEmpty()));

        authGroup = (LinearLayout) findViewById(R.id.authGroup);
        setVisibilityOfAuthentificationGroup(authEnabledSwitch.isChecked());

        userNameText = (EditText) findViewById(R.id.txtUsername);
        userNameText.setText(profile.getUsername());

        passwordText = (EditText) findViewById(R.id.txtPassword);
        passwordText.setText(profile.getPassword());

        payloadInText = (EditText) findViewById(R.id.txtPayloadZoneIn);
        payloadInText.setText(profile.getEnteringContent());

        payloadOutText = (EditText) findViewById(R.id.txtPayloadZoneOut);
        payloadOutText.setText(profile.getLeavingContent());

        contentTypeGroup = (LinearLayout) findViewById(R.id.contentTypeGroup);

        contentType = (Spinner) findViewById(R.id.spinnerContentType);
        final String contentTypeString = profile.getContentType();
        for (int i = 0; i < contentType.getChildCount(); i++) {
            String text = (String) contentType.getAdapter().getItem(0);
            if (contentTypeString.equals(text)) {
                contentType.setSelection(i);
            }
        }

        methodGroup = (RadioGroup) findViewById(R.id.rgMethod);
        RadioButton button;
        switch (profile.getMethod()) {
            default:
            case Request.Method.GET:
                button = ((RadioButton) methodGroup.getChildAt(0));
                break;
            case Request.Method.POST:
                button = ((RadioButton) methodGroup.getChildAt(1));
                break;
            case Request.Method.PUT:
                button = ((RadioButton) methodGroup.getChildAt(2));
                break;
            case Request.Method.DELETE:
                button = ((RadioButton) methodGroup.getChildAt(3));
                break;
        }
        button.setChecked(true);
        setVisibilityOfContentType(button);
    }

    private class MyTextWatcher implements TextWatcher {

        private final Pattern URL_PATTERN = Pattern.compile("http(s)*://.+(:/d+)*(/.+)*");
        private final EditText source;
        private final SharedPreferences.Editor editor;

        MyTextWatcher(SharedPreferences.Editor editor, EditText source) {
            this.editor = editor;
            this.source = source;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (source.getId() == R.id.txtUrl) {
                if (!URL_PATTERN.matcher(s.toString()).matches()) {
                    source.setError(getString(R.string.url_pattern_not_matched));
                } else {
                    source.setError(null);
                }
            } else if (source.getId() == R.id.txtUsername || source.getId() == R.id.txtPassword) {
                if (s.toString().isEmpty()) {
                    source.setError(getString(R.string.credentials_empty));
                } else {
                    source.setError(null);
                }
            }
            editor.putString(String.valueOf(source.getId()), s.toString());
        }
    }
}
