package dynamusic;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertFalse;

/**
 * Class for testing ArtistFormHandler
 */
public class ArtistFormHandlerTest {

    private ArtistFormHandler artistFormHandler;

    @Mock
    private DynamoHttpServletRequest requestMock;

    @Mock
    private DynamoHttpServletResponse responseMock;

    @Before
    public void setUp() throws Exception {
        artistFormHandler = new ArtistFormHandler();
    }

    @Test
    public void shouldPreDeleteItemWhenRepositoryItemIsNull() throws Exception {
        artistFormHandler.preDeleteItem(requestMock, responseMock);
        assertFalse(artistFormHandler.getFormExceptions().isEmpty());
    }

}