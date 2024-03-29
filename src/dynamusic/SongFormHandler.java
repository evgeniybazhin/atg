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

import atg.core.util.Null;
import atg.droplet.DropletException;

import atg.repository.RepositoryException;
import atg.repository.servlet.RepositoryFormHandler;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;


public class SongFormHandler extends RepositoryFormHandler {

    private SongsManager mSM;
    private String mAlbumId;
    private String mArtistId;
    private static final String POST_CREATE_ITEM_CALLED = "postCreateItem called, item created: ";
    private static final String CANNOT_ADD_SONG_TO_ALBUM = "Cannot add song to album";
    
    // Property methods
    
    public SongsManager getSongsManager() {
        return mSM;
    }
    
    public void setSongsManager(SongsManager pSM) {
        mSM = pSM;
    }
    
    public String getAlbumId() {
        return mAlbumId;
    }
    
    public void setAlbumId(String pAlbumId) {
        mAlbumId = pAlbumId;
    }

    public String getArtistId() {
        return mArtistId;
    }
    
    public void setArtistId(String pArtistId) {
        mArtistId = pArtistId;
    }


    protected void postCreateItem(DynamoHttpServletRequest pRequest, 
                         DynamoHttpServletResponse pResponse) 
                      throws javax.servlet.ServletException,
                              java.io.IOException {
     
       	if (isLoggingDebug())
  		logDebug(POST_CREATE_ITEM_CALLED + getRepositoryItem());

        SongsManager sm = getSongsManager();
    
        try {
            if(sm != null){
                sm.addSongToAlbum(getRepositoryId(),getAlbumId());
                sm.addArtistToSong(getRepositoryId(),getArtistId());
                sm.fireNewSongMessage(getRepositoryItem());
            }else addFormException(new DropletException(CANNOT_ADD_SONG_TO_ALBUM));
        }catch (RepositoryException e) {
           if (isLoggingError()){
               logError(CANNOT_ADD_SONG_TO_ALBUM, e);
           }
            addFormException(new DropletException(CANNOT_ADD_SONG_TO_ALBUM));
        }
   } 
}