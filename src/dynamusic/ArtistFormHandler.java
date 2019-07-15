package dynamusic;

import atg.droplet.*;
import atg.repository.*;
import atg.repository.servlet.*;
import atg.servlet.*;




public class ArtistFormHandler extends RepositoryFormHandler {

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
  		logDebug("preDeleteItem called, item: " + getRepositoryItem());
  	        
        SongsManager sm = getSongsManager();
        String artistid = getRepositoryItem().getRepositoryId();
   
        if (sm != null) {
             try {
                sm.deleteAlbumsByArtist(artistid);                
             }
             catch (RepositoryException re) {
                if (isLoggingError())
                   logError("Cannot delete albums by artist", re);
                addFormException(new DropletException("Cannot delete albums by artist"));
             }
        }
        else {
           if (isLoggingWarning())
                logWarning("no songs manager set");
        } 	  	
    }
    
}