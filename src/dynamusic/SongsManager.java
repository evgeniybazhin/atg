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

/* -------------------------------------------
 * SongsManager
 *
 * Sample class for Dynamusic exercises.
 * 
 * Developing ATG Applications, Part I
 *
 * @author Diana Carroll
 * Last modified: June 12, 2003
 *--------------------------------------------
 */

import atg.repository.Repository;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.RepositoryException;

import atg.repository.rql.RqlStatement;

import javax.transaction.TransactionManager;
import javax.transaction.SystemException;

import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;

import java.util.Collection;


public class SongsManager extends atg.nucleus.GenericService {

  // ----------------------------------------
  // [1] MEMBER VARIABLES
  
  private TransactionManager mTransactionManager = null;
  private Repository mRepository = null;
  private Repository mUserRepository = null;
  private SongMessageSource mSongMessageSource = null;

  // ----------------------------------------
  // [2] PROPERTIES
  
  // -- Property: TransactionManager --
  
  /**
   * Sets the transactionManager property, used to manage transactions 
   * in the handler. 
   **/
  public void setTransactionManager(TransactionManager pTransactionManager) {
    mTransactionManager = pTransactionManager;
  }
  
  /**
   * Returns the transactionManager property, used to manage transactions 
   * in the handler. 
   **/
  public TransactionManager getTransactionManager() {
    return mTransactionManager;
  }

  // -- Property: repository --
  
  /**
   * Sets the repository property, which points to the repository
   * component for the album and songs repository. 
   **/
  public void setRepository(Repository pRepository) {
    mRepository = pRepository;
  }
  
  /**
   * Returns the repository property, which points to the repository
   * component for the album and songs repository. 
   **/
  public Repository getRepository() {
    return mRepository;
  }

  // -- Property: userRepository --
  
  /**
   * Sets the userRepository property, which points to the repository
   * component for users (the profile repository usually) 
   **/
  public void setUserRepository(Repository pUserRepository) {
    mUserRepository = pUserRepository;
  }
  
  /**
   * Returns the userRepository property, which points to the repository
   * component for users (the profile repository usually) 
   **/
  public Repository getUserRepository() {
    return mUserRepository;
  }

  // -- Property: songMessageSource --
  
  /**
   * Sets the songMessageSource property, which points to the
   * component to fire a NewSongMessage event. 
   **/
  public void setSongMessageSource(SongMessageSource pSongMessageSource) {
    mSongMessageSource = pSongMessageSource;
  }

  /**
   * Returns the songMessageSource property, which points to the
   * component to fire a NewSongMessage event. 
   **/
  public SongMessageSource getSongMessageSource() {
    return mSongMessageSource;
  }

  public String createArtistFromUser(String pUserid) throws RepositoryException {
   
      if (isLoggingDebug()) 
          logDebug("creating new artist from user item " + pUserid);

      MutableRepository mutRepos = (MutableRepository) getRepository();
      Repository userRepos = getUserRepository();
      
      RepositoryItem user = userRepos.getItem(pUserid, "user");
      String username = (String)user.getPropertyValue("firstName") + " " + user.getPropertyValue("lastName");
      String description = (String)user.getPropertyValue("info");
      Boolean shareProfile = (Boolean)user.getPropertyValue("shareProfile");
      RepositoryItem artistItem = null;

      RqlStatement finduserRQL = RqlStatement.parseRqlStatement("name = ?0");
      RepositoryView artistView = mutRepos.getView("artist");
      Object rqlparams[] = new Object[1];
      rqlparams[0] = username;
      RepositoryItem [] artistList = 
            finduserRQL.executeQuery (artistView, rqlparams);

      if (artistList != null) {
           if (isLoggingDebug()) logDebug("artists found for this user:" + artistList.length + " (using artist: " + artistList[0] + ")");
           artistItem = artistList[0];
      }      
      else {
        try {
         MutableRepositoryItem mutArtistItem = mutRepos.createItem("artist");
         mutArtistItem.setPropertyValue("name", username);
            mutArtistItem.setPropertyValue("description",description);
         mutRepos.addItem(mutArtistItem);
         artistItem = mutArtistItem;
         if (isLoggingDebug()) 
             logDebug("no artists found for this user, new artist " + mutArtistItem + " created.");

        }
        catch (RepositoryException e) {
         if (isLoggingError()) {
            logError(e);
         }
         throw e;
        }
      }
      return artistItem.getRepositoryId();        
  }

  public void addArtistToSong(String pSongid, String pArtistid) throws RepositoryException {
      if (isLoggingDebug()) 
          logDebug("adding song " + pSongid + " to artist " + pArtistid);

      MutableRepository mutRepos = (MutableRepository)getRepository();
      
      try {
          MutableRepositoryItem mutSongItem = mutRepos.getItemForUpdate(pSongid,"song");  
          RepositoryItem artistItem = mutRepos.getItem(pArtistid,"artist");
          if (isLoggingDebug()) 
             logDebug("adding song " + mutSongItem + " to artist " + artistItem);
          mutSongItem.setPropertyValue("artist",artistItem);
          mutRepos.updateItem(mutSongItem);
      
      }
      catch(RepositoryException e) {
         if (isLoggingError()) {
             logError(e);
         }
         throw e;
      }
      
 }      

  public void addSongToAlbum(String pSongid, String pAlbumid) throws RepositoryException {
      if (isLoggingDebug()) 
          logDebug("adding song " + pSongid + " to album " + pAlbumid);

      MutableRepository mutRepos = (MutableRepository)getRepository();
      
      try {
          MutableRepositoryItem album = mutRepos.getItemForUpdate(pAlbumid,"album");  
          RepositoryItem song = mutRepos.getItem(pSongid,"song");
          if (isLoggingDebug()) 
             logDebug("adding song " + song + " to album " + album);
          Collection songlist = (Collection)album.getPropertyValue("songList");
          songlist.add(song);
          mutRepos.updateItem(album);
      
      }
      catch(RepositoryException e) {
         if (isLoggingError()) {
             logError(e);
         }
         throw e;
      }
      
 }      

   public void deleteAlbumsByArtist(String pArtistId) throws RepositoryException {

      if (isLoggingDebug()) 
           logDebug("deleting albums by artist id " + pArtistId);
       
      MutableRepository mutRepos = (MutableRepository)getRepository();

      try {
          TransactionDemarcation td = new TransactionDemarcation();
          td.begin(getTransactionManager(), td.REQUIRED);
          try {
             RqlStatement findalbumsRQL = RqlStatement.parseRqlStatement("artist.id = ?0");
             RepositoryView albumView = mutRepos.getView("album");
             Object rqlparams[] = new Object[1];
             rqlparams[0] = pArtistId;
             RepositoryItem [] albumList = 
                findalbumsRQL.executeQuery (albumView, rqlparams);

             if (isLoggingDebug())
                 logDebug("found albums for artist: " + albumList);
             if (albumList != null) {
                 for (int i = 0; i<albumList.length; i++) {
                     if (isLoggingDebug()) 
                         logDebug("deleting album " + albumList[i]);
                    mutRepos.removeItem(albumList[i].getRepositoryId(), "album");
                 }
             }
          }catch (Exception e) {
             if (isLoggingError())
                 logError("Exception occured trying to remove albums", e); 
             try {
                 getTransactionManager().setRollbackOnly();
             }
             catch (SystemException se) {
                 if (isLoggingError())
                     logError("Unable to set rollback for transaction", se);
             }
          }finally {
              td.end();
          } 
      }
      catch (TransactionDemarcationException e) {
         if (isLoggingError()) 
             logError("creating transaction demarcation failed, no albums deleted", e);
      }
      
   }
   
  public void fireNewSongMessage(RepositoryItem pNewSong) {
  
      SongMessageSource source = getSongMessageSource();
      
      if (source != null) {
          if (isLoggingDebug())
              logDebug("Found message source, firing message now");

          try {
             source.fireMessage(
        	pNewSong.getRepositoryId(),
        	(String) pNewSong.getPropertyValue("songGenre"),
        	(String) pNewSong.getPropertyValue("title"));
          }
          catch (Exception e) {
             if (isLoggingError())
                 logError("unable to fire new song message",e);
          }
      }
      else {
         if (isLoggingDebug())
            logDebug("No SongMessageSource set, no message sent");
      }
  }

  public SongsManager() {}
}
