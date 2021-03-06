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

package com.funambol.client.test.basic;

/**
 * This component lists all the commands available in the Android automatic test
 * scripting language.
 */
public interface BasicUserCommands {

    /**
     * This is the source name to use for contacts related commands.
     */
    public static final String SOURCE_NAME_CONTACTS = "Contacts";

    /**
     * This is the source name to use for calendar related commands.
     */
    public static final String SOURCE_NAME_CALENDAR = "Calendar";

    /**
     * This is the source name to use for pictures related commands.
     */
    public static final String SOURCE_NAME_PICTURES = "Pictures";

    /**
     * This is the source name to use for videos related commands.
     */
    public static final String SOURCE_NAME_VIDEOS   = "Videos";

    /**
     * This is the source name to use for files related commands.
     */
    public static final String SOURCE_NAME_FILES    = "Files";

    /**
     * This instruction must be the first one at the beginning of a test.
     * For a single test script there is only one of such instruction, while a 
     * multi test script may contain several instances.
     *
     * @param description is the description of the test as it will be reported in the final
     *                    summary.
     * @param name is the test name. This can be used for filtering out tests.
     * If a test must always be executed, its name must be *
     *
     * @param source is the source this test belongs to. Possible values are
     * contact, calendar, picture, video and file. If a test does not belong to
     * any source, then this value must be *
     *
     * @param direction is the test direction. c2s stands for client to server,
     * s2c for server to client. If a test is bidirectional, this value must be
     * *
     *
     * @param locality is the test locality. local mean the test does not need a
     * remote server. e2e means it does require it.
     *
     * @example BeginTest("Contact01","sync01","contact","c2s","e2e");
     */
    public static final String BEGIN_TEST_COMMAND = "BeginTest";

    /**
     * Terminates the commands of a test.
     *
     * @example EndTest();
     */
    public static final String END_TEST_COMMAND = "EndTest";

    /**
     * This command includes another script file.
     *
     * @param name is the script name. If this is an URL, then the script is
     *             fetched from that location, otherwise the base url if the main script is
     *             added as prefix to this name
     *
     * @example Include("deleteall.txt");
     */
    public static final String INCLUDE_COMMAND = "Include";
    
    /**
     * This command starts the main application. When the test starts the main
     * application is not started until the script triggers this command.
     * This instruction starts the FunambolClient Activity, so it is recommended
     * to run the waitForActivity command in order to verify that it is started
     * correctly.
     *
     * @example StartMainApp();
     */
    public static final String START_MAIN_APP_COMMAND = "StartMainApp";

    /**
     * This command stops the main application. 
     *
     * @example CloseMainApp();
     */
    public static final String CLOSE_MAIN_APP_COMMAND = "CloseMainApp";
    
    /**
     * This command suspends the test execution for the given amount of time.
     *
     * @param delay is an integer value expressed in seconds. If the delay
     *              is &lt= 0 then the command wait forever and the script gets
     *              interrupted. This is useful if the script shall give the
     *              user the possibility to use the application.
     *
     * @example Wait(5);
     */
    public static final String WAIT_COMMAND = "Wait";

    /**
     * This command wait for a sync to start and finish within a maximum amount
     * of time. As soon as the sync terminates, the script continues its
     * execution.
     *
     * @param minStart is the time the script waits for the sync to start. If
     *                 the sync does not start withing this time, the test fails
     * @param maxTime  is the maximum time the script is willing for the sync to
     *                 terminate. If it does not terminate within this limit,
     *                 the test fails.
     *
     * @example WaitForSyncToComplete(10, 120);
     */
    public static final String WAIT_FOR_SYNC_TO_COMPLETE_COMMAND = "WaitForSyncToComplete";

    /**
     * This command simulates a user action via the device keypad.
     * @param command the command to simulate. Possible values are:
     *                <ul>
     *                  <li> KeyDown to move down </li>
     *                  <li> KeyUp to move up </li>
     *                  <li> KeyLeft to move left </li>
     *                  <li> KeyRight to move right </li>
     *                  <li> KeyFire to click </li>
     *                  <li> KeyMenu to open menu </li>
     *                  <li> KeyBack to return back </li>
     *                  <li> KeyDelete to delete </li>
     *                </ul>
     * @param count the number of commands to send (not mandatory)
     *
     * @example KeyPress(KeyDown, 1);
     */
    public static final String KEY_PRESS_COMMAND = "KeyPress";

    /**
     * This command simulates a user writing a text through the device keyboard.
     * @param text the text to write (e.g. into an input field)
     *
     * @example WriteString("username");
     */
    public static final String WRITE_STRING_COMMAND = "WriteString";

    /**
     * This command starts a refresh of all the sources
     *
     * @example RefreshAll();
     */
    public static final String REFRESH_ALL_COMMAND = "RefreshAll";

    /**
     * This command starts a refresh for the given source
     * @param source the name of the source to refresh
     *
     * @example RefreshSource(Pictures);
     */
    public static final String REFRESH_SOURCE_COMMAND = "RefreshSource";
    
    /**
     * This command forces the next sync to be a slow sync for the given source
     *
     * @param sourceName is the name of the source. The value is what is displayed
     * on the main screen for that source.
     *
     * @example ForceSlowSync("Calendar");
     */
    public static final String FORCE_SLOW_SYNC_COMMAND = "ForceSlowSync";

    /**
     * This command checks the amount of items exchanged between the server and
     * the client during the last synchronization (for a given source).
     *
     * @param source the source name. The value is what is displayed
     * on the main screen for that source.
     * @param sentAdd the expected number of new items sent
     * @param sentReplace the expected number of replace items sent
     * @param sentDelete the expected number of delete items sent
     * @param receivedAdd the expected number of new received items
     * @param receivedReplace the expected number of replace received items
     * @param receivedDelete the expected number of delete received items
     *
     * @example CheckExchangedData("Calendar", 1, 0, 0, 0, 0, 1);
     */
    public static final String CHECK_EXCHANGED_DATA_COMMAND = "CheckExchangedData";    
    
    /**
     * This command checks the amount of items exchanged between the server and
     * the client during the last synchronization (for a given source).
     *
     * @param source the source name. The value is what is displayed
     * on the main screen for that source.
     * @param code one of the status codes in {@link SyncListener}
     *
     * @example CheckExchangedData("Pictures", 154);
     */
    public static final String CHECK_SYNC_STATUS_CODE_COMMAND = "CheckSyncStatusCode";

    /**
     * This command checks the amount of items exchanged between the server and
     * the client during the last synchronization (for a given source).
     *
     * @param id
     * @param severity
     * @param ticker
     * @param title
     * @param message
     *
     * @example CheckLastNotification(10, 1, "Sync error", 
     *          "Insufficient storage on device", 
     *          "You're running out of storage space on device and cannot download media and files.");
     */
    public static final String CHECK_LAST_NOTIFICATION = "CheckLastNotification";

    /**
     * This command checks the amount of errors happened during the last
     * synchronization.
     *
     * @param source the source name. The value is what is displayed
     * on the main screen for that source.
     * @param sendingErrors the expected number of sent resumed items
     * @param receivingErrors the expected number of received resumed items
     * @example CheckSyncErrors("Pictures", 1, 0);
     */
    public static final String CHECK_SYNC_ERRORS_COMMAND = "CheckSyncErrors";

    /**
     * This command checks the amount of resumed items exchanged between the
     * server and the client during the last synchronization (for a given
     * source).
     *
     * @param source the source name. The value is what is displayed
     * on the main screen for that source.
     * @param sentResumed the expected number of sent resumed items
     * @param receivedResumed the expected number of received resumed items
     * @example CheckResumedData("Pictures", 1, 0);
     */
    public static final String CHECK_RESUMED_DATA_COMMAND = "CheckResumedData";

    /**
     * This command checks the requested sync mode of the last sync.
     *
     * @param source is the source name
     * @param mode an integer representing the expected sync mode
     *
     * @example CheckRequestedSyncMode("Calendar", 200);
     */
    public static final String CHECK_REQUESTED_SYNC_MODE_COMMAND = "CheckRequestedSyncMode";

    /**
     * This command checks the alerted sync mode of the last sync.
     *
     * @param source is the source name
     * @param mode an integer representing the expected sync mode
     *
     * @example CheckRequestedSyncMode("Calendar", 201);
     */
    public static final String CHECK_ALERTED_SYNC_MODE_COMMAND = "CheckAlertedSyncMode";

    /**
     * This command checks that the last sync was performed on the given URI.
     *
     * @param source the source name as it appears in the home screen
     * @param uri the remote uri
     *
     * @example CheckRemoteUri("Calendar", "event");
     */
    public static final String CHECK_REMOTE_URI_COMMAND = "CheckRemoteUri";

    /**
     * This command allows to interrupt the synchronization in some specific
     * moments.
     *
     * @param phase specifies the sync phase that shall be interrupted.
     * Supported values are:
     *
     * <ul>
     *   <li> Sending </li>
     *   <li> Receiving </li>
     * </ul>
     *
     * @param num is the number of items after which interruption shall occur
     * @param reason is the description of the exception that will be thrown
     * (for example "Cancelled" or "Network Error")
     *
     * @example InterruptSyncAfterPhase("Sending", 1, "User cancelled");
     */
    public static final String INTERRUPT_SYNC_AFTER_PHASE_COMMAND = "InterruptSyncAfterPhase";

    /**
     * This command allows to set device date/time
     *
     * @param date specifies date time in the format yyyyMMddTHHmmssZ
     *
     * @example SetDeviceDate("20101001");
     */
    public static final String SET_DEVICE_DATE = "SetDeviceDate";

    /**
     * This command allows to reset the first run timestamp to the current date/time.
     * 
     * @example ResetFirstRunTimestamp();
     */
    public static final String RESET_FIRST_RUN_TIMESTAMP = "ResetFirstRunTimestamp";

    /**
     * This command allows to define a variable
     *
     * @example SetVariable("number","5");
     */
    public static final String SET_VARIABLE = "SetVariable";

}
    

