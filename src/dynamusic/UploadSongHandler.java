/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2003 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution ofthis
 * work may be made except in accordance with a valid license
 * agreement from Art Technology Group.  This notice must be
 * included on all copies, modifications and derivatives of this
 * work.
 *
 * Art Technology Group (ATG) MAKES NO REPRESENTATIONS OR WARRANTIES
 * ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ATG SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * "Dynamo" is a trademark of Art Technology Group, Inc.
 </ATGCOPYRIGHT>*/

package dynamusic;

import atg.repository.RepositoryException;
import atg.repository.servlet.RepositoryFormHandler;

import atg.droplet.DropletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import java.io.Serializable;


public class UploadSongHandler extends RepositoryFormHandler implements Serializable {

    private static final String POST_CREATE_ITEM_CALLED = "postCreateItem called, item created: ";
    private static final String NO_SONGS_MANAGER_SET = "no songs manager set";
    private static final String CANNOT_ADD_SONG_TO_ARTIST = "Cannot add song to artist";
    private static final String UNABLE_TO_ADD_ARTIST_FOR_SONG = "unable to add artist for song";
    private static final String ADDING_SONG_FAILED_BUT_ROLLBACK = "Adding song failed, but rollback failed too";
    SongsManager mSM;
    String mUserid;
        
    public SongsManager getSongsManager() { return mSM; }
    public void setSongsManager(SongsManager pSM) { mSM = pSM; }

    public String getUserid() { return mUserid; }
    public void setUserid(String pUserid) { mUserid = pUserid; }
    
                             
    protected void postCreateItem(DynamoHttpServletRequest pRequest, 
                         DynamoHttpServletResponse pResponse) 
                      throws javax.servlet.ServletException,
                              java.io.IOException {
     
       	if (isLoggingDebug())
  		logDebug(POST_CREATE_ITEM_CALLED + getRepositoryItem());
  	        

        SongsManager sm = getSongsManager();
        String userid = getUserid();
        String artistid = null;
        String songid = getRepositoryItem().getRepositoryId();
   
        if (sm != null) {
             addSongToArtist(artistid, userid, songid, sm);
        }
        else {
           if (isLoggingWarning())
                logWarning(NO_SONGS_MANAGER_SET);
        }
    }

    private void addSongToArtist(String artistid, String userid, String songid, SongsManager sm){
        try {
            artistid = sm.createArtistFromUser(userid);
            sm.addArtistToSong(songid,artistid);
        }
        catch (RepositoryException re) {
            if (isLoggingError())
                logError(CANNOT_ADD_SONG_TO_ARTIST, re);
            addFormException(new DropletException(UNABLE_TO_ADD_ARTIST_FOR_SONG));
            try {
                sm.getTransactionManager().setRollbackOnly();
            }
            catch(Exception e) {
                if (isLoggingError())
                    logError(ADDING_SONG_FAILED_BUT_ROLLBACK, e);
            }
        }
    }
}
