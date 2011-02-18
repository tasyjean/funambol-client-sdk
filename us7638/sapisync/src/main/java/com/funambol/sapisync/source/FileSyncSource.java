/*
 * Funambol is a mobile platform developed by Funambol, Inc. 
 * Copyright (C) 2011 Funambol, Inc.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License version 3 as published by
 * the Free Software Foundation with the addition of the following permission 
 * added to Section 15 as permitted in Section 7(a): FOR ANY PART OF THE COVERED
 * WORK IN WHICH THE COPYRIGHT IS OWNED BY FUNAMBOL, FUNAMBOL DISCLAIMS THE 
 * WARRANTY OF NON INFRINGEMENT  OF THIRD PARTY RIGHTS.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License 
 * along with this program; if not, see http://www.gnu.org/licenses or write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301 USA.
 * 
 * You can contact Funambol, Inc. headquarters at 643 Bair Island Road, Suite 
 * 305, Redwood City, CA 94063, USA, or at email address info@funambol.com.
 * 
 * The interactive user interfaces in modified source and object code versions
 * of this program must display Appropriate Legal Notices, as required under
 * Section 5 of the GNU Affero General Public License version 3.
 * 
 * In accordance with Section 7(b) of the GNU Affero General Public License
 * version 3, these Appropriate Legal Notices must retain the display of the
 * "Powered by Funambol" logo. If the display of the logo is not reasonably 
 * feasible for technical reasons, the Appropriate Legal Notices must display
 * the words "Powered by Funambol". 
 */

package com.funambol.sapisync.source;

import java.util.Enumeration;
import java.util.Vector;
import java.util.Date;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.funambol.sync.SyncItem;
import com.funambol.sync.TwinDetectionSource;
import com.funambol.sync.SourceConfig;
import com.funambol.sync.SyncConfig;
import com.funambol.sync.SyncException;
import com.funambol.sync.SyncAnchor;
import com.funambol.sync.SyncSource;
import com.funambol.sync.client.ChangesTracker;

import com.funambol.platform.FileAdapter;
import com.funambol.util.Log;
import com.funambol.util.Base64;

public class FileSyncSource extends JSONSyncSource implements TwinDetectionSource {

    private static final String TAG_LOG = "FileSyncSource";

    protected String directory;
    protected String extensions[] = {};
    
    //------------------------------------------------------------- Constructors

    /**
     * FileSyncSource constructor: initialize source config
     */
    public FileSyncSource(SourceConfig config, SyncConfig syncConfig, ChangesTracker tracker, String directory) {

        super(config, syncConfig, tracker);
        this.directory = directory;
    }

    /**
     * Twin detection implementation
     * @param item
     * @return
     */
    public SyncItem findTwin(SyncItem item) {

        if(item instanceof JSONSyncItem) {

            JSONFileObject json = ((JSONSyncItem)item).getJSONFileObject();
            String fileName = json.getName();

            // Does this existing in our directory?
            if (Log.isLoggable(Log.DEBUG)) {
                Log.debug(TAG_LOG, "Checking for twin for: " + fileName);
            }
            FileAdapter fa = null;
            try {
                fa = new FileAdapter(directory + fileName);
                if (fa.exists()) {
                    if (Log.isLoggable(Log.DEBUG)) {
                        Log.debug(TAG_LOG, "Twin found");
                    }
                    return item;
                }
                fa.close();
            } catch (IOException ioe) {
                Log.error(TAG_LOG, "Cannot check for twins", ioe);
            } finally {
                if (fa != null) {
                    try {
                        fa.close();
                    } catch (IOException ioe) {
                    }
                }
            }
        }
        // No twin found
        return null;
    }

    protected Enumeration getAllItemsKeys() throws SyncException {
        if (Log.isLoggable(Log.TRACE)) {
            Log.trace(TAG_LOG, "getAllItemsKeys");
        }
        // Scan the briefcase directory and return all keys
        try {
            FileAdapter dir = new FileAdapter(directory);
            Enumeration files = dir.list(false);
            dir.close();
            // We use the full file name as key, so we need to scan all the
            // items and prepend the directory
            Vector keys = new Vector();
            while(files.hasMoreElements()) {
                String file = (String)files.nextElement();
                if (filterFile(file)) {
                    keys.addElement(directory + file);
                }
            }
            return keys.elements();
        } catch (Exception e) {
            Log.error(TAG_LOG, "Cannot get list of files", e);
            throw new SyncException(SyncException.CLIENT_ERROR, e.toString());
        }
    }

    protected SyncItem getItemContent(SyncItem item) throws SyncException {
        return item;
    }


    protected OutputStream getDownloadOutputStream(String name, long size, boolean isUpdate, boolean isThumbnail)
    throws IOException {

        // TODO FIXME: hanlde the resume
        FileAdapter file = new FileAdapter(directory + name);
        return file.openOutputStream();
    }


    protected void deleteAllItems()
    {
        if (Log.isLoggable(Log.TRACE)) {
            Log.trace(TAG_LOG, "removeAllItems");
        }
        // Scan the briefcase directory and return all keys
        try {
            FileAdapter dir = new FileAdapter(directory);
            Enumeration files = dir.list(false);
            dir.close();
            // We use the full file name as key, so we need to scan all the
            // items and prepend the directory
            while(files.hasMoreElements()) {
                String fileName = (String)files.nextElement();
                FileAdapter file = new FileAdapter(directory + fileName);
                file.delete();
                file.close();
            }

            //at the end, empty the tracker
            tracker.reset();
        }
        catch (Exception e) {
            throw new SyncException(SyncException.CLIENT_ERROR, e.toString());
        }
    }

    public void setSupportedExtensions(String[] extensions) {
        this.extensions = extensions;
    }

    /**
     * Return whether a given filename is filtered by the SyncSource.
     * @param filename
     * @return true if the given filename is actually filtered by the SyncSource.
     */
    public boolean filterFile(String name) {
        if (extensions == null || extensions.length == 0) {
            return true;
        }
        name = name.toLowerCase();
        for(int i=0;i<extensions.length;++i) {
            String ext = extensions[i].toLowerCase();
            if (name.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }
}

