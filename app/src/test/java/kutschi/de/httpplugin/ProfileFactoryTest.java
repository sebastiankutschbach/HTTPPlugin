package kutschi.de.httpplugin;

import android.net.Uri;
import android.util.Log;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import kutschi.de.httpplugin.model.Profile;
import kutschi.de.httpplugin.model.ProfileFactory;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

/**
 * Created by vwyx6x9 on 22.03.2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class, Uri.class})
public class ProfileFactoryTest {

    private static final String ID = "myID";
    @Mock
    private Profile profileMock;

    @Before
    public void before() {
        PowerMockito.mockStatic(Log.class);
        PowerMockito.mockStatic(Uri.class);
        Mockito.when(Uri.parse(Mockito.anyString())).thenReturn(Uri.EMPTY);
    }

    @Test
    public void testAddProfile() {
        final int sizeBefore = ProfileFactory.getInstance().getProfiles().size();
        ProfileFactory.getInstance().addProfile(ID, profileMock);

        assertEquals(sizeBefore + 1, ProfileFactory.getInstance().getProfiles().size());
        assertEquals(profileMock, ProfileFactory.getInstance().getProfiles().get(ID));
    }

    @Test
    public void testRemoveProfile() {
        ProfileFactory.getInstance().addProfile(ID, profileMock);
        final int sizeBefore = ProfileFactory.getInstance().getProfiles().size();

        ProfileFactory.getInstance().removeProfile(ID);

        assertEquals(sizeBefore - 1, ProfileFactory.getInstance().getProfiles().size());
        assertNull(ProfileFactory.getInstance().getProfiles().get(ID));
    }
}
