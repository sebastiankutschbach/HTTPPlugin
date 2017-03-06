package kutschi.de.httpplugin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.Request;

public class HttpPluginMain extends AppCompatActivity{

    // The SharedPreferences object in which settings are stored
    private SharedPreferences mPrefs = null;
    // The name of the resulting SharedPreferences
    public static final String SHARED_PREFERENCE_NAME = HttpPluginMain.class.getSimpleName();
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private EditText urlText;

    private RadioGroup methodGroup;

    private Switch authEnabledSwitch;
    private LinearLayout authGroup;
    private EditText userNameText;
    private EditText passwordText ;

    private LinearLayout contentTypeGroup;
    private Spinner contentType;
    private EditText payloadInText;
    private EditText payloadOutText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plugin);

        preferences = getSharedPreferences(SHARED_PREFERENCE_NAME, MODE_PRIVATE);
        editor = preferences.edit();

        findViewsByIdAndRestoreValues();

        urlText.addTextChangedListener(new MyTextWatcher(editor, urlText));
        payloadInText.addTextChangedListener(new MyTextWatcher(editor, payloadInText));
        payloadOutText.addTextChangedListener(new MyTextWatcher(editor, payloadOutText));
        userNameText.addTextChangedListener(new MyTextWatcher(editor, userNameText));
        passwordText.addTextChangedListener(new MyTextWatcher(editor, passwordText));

        // Authentification Group ausblenden, wenn nicht benötigt
        authEnabledSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                authGroup.setEnabled(isChecked);
                authGroup.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                editor.putBoolean(String.valueOf(authEnabledSwitch.getId()), isChecked);
            }
        });

        // Content Group ausblenden, wenn nicht benötigt
        methodGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton childAt = (RadioButton)findViewById(checkedId);
                if (childAt.getText().equals(getString(R.string.radioButtonGet)) || childAt.getText().equals(getString(R.string.radioButtonDelete))) {
                    contentTypeGroup.setEnabled(false);
                    contentTypeGroup.setVisibility(View.GONE);
                } else{
                    contentTypeGroup.setEnabled(true);
                    contentTypeGroup.setVisibility(View.VISIBLE);
                }
                if(childAt.getText().equals(getString(R.string.radioButtonGet))) {
                    editor.putInt(String.valueOf(methodGroup.getId()), Request.Method.GET);
                } else if(childAt.getText().equals(getString(R.string.radioButtonPost))){
                    editor.putInt(String.valueOf(methodGroup.getId()), Request.Method.POST);
                } else if(childAt.getText().equals(getString(R.string.radioButtonPut))){
                    editor.putInt(String.valueOf(methodGroup.getId()), Request.Method.PUT);
                } else if(childAt.getText().equals(getString(R.string.radioButtonDelete))){
                    editor.putInt(String.valueOf(methodGroup.getId()), Request.Method.DELETE);
                }
            }
        });

        contentType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editor.putInt(String.valueOf(R.id.spinnerContentType), position);
                editor.putString(String.valueOf(R.id.spinnerContentType) + "/text", String.valueOf(((TextView)view).getText()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                editor.putInt(String.valueOf(R.id.spinnerContentType), 0);
                editor.putString(String.valueOf(R.id.spinnerContentType) + "/text", null);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        editor.commit();
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_info) {
            Intent info = new Intent(this, Info.class);
            startActivity(info);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void findViewsByIdAndRestoreValues() {
        urlText = (EditText) findViewById(R.id.txtUrl);
        urlText.setText(preferences.getString(String.valueOf(urlText.getId()), ""));

        authEnabledSwitch = (Switch)findViewById(R.id.swtAuthEnabled);
        authEnabledSwitch.setChecked(preferences.getBoolean(String.valueOf(authEnabledSwitch.getId()), false));

        authGroup = (LinearLayout)findViewById(R.id.authGroup);

        userNameText = (EditText)findViewById(R.id.txtUsername);
        userNameText.setText(preferences.getString(String.valueOf(userNameText.getId()), ""));

        passwordText = (EditText)findViewById(R.id.txtPassword);
        passwordText.setText(preferences.getString(String.valueOf(passwordText.getId()), ""));

        payloadInText = (EditText) findViewById(R.id.txtPayloadZoneIn);
        payloadInText.setText(preferences.getString(String.valueOf(payloadInText.getId()), ""));

        payloadOutText = (EditText) findViewById(R.id.txtPayloadZoneOut);
        payloadOutText.setText(preferences.getString(String.valueOf(payloadOutText.getId()), ""));

        contentTypeGroup = (LinearLayout) findViewById(R.id.contentTypeGroup);

        contentType = (Spinner) findViewById(R.id.spinnerContentType);
        contentType.setSelection(preferences.getInt(String.valueOf(R.id.spinnerContentType), 0));

        methodGroup = (RadioGroup)findViewById(R.id.rgMethod);
        int idxOfSelectedRadioButton = preferences.getInt(String.valueOf(methodGroup.getId()), 0);
        if(idxOfSelectedRadioButton != 0) {
            RadioButton button = (RadioButton) methodGroup.getChildAt(idxOfSelectedRadioButton);
            button.setChecked(true);
            // FIXME: notify listener on restore
        }
    }

    class MyTextWatcher implements TextWatcher {

        private final EditText source;
        private final SharedPreferences.Editor editor;

        public MyTextWatcher(SharedPreferences.Editor editor, EditText source) {
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
            editor.putString(String.valueOf(source.getId()), s.toString());
        }
    }
}
