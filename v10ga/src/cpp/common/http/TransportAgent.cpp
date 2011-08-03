/*
 * Funambol is a mobile platform developed by Funambol, Inc. 
 * Copyright (C) 2003 - 2007 Funambol, Inc.
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
#include "http/constants.h"
#include "http/HTTPHeader.h"
#include "http/TransportAgent.h"
#include "base/util/KeyValuePair.h"
#include "base/util/utils.h"
#include "base/globalsdef.h"

USE_NAMESPACE

TransportAgent::TransportAgent() {
    timeout = DEFAULT_MAX_TIMEOUT;
    maxmsgsize = DEFAULT_MAX_MSG_SIZE;
    readBufferSize = DEFAULT_INTERNET_READ_BUFFER_SIZE;
    compression = false;
    SSLServerCertificates = "";
    SSLVerifyServer = true;
    SSLVerifyHost = true;
    responseSize = 0;
    responseCode = -1;
    // Set the default content type in the SyncManager::initTransportAgent    
}

TransportAgent::TransportAgent(const URL& newURL,
                               Proxy& newProxy,
                               unsigned int timeout,
                               unsigned int maxmsgsize) {

    url = newURL;
    proxy.setProxy(newProxy);
    this->timeout = timeout;
    this->maxmsgsize  = maxmsgsize;
    readBufferSize = DEFAULT_INTERNET_READ_BUFFER_SIZE;
    compression = false;
    SSLServerCertificates = "";
    SSLVerifyServer = true;
    SSLVerifyHost = true;
    responseSize = 0;
    responseCode = -1;
    // Set the default content type in the SyncManager::initTransportAgent     
}


TransportAgent::~TransportAgent() {
}

void TransportAgent::setURL(const URL& newURL) {
    url = newURL;
}

URL& TransportAgent::getURL() {
    return url;
}

void TransportAgent::setTimeout(unsigned int t) {
    timeout = t;
}

unsigned int TransportAgent::getTimeout() {
    return timeout;
}

void TransportAgent::setMaxMsgSize(unsigned int t) {
    maxmsgsize = t;
}

unsigned int TransportAgent::getMaxMsgSize() {
    return maxmsgsize;
}

void TransportAgent::setReadBufferSize(unsigned int t) {
    readBufferSize = t;
}

unsigned int TransportAgent::getReadBufferSize() {
    return readBufferSize;
}

const char* TransportAgent::getResponseProperty(const char *pname) {
    return responseProperties[pname].c_str();
}

unsigned int TransportAgent::getResponseSize() {
	return responseSize;
}

void TransportAgent::setResponseCode(int respCode){
    responseCode = respCode;
}

int TransportAgent::getResponseCode() {
    return responseCode;
}

void TransportAgent::setUserAgent(const char* ua) {
    userAgent = ua;
}

const char* TransportAgent::getUserAgent() {
    return userAgent;
}

void TransportAgent::setCompression(bool newCompression){
    compression = newCompression;
}

bool TransportAgent::getCompression(){
    return compression;
}

void TransportAgent::setProperty(const char *propName, const char * const propValue){
    requestProperties.put(propName, propValue);
}

char* TransportAgent::query(ArrayList& httpHeaders, long* protocolResponseCode){
    responseCode = 0;
    return 0;
}
