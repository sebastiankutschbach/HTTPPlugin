package kutschi.de.httpplugin;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class Info extends AppCompatActivity  {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);
        try {
            PackageInfo pi = getPackageManager().getPackageInfo("de.egi.geofence.geozone.plugin.example", PackageManager.GET_CONFIGURATIONS);
			String v = pi.versionName;
            setTitle("HttpPlugin " + v);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
		}
    }
}