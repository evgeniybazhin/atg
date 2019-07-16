package dynamusic;

import atg.droplet.DropletException;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.servlet.RepositoryFormHandler;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import javax.servlet.ServletException;
import java.io.IOException;

public class PlaylistFormHandler extends RepositoryFormHandler {

    public static final String POST_CREATE_ITEM_CALLED_ITEM = "postCreateItem method called, item created: ";
    public static final String CANNOT_ADD_PLAYLIST_TO_USER = "Cannot add playlist to user";
    public static final String ADDING_PLAYLIST_AND_ROLLBACK_FAILED = "Adding playlist failed but rollback failed too";
    public static final String HANDLE_ADD_SONG_CALLED = "handleAddSong called";
    public static final String CANNOT_ADD_SONG_TO_PLAYLIST = "Cannot add song to playlist";
    public static final String ADD_SONG_WAS_SUCCESSFUL = "Add song was successful";
    public static final String ADD_SONG_WAS_NOT_SUCCESSFUL = "Add song was not successful";
    public static final String REDIRECTING_TO = "Redirecting to ";
    public static final String PLAYLIST_IS_NULL = "Playlist is null";
    public static final String NO_PLAYLIST_MANAGER_SET = "No playlist manager set";

    private PlaylistManager playlistManager;
    private String userId;
    private String songId;
    private String addSongSuccessUrl;
    private String addSongErrorUrl;


    public PlaylistManager getPlaylistManager() {
        return playlistManager;
    }

    public void setPlaylistManager(PlaylistManager playlistManager) {
        this.playlistManager = playlistManager;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public String getAddSongSuccessUrl() {
        return addSongSuccessUrl;
    }

    public void setAddSongSuccessUrl(String addSongSuccessUrl) {
        this.addSongSuccessUrl = addSongSuccessUrl;
    }

    public String getAddSongErrorUrl() {
        return addSongErrorUrl;
    }

    public void setAddSongErrorUrl(String addSongErrorUrl) {
        this.addSongErrorUrl = addSongErrorUrl;
    }


    @Override
    protected void postCreateItem(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {
        if (isLoggingDebug()) {
            logDebug(POST_CREATE_ITEM_CALLED_ITEM + getRepositoryItem());
        }

        PlaylistManager pm = getPlaylistManager();
        RepositoryItem playlist = getRepositoryItem();

        if (playlist == null || playlist.getRepositoryId() == null) {
            addPlaylistFormException(new RepositoryException(PLAYLIST_IS_NULL));
        } else if (pm != null) {
            addPlaylistToUser(pm, getRepositoryId(), getUserId());
        } else if (isLoggingWarning()) {
            logWarning(NO_PLAYLIST_MANAGER_SET);
        }
    }


    private void addPlaylistToUser(PlaylistManager playlistManager, String playlistId, String userId) {
        try {
            playlistManager.addPlaylistToUser(getRepositoryId(), getUserId());
        } catch (RepositoryException repositoryException) {
            addPlaylistFormException(repositoryException);
            try {
                playlistManager.getTransactionManager().setRollbackOnly();
            } catch (Exception exception) {
                if (isLoggingError()) {
                    logError(ADDING_PLAYLIST_AND_ROLLBACK_FAILED, exception);
                }
            }
        }
    }


    private void addPlaylistFormException(RepositoryException repositoryException) {
        if (isLoggingError()) {
            logError(CANNOT_ADD_PLAYLIST_TO_USER, repositoryException);
        }
        addFormException(new DropletException(CANNOT_ADD_PLAYLIST_TO_USER));
    }


    public boolean handleAddSong(DynamoHttpServletRequest request, DynamoHttpServletResponse response)
            throws java.io.IOException {
        if (isLoggingDebug()) {
            logDebug(HANDLE_ADD_SONG_CALLED);
        }

        PlaylistManager pm = getPlaylistManager();
        RepositoryItem playlist = getRepositoryItem();

        if (playlist == null || playlist.getRepositoryId() == null) {
            addSongFormException(new RepositoryException(PLAYLIST_IS_NULL));
        } else if (pm != null) {
            addSongToPlaylist(pm, getRepositoryId(), getSongId());
        } else if (isLoggingWarning()) {
            logWarning(NO_PLAYLIST_MANAGER_SET);
        }

        if (getFormError()) {
            if (isLoggingDebug()) {
                logDebug(ADD_SONG_WAS_NOT_SUCCESSFUL);
            }
            if (getAddSongErrorUrl() != null) {
                if (isLoggingDebug()) {
                    logDebug(REDIRECTING_TO + getAddSongErrorUrl());
                }
                response.sendLocalRedirect(getAddSongErrorUrl(), request);
                return false;
            } else return true;
        }
        if (isLoggingDebug()) {
            logDebug(ADD_SONG_WAS_SUCCESSFUL);
        }
        if (getAddSongSuccessUrl() != null) {
            if (isLoggingDebug()) {
                logDebug(REDIRECTING_TO + getAddSongSuccessUrl());
            }
            response.sendLocalRedirect(getAddSongSuccessUrl(), request);
            return false;
        }
        return true;
    }


    private void addSongToPlaylist(PlaylistManager playlistManager, String playlistId, String songId) {
        try {
            playlistManager.addSongToPlaylist(playlistId, songId);
        } catch (RepositoryException repositoryException) {
            addSongFormException(repositoryException);
        }
    }


    private void addSongFormException(RepositoryException repositoryException) {
        if (isLoggingError()) {
            logError(CANNOT_ADD_SONG_TO_PLAYLIST, repositoryException);
        }
        addFormException(new DropletException(CANNOT_ADD_SONG_TO_PLAYLIST));
    }
}