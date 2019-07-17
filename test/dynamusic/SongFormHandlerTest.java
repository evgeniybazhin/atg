package dynamusic;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.servlet.ServletException;
import java.io.IOException;

import static junit.framework.Assert.assertFalse;

public class SongFormHandlerTest {
    private SongFormHandler songFormHandler;
    
    @Mock
    private DynamoHttpServletRequest request;
    
    @Mock
    private DynamoHttpServletResponse response;

    @Before
    public void setUp() throws Exception {
        songFormHandler = new SongFormHandler();
    }

    @Test
    public void shouldPostCreateItem() throws ServletException, IOException {
        songFormHandler.postCreateItem(request, response);
        assertFalse(songFormHandler.getFormExceptions().isEmpty());
    }
}
