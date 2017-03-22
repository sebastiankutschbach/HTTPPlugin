package kutschi.de.httpplugin;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import kutschi.de.httpplugin.model.Profile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * Created by vwyx6x9 on 22.03.2017.
 */

public class ProfileTest {

    @Mock
    JSONObject jsonObject;

    private final static String DESCRIPTION = "description";
    private final static String URL = "http://url:8080";
    private final static int METHOD = 0;
    private final static String[] ZONES = new String[]{"zone1", "zone2"};

    @Test
    public void testConstructor() {
        Profile profile = new Profile(DESCRIPTION, URL, METHOD, ZONES);
        assertEquals(DESCRIPTION, profile.getDescription());
        assertEquals(URL, profile.getUrl());
        assertEquals(METHOD, profile.getMethod());
        for (int i = 0; i < ZONES.length ; i++) {
            assertEquals(ZONES[i], profile.getZones()[i]);
        }
        assertNull(profile.getContentType());
        assertNull(profile.getEnteringContent());
        assertNull(profile.getLeavingContent());
        assertNull(profile.getUsername());
        assertNull(profile.getPassword());
    }
}
