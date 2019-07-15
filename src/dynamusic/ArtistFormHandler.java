package dynamusic;

import atg.droplet.*;
import atg.repository.*;
import atg.repository.servlet.*;
import atg.servlet.*;




public class ArtistFormHandler extends RepositoryFormHandler {

    private static final String PRE_DELETE_CALLED = "preDeleteItem called, item: ";
    private static final String CANNOT_DELETE_ALBUMS_BY_ARTIST = "Cannot delete albums by artist";
    private static final String NO_SONG_MANAGER_SET = "no songs manager set";
    SongsManager mSM;

    public SongsManager getSongsManager() {
        return mSM;
    }
    
    public void setSongsManager(SongsManager pSM) {
        mSM = pSM;
    }
    
    
    protected void preDeleteItem(DynamoHttpServletRequest pRequest, 
                         DynamoHttpServletResponse pResponse) 
                      throws javax.servlet.ServletException,
                              java.io.IOException {
     
       	if (isLoggingDebug())
  		logDebug(PRE_DELETE_CALLED + getRepositoryItem());
  	        
        SongsManager sm = getSongsManager();
        String artistid = getRepositoryItem().getRepositoryId();
   
        if (sm != null) {
             try {
                sm.deleteAlbumsByArtist(artistid);                
             }
             catch (RepositoryException re) {
                if (isLoggingError())
                   logError(CANNOT_DELETE_ALBUMS_BY_ARTIST, re);
                addFormException(new DropletException(CANNOT_DELETE_ALBUMS_BY_ARTIST));
             }
        }
        else {
           if (isLoggingWarning())
                logWarning(NO_SONG_MANAGER_SET);
        } 	  	
    }
    
}