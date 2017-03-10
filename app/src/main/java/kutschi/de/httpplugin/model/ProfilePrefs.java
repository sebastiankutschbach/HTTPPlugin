package kutschi.de.httpplugin.model;

import com.android.volley.Request;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by seb on 09.03.17.
 */

public class ProfilePrefs {

    private List<Profile> profiles = new ArrayList<>();

    public List<Profile> getProfiles() {
        return profiles;
    }

    public void addProfile(Profile profile) {
        profiles.add(profile);
    }

    public void restore() {
        final Profile profile1 = new Profile("Description A", "http://google.de", Request.Method.POST, new String[]{"ZoneA"});
        profile1.setEnteringContent("ON");
        profile1.setLeavingContent("OFF");
        profile1.setUsername("user");
        profile1.setPassword("pass");
        addProfile(profile1);

        final Profile profile2 = new Profile("Description B", "B", Request.Method.GET, new String[]{"ZoneA, ZoneB"});
        profile2.setEnteringContent("ON1");
        profile2.setLeavingContent("ON2");
        addProfile(profile2);
    }

    public void persist() {

    }
}
