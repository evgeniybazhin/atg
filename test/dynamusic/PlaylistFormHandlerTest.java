package dynamusic;

import atg.repository.MutableRepository;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.servlet.ServletException;
import java.io.IOException;

import static junit.framework.Assert.assertFalse;

public class PlaylistFormHandlerTest {
    private PlaylistFormHandler playlistFormHandler;

    @Mock
    private DynamoHttpServletRequest requestMock;

    @Mock
    private DynamoHttpServletResponse responseMock;

    @Before
    public void setUp(){
        playlistFormHandler = new PlaylistFormHandler();
    }

    @Test
    public void testPostCreateItemWithRepositoryItemIsNull() throws ServletException, IOException {
        playlistFormHandler.postCreateItem(requestMock, responseMock);
        assertFalse(playlistFormHandler.getFormExceptions().isEmpty());
    }

    @Test
    public void shouldHandleAddSong() throws IOException {
        playlistFormHandler.handleAddSong(requestMock, responseMock);
        assertFalse(playlistFormHandler.getFormExceptions().isEmpty());
    }
}
