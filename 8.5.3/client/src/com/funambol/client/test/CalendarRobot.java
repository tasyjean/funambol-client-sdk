/*
 * Funambol is a mobile platform developed by Funambol, Inc. 
 * Copyright (C) 2010 Funambol, Inc.
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

package com.funambol.client.test;

import com.funambol.client.test.*;

import com.funambol.syncml.spds.SyncItem;
import com.funambol.util.StringUtil;

public abstract class CalendarRobot extends Robot {
   
    private static final String TAG_LOG = "CalendarRobot";

    public void saveEventOnServer(CheckSyncClient client) throws Throwable {

    }

    public void deleteEventOnServer(String summary, CheckSyncClient client) throws Throwable {
        CheckSyncSource source = client.getSyncSource(
                CheckSyncClient.SOURCE_NAME_CALENDAR);
        String itemKey = findEventKeyOnServer(summary, client);
        source.deleteItemFromOutside(itemKey);
        
    }

    public void deleteAllEventsOnServer(CheckSyncClient client) throws Throwable {
        CheckSyncSource source = client.getSyncSource(
                CheckSyncClient.SOURCE_NAME_CALENDAR);
        source.deleteAllFromOutside();
    }


    public abstract void createEmptyEvent() throws Throwable;
    public abstract void setEventField(String field, String value) throws Throwable;

    public abstract void setEventRecurrenceField(String recField, String value) throws Throwable;

    public abstract void loadEvent(String summary) throws Throwable;
    public abstract void saveEvent() throws Throwable;
    public abstract void deleteEvent(String summary) throws Throwable;
    public abstract void deleteAllEvents() throws Throwable;

    public abstract void checkNewEvent(String summary,
            CheckSyncClient client, boolean checkContent) throws Throwable;

    public abstract void checkUpdatedEvent(String summary,
            CheckSyncClient client, boolean checkContent) throws Throwable;

    public abstract void checkDeletedEvent(String summary,
            CheckSyncClient client) throws Throwable;

    public abstract void checkNewEventOnServer(String summary,
            CheckSyncClient client, boolean checkContent) throws Throwable;

    public abstract void checkUpdatedEventOnServer(String summary,
            CheckSyncClient client, boolean checkContent) throws Throwable;

    public abstract void checkDeletedEventOnServer(String summary,
            CheckSyncClient client) throws Throwable;

    public abstract void loadEventOnServer(String summary,
            CheckSyncClient client) throws Throwable;

    protected abstract String findEventKeyOnServer(String summary,
            CheckSyncClient client) throws Throwable;
}
