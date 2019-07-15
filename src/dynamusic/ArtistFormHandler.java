package dynamusic;

import atg.droplet.*;
import atg.repository.*;
import atg.repository.servlet.*;
import atg.servlet.*;

public class ArtistFormHandler extends RepositoryFormHandler {

    public static final String CANNOT_DELETE_ALBUMS_BY_ARTIST = "Cannot delete albums by artist";
    public static final String NO_SONGS_MANAGER_SET = "no songs manager set";
    public static final String ARTIST_IS_NULL = "artist is null";
    public static final String PRE_DELETE_ITEM_CALLED_ITEM = "preDeleteItem called, item: ";
    private SongsManager mSongsManager;

    // Property methods

    public SongsManager getSongsManager() {
        return mSongsManager;
    }

    public void setSongsManager(SongsManager pSongsManager) {
        this.mSongsManager = pSongsManager;
    }

    /**
     * Deletes artist's albums before deleting the artist
     */
    protected void preDeleteItem(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws javax.servlet.ServletException, java.io.IOException {

        if (isLoggingDebug()) {
            logDebug(PRE_DELETE_ITEM_CALLED_ITEM + getRepositoryItem());
        }

        SongsManager songsManager = getSongsManager();
        RepositoryItem artist = getRepositoryItem();

        if (artist == null || artist.getRepositoryId() == null) {
            addDeleteAlbumsFormException(new RepositoryException(ARTIST_IS_NULL));
        } else if (songsManager != null) {
            deleteAlbumsByArtist(songsManager, artist);
        } else if (isLoggingWarning()) {
            logWarning(NO_SONGS_MANAGER_SET);
        }
    }

    /**
     * Deletes artist's albums
     */
    private void deleteAlbumsByArtist(SongsManager songsManager, RepositoryItem artist) {
        try {
            songsManager.deleteAlbumsByArtist(artist.getRepositoryId());
        } catch (RepositoryException repositoryException) {
            addDeleteAlbumsFormException(repositoryException);
        }
    }

    /**
     * Adds forms exception in case of issue
     * @param repositoryException exception thrown
     */
    private void addDeleteAlbumsFormException(RepositoryException repositoryException) {
        if (isLoggingError()) {
            logError(CANNOT_DELETE_ALBUMS_BY_ARTIST, repositoryException);
        }
        addFormException(new DropletException(CANNOT_DELETE_ALBUMS_BY_ARTIST));
    }

}