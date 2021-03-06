/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2009 Funambol, Inc.
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

package com.funambol.client.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import com.funambol.org.json.me.JSONException;
import com.funambol.org.json.me.JSONObject;

import com.funambol.client.customization.Customization;
import com.funambol.client.configuration.Configuration;
import com.funambol.client.source.AppSyncSourceManager;
import com.funambol.client.localization.Localization;
import com.funambol.client.ui.DisplayManager;
import com.funambol.client.ui.SignupScreen;
import com.funambol.sapisync.sapi.SapiHandler;

import com.funambol.platform.HttpConnectionAdapter;
import com.funambol.platform.DeviceInfoInterface;

import com.funambol.sync.SyncException;
import com.funambol.util.ConnectionManager;
import com.funambol.platform.NetworkStatus;
import com.funambol.util.Log;
import com.funambol.util.StringUtil;

/**
 * This class is the controller (in the MVC model) for the SignupScreen.
 */
public abstract class SignupScreenController extends AccountScreenController {

    private static final String TAG_LOG = "SignupScreenController";

    protected final int STATE_BEGIN        = 0;
    protected final int STATE_LOGGED_IN    = 1;
    protected final int STATE_LOGIN_FAILED = 2;
    protected final int STATE_CAPTCHA      = 3;
    protected final int STATE_SIGNED_UP    = 4;

    protected int state = STATE_BEGIN;

    private SignupHandler signupHandler;
    private String        jsessionId;

    public SignupScreenController(Controller controller, SignupScreen signupScreen, NetworkStatus networkStatus) {
        super(controller, signupScreen, networkStatus);
    }

    /**
     * @return the deviceInfo implementation for the current controller
     */
    protected abstract DeviceInfoInterface getDeviceInfo();
    

    public void initScreen() {
        String url, usr, pwd;
        DeviceInfoInterface devInfo = getDeviceInfo();
        String phoneNumber;

        if (customization.getPrefillPhoneNumber()) {
            phoneNumber = devInfo.getPhoneNumber();
        } else {
            phoneNumber = null;
        }

        usr = StringUtil.isNullOrEmpty(phoneNumber) ?
              customization.getUserDefault() : phoneNumber.trim();
        pwd = customization.getPasswordDefault();
        url = customization.getServerUriDefault();
        initScreen(url, usr, pwd);
    }

    public void initScreen(String url, String usr, String pwd) {
        if(screen != null) {
            screen.setSyncUrl(url);
            originalUrl = url;
            screen.setUsername(usr);
            originalUser = usr;
            screen.setPassword(pwd);
            originalPassword = pwd;
            if(customization.getAddShowPasswordField()) {
                screen.addShowPasswordField(true);
            }
        }
        promptCredentials();
    }

    public void promptCredentials() {
        ((SignupScreen)screen).promptCredentials();
        state = STATE_BEGIN;
    }

    public void promptCaptcha() {
        ((SignupScreen)screen).promptCaptcha();
        state = STATE_CAPTCHA;
    }

    /**
     * Prompts the user to the login screen
     */
    public void switchToLoginScreen() {
        try {
            controller.showScreen(screen, Controller.LOGIN_SCREEN_ID);
            controller.hideScreen(screen);
        } catch(Exception ex) {
            Log.error(TAG_LOG, "Unable to switch to login screen", ex);
        }
    }

    /**
     * Sign up the user with the information taken from the account screen and
     * DeviceInfo
     */
    public void continueWithSignup() {
        if (Log.isLoggable(Log.DEBUG)) {
            Log.debug(TAG_LOG, "Continue with signup");
        }

        String phoneNumber = screen.getUsername();
        String password    = screen.getPassword();
        
        // First validity check of Phone # and Password
        if (StringUtil.isNullOrEmpty(phoneNumber)) {
            signupFailed(localization.getLanguage("signup_failed_empty_fields_message"));
            return;
        } else if (StringUtil.isNullOrEmpty(password)) {
            signupFailed(localization.getLanguage("signup_failed_empty_fields_message"));
            return;
        }

        // Before requesting the CAPTCHA we try to do a login in order to check
        // if the provided credentials are valid. This prevents the user to
        // enter the CAPTCHA token even thought the login can be performed
        // immediately.  
        requestLogin();
    }

    protected void requestLogin() {
        new LoginRequest().start();
    }

    protected void loginRequestStarted() {
        showProgressDialog(localization.getLanguage("signup_please_wait"));
    }

    protected void loginRequestSucceeded() {
        // Signup succeeded. Proceed with client login
        if (Log.isLoggable(Log.INFO)) {
            Log.info(TAG_LOG, "Login succeeded, proceeding with client login");
        }
        state = STATE_LOGGED_IN;
        hideProgressDialog();
        saveAndCheck();
    }

    protected void loginRequestFailed() {
        // Login failed. Proceed with signup
        if (Log.isLoggable(Log.INFO)) {
            Log.info(TAG_LOG, "Login failed, proceeding with signup");
        }
        state = STATE_LOGIN_FAILED;
        requestNewCaptcha(false);
    }

    protected void requestNewCaptcha(boolean unmatched) {
        new CaptchaRequest(unmatched).start();
    }

    protected void captchaRequestStarted() {
        if(state == STATE_BEGIN) {
            showProgressDialog(localization.getLanguage("signup_please_wait"));
        }
    }

    protected void captchaRequestSucceeded(byte[] captcha) {
        hideProgressDialog();
        // Check if captcha validation is not required. In this case start the
        // signup directly
        if(captcha == null) {
            if (Log.isLoggable(Log.INFO)) {
                Log.info(TAG_LOG, "No need to do CAPTCHA validation, continue with signup");
            }
            signup();
        } else {
            if (Log.isLoggable(Log.INFO)) {
                Log.info(TAG_LOG, "Asking for CAPTCHA token");
            }
            ((SignupScreen)screen).setCaptchaToken("");
            ((SignupScreen)screen).setCaptchaImage(captcha);
            promptCaptcha();
        }
    }

    protected void captchaRequestFailed(String msg) {
        if (Log.isLoggable(Log.DEBUG)) {
            Log.debug(TAG_LOG, "CAPTCHA request failed");
        }
        hideProgressDialog();
        showMessage(msg);
        promptCredentials();
    }

    /**
     * Sign up the user with the information taken from the account screen and
     * DeviceInfo
     */
    public void signup() {
        if (Log.isLoggable(Log.DEBUG)) {
            Log.debug(TAG_LOG, "Starting signup thread");
        }

        ContinueAfterNetwokUsageWarning ca = new ContinueAfterNetwokUsageWarning();
        AbortAfterNetwokUsageWarning asa = new AbortAfterNetwokUsageWarning();
        boolean prompt = promptUserForNetworkUsage(ca, asa);
        if (!prompt) {
            fireSignup();
        }
    }

    private void fireSignup() {
        signupHandler = new SignupHandler((SignupScreen)screen, this);
        signupHandler.start();
    }


    public void signupStarted() {
        if (Log.isLoggable(Log.TRACE)) {
            Log.trace(TAG_LOG, "signupStarted");
        }
        showProgressDialog(localization.getLanguage("signup_signing_up"));
    }

    public void signupSucceeded() {

        if (Log.isLoggable(Log.TRACE)) {
            Log.trace(TAG_LOG, "signupSucceeded");
        }
        hideProgressDialog();

        ((SignupScreen)screen).enableSave();
        ((SignupScreen)screen).enableSignup();

        state = STATE_SIGNED_UP;

        // At this point the user was able to signup, he should never be able to
        // signup again from this device, unless the app is re-installed from
        // scratch
        configuration.setSignupAccountCreated(true);
        configuration.save();

        // It's time to login
        saveAndCheck();
    }

    protected void signupFailed(String msg) {
        signupFailed(msg, true);
    }
    
    protected void signupFailed(String msg, boolean promptCredentials) {
        if (Log.isLoggable(Log.TRACE)) {
            Log.trace(TAG_LOG, "signupFailed");
        }
        hideProgressDialog();
        if(msg != null) {
            showMessage(msg);
        }
        ((SignupScreen)screen).enableSave();
        ((SignupScreen)screen).enableSignup();
        if(promptCredentials) {
            promptCredentials();
        }
        networkUsageWarningShown = false;
    }

    public void syncEnded() {
        super.syncEnded();
        if (failed) {

            hideProgressDialog();

            if (configuration.getSignupAccountCreated()) {
                // The signup process failed after the account was created on
                // server. Open the login screen with the right credentials
                // pre-filled
                configuration.setCredentialsCheckPending(true);
                configuration.save();

                // We inform the user that he needs to login
                DialogController dc = controller.getDialogController();
                String msg = localization.getLanguage("signup_account_created_login_failed");
                dc.showOkDialog(screen, msg,
                    new Runnable() {
                        public void run() {
                            switchToLoginScreen();
                        }
                    }
                );
            } else {
                // If the config sync failed for any reason, we prompt the user to
                // the credentials screen
                promptCredentials();
            }
        }
    }

    public void showActivationRequiredActionDialog() {
        hideProgressDialog();

        UserPerformedManualSignupAction okAction = new UserPerformedManualSignupAction();
        DialogController dc = controller.getDialogController();
        String msg = localization.getLanguage("signup_require_sms_activation");
        dc.showOkDialog(screen, msg, okAction);
    }

    protected void userAuthenticated() {
        ((SignupScreen)screen).enableSignup();
        // Show the congrats message only if the account has really been created
        if(customization.getShowSignupSuccededMessage() && state == STATE_SIGNED_UP) {
            if (Log.isLoggable(Log.DEBUG)) {
                Log.debug(TAG_LOG, "Showing signup succeeded message");
            }
            // If the password is not showed in clear text, we cannot show it
            // in the signup succeeded message.
            String succeededMsg;
            if(((SignupScreen)screen).isPasswordShowed()) {
                succeededMsg = localization.getLanguage("signup_succeeded");
                succeededMsg = StringUtil.replaceAll(succeededMsg, "__PASSWORD__",
                    configuration.getPassword());
            } else {
                succeededMsg = localization.getLanguage("signup_succeeded_no_password");
            }
            succeededMsg = StringUtil.replaceAll(succeededMsg, "__USERNAME__",
                    configuration.getUsername());
            showSignupSucceededMessage(succeededMsg);
        }
        super.userAuthenticated();
        hideProgressDialog();
    }

    protected void showSignupSucceededMessage(String message) {
        showMessage(message);
    }

    protected void showProgressDialog(String label) {
    }

    protected void hideProgressDialog() {
    }
    
    protected String getMessageFromSyncException(SyncException ex) {
        String msg = super.getMessageFromSyncException(ex);
        switch (ex.getCode()) {
            case SyncException.AUTH_ERROR:
                // Should never happen since the account has been just created
                 msg = localization.getLanguage("signup_failed_generic_message");
                break;
            case SyncException.DATA_NULL:
            case SyncException.READ_SERVER_RESPONSE_ERROR:
            case SyncException.WRITE_SERVER_REQUEST_ERROR:
            case SyncException.SERVER_CONNECTION_REQUEST_ERROR:
                msg = localization.getLanguage("signup_failed_network");
                break;
            default:
                msg = localization.getLanguage("signup_failed_generic_message");
                break;
        }
        return msg;
    }

    /**
     * @return the jsessionid of the latest CAPTCHA request session
     */
    public String getCurrentJSessionId() {
        return jsessionId;
    }

    public void setCurrentJSessionId(String id) {
        jsessionId = id;
    }

    private class ContinueSignUpAction implements Runnable {       
        public ContinueSignUpAction() {
        }
        
        public void run() {
            requestLogin();
        }
    }

    /**
     * This is the Thread which performs a login request to the server
     */
    private class LoginRequest extends Thread {

        private static final String JSON_OBJECT_DATA = "data";
        private static final String JSON_OBJECT_DATA_FIELD_JSESSIONID = "jsessionid";

        public LoginRequest() {
        }

        public void run() {

            loginRequestStarted();

            SapiHandler sapiHandler = new SapiHandler(
                StringUtil.extractAddressFromUrl(screen.getSyncUrl()),
                screen.getUsername(), screen.getPassword());
            sapiHandler.setAuthenticationMethod(SapiHandler.AUTH_IN_QUERY_STRING);
            try {
                if (Log.isLoggable(Log.DEBUG)) {
                    Log.debug(TAG_LOG, "Sending Login request");
                }
                JSONObject res = sapiHandler.query("login", "login",
                        null, null, null);
                if(isAuthenticated(res)) {
                    // Login succeeded
                    loginRequestSucceeded();
                    return;
                }
            } catch(Exception ex) {
                // Ignore exception
            }
            // Login failed
            loginRequestFailed();
        }

        private boolean isAuthenticated(JSONObject response) throws JSONException {
            JSONObject resData = response.getJSONObject(JSON_OBJECT_DATA);
            if(resData != null) {
                String jsessionid = resData.getString(JSON_OBJECT_DATA_FIELD_JSESSIONID);
                if(!StringUtil.isNullOrEmpty(jsessionid)) {
                    // Login succeeded
                    return true;
                }
            }
            // Login failed
            return false;
        }
    }

    // This method is invoked after the user has been created but we still need
    // to login in case the user needs to perform some manual action (e.g.
    // clicking on the activation SMS)
    protected void continueAfterSignup() {
        signupSucceeded();
    }

    private class UserPerformedManualSignupAction implements Runnable {
        public void run() {
            // The signup succeeded, so we let the entire process to continue
            showProgressDialog(localization.getLanguage("signup_please_wait"));
            continueAfterSignup();
        }
    }

    private class ContinueAfterNetwokUsageWarning implements Runnable {
        public void run() {
            fireSignup();
        }
    }

    private class AbortAfterNetwokUsageWarning implements Runnable {
        public void run() {
            networkUsageWarningShown = false;
        }
    }

    /**
     * This is the Thread which requests the CAPTCHA image from the server
     */
    private class CaptchaRequest extends Thread {

        private static final String JSON_OBJECT_DATA    = "data";
        private static final String JSON_OBJECT_CAPTCHA = "captchaurl";

        private static final String JSON_OBJECT_CAPTCHA_FIELD_PORTAL_URL = "portalurl";
        private static final String JSON_OBJECT_CAPTCHA_FIELD_IMAGE_PATH = "imagepath";
        private static final String JSON_OBJECT_CAPTCHA_FIELD_ACTIVE     = "active";

        private static final String JSESSIONID_HEADER = "JSESSIONID";
        private static final String SET_COOKIE_HEADER = "Set-Cookie";

        private boolean unmatched = false;

        public CaptchaRequest(boolean unmatched) {
            this.unmatched = unmatched;
        }

        public void run() {

            captchaRequestStarted();

            // Retrieve captcha url using SAPI
            String captchaUrl = null;
            SapiHandler sapiHandler = new SapiHandler(
                StringUtil.extractAddressFromUrl(screen.getSyncUrl()));
            try {
                if (Log.isLoggable(Log.DEBUG)) {
                    Log.debug(TAG_LOG, "Sending CAPTCHA SAPI request");
                }
                Vector params = new Vector();
                params.addElement("mobile=true");
                JSONObject res = sapiHandler.query("system/captcha", "get-url",
                        params, null, null);
                captchaUrl = getCaptchaUrlFromJSON(res);
            } catch(IOException ex) {
                // This is a network failure
                Log.error(TAG_LOG, "Unable to retrieve CAPTCHA url", ex);
                captchaRequestFailed(localization.getLanguage(
                        "signup_failed_network"));
                return;
            } catch(Exception ex) {
                // This is a generic failure
                Log.error(TAG_LOG, "Unable to retrieve CAPTCHA url", ex);
                captchaRequestFailed(localization.getLanguage(
                            "signup_failed_generic_message"));
                return;
            }
            // Check if captcha validation shall be used
            if(captchaUrl == null) {
                captchaRequestSucceeded(null);
                return;
            }
            // Retrieve CAPTCHA image
            ByteArrayOutputStream captcha = new ByteArrayOutputStream();
            HttpConnectionAdapter conn = null;
            OutputStream os = null;
            InputStream  is = null;
            try {
                if (Log.isLoggable(Log.DEBUG)) {
                    Log.debug(TAG_LOG, "Requesting CAPTCHA image from: " + captchaUrl);
                }
                conn = ConnectionManager.getInstance().openHttpConnection(
                        captchaUrl, "wrapper");
                conn.setRequestMethod(HttpConnectionAdapter.GET);
                conn.execute(null, -1);
                if (Log.isLoggable(Log.DEBUG)) {
                    Log.debug(TAG_LOG, "Response is: " + conn.getResponseCode());
                }
                if (conn.getResponseCode() == HttpConnectionAdapter.HTTP_OK) {
                    is = conn.openInputStream();
                    // Read until we have data
                    int responseLength = conn.getLength();
                    if(responseLength > 0) {
                        // Read the input stream according to the response
                        // content-length header
                        int b;
                        do {
                            b = is.read();
                            responseLength--;
                            if (b != -1) {
                                captcha.write(b);
                            }
                        } while(b != -1 && responseLength > 0);
                    } else if(responseLength < 0) {
                        // The content-length header was not found in the http
                        // response. It could be a chunked encoded response
                        int b;
                        do {
                            b = is.read();
                            if (b != -1) {
                                captcha.write(b);
                            }
                        } while(b != -1);
                    }
                } else {
                    Log.error(TAG_LOG, "Captcha request failed. Server " +
                            "replied: " + conn.getResponseCode() + ", message: " +
                            conn.getResponseMessage());
                    captchaRequestFailed(localization.getLanguage(
                            "signup_failed_network"));
                    return;
                }
                // retrieve the JSESSIONID from http headers
                String cookies = conn.getHeaderField(SET_COOKIE_HEADER);
                if (cookies != null) {
                    if (Log.isLoggable(Log.DEBUG)) {
                        Log.debug(TAG_LOG, "Set-Cookie from server: " + cookies);
                    }
                    int jsidx = cookies.indexOf(JSESSIONID_HEADER);
                    if (jsidx >= 0) {
                        String tmpjsessionId = cookies.substring(jsidx);
                        int equalidx = tmpjsessionId.indexOf("=");
                        int idx = tmpjsessionId.indexOf(";");
                        if (equalidx >= 0) {
                            if(idx > 0) {
                                jsessionId = tmpjsessionId.substring(equalidx+1, idx);
                            } else {
                                jsessionId = tmpjsessionId.substring(equalidx+1);
                            }
                            if (Log.isLoggable(Log.DEBUG)) {
                                Log.debug(TAG_LOG, "Found jsessionid = " + jsessionId);
                            }
                        }
                    }
                }
            } catch(IOException ex) {
                Log.error(TAG_LOG, "Captcha request failed", ex);
                captchaRequestFailed(localization.getLanguage(
                        "signup_failed_network"));
                return;
            } finally {
                // Release all resources
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {}
                    os = null;
                }
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {}
                    is = null;
                }
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (IOException ioe) {}
                    conn = null;
                }
            }
            
            // Request succeeded
            captchaRequestSucceeded(captcha.toByteArray());

            // If the last entered code didn't match, we show an alert message
            if(unmatched) {
                showMessage(localization.getLanguage("signup_failed_invalid_captcha"));
            }
        }

        private String getCaptchaUrlFromJSON(JSONObject response) throws Exception {
            try {
                JSONObject resData = response.getJSONObject(JSON_OBJECT_DATA);
                if(resData != null) {
                    JSONObject resCaptcha = resData.getJSONObject(JSON_OBJECT_CAPTCHA);
                    if(resCaptcha != null) {
                        boolean active = resCaptcha.getBoolean(JSON_OBJECT_CAPTCHA_FIELD_ACTIVE);
                        if(!active) {
                            if (Log.isLoggable(Log.DEBUG)) {
                                Log.debug(TAG_LOG, "Captcha disabled on server");
                            }
                            return null;
                        }
                        StringBuffer captchaUrl = new StringBuffer();
                        String url = resCaptcha.getString(JSON_OBJECT_CAPTCHA_FIELD_PORTAL_URL);
                        String path = resCaptcha.getString(JSON_OBJECT_CAPTCHA_FIELD_IMAGE_PATH);
                        captchaUrl.append(url).append(path);
                        return captchaUrl.toString();
                    }
                }
            } catch(JSONException ex) {
                Log.error(TAG_LOG, "Failed to retrieve user data json object", ex);
            }
            throw new Exception("Unable to retrieve CAPTCHA url from JSON response");
        }
    }
}
