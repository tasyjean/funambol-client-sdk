/*
 * Copyright (C) 2006-2007 Funambol
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA 
 */

package com.funambol.syncml.client;

import com.funambol.syncml.spds.HttpTransportAgent;
import jmunit.framework.cldc10.AssertionFailedException;
import jmunit.framework.cldc10.TestCase;
import com.funambol.util.Log;

public class HttpTransportAgentTest extends TestCase {

   
    public HttpTransportAgentTest() {
        super(1, "HttpTransportAgentTest");
    }
    
    public void setUp() {
    }

    public void tearDown() {
    }
    
    public void test(int testNumber) throws Throwable {
        switch(testNumber) {
            case 0: 
                testConstructorUrlNull();
                break;
            default:                    
                break;
        }
    }

    public void testConstructorUrlNull() throws Exception {
        boolean result = true;
        String msg ="";
        try{
            HttpTransportAgent hta = new HttpTransportAgent(null,false,false);
            result=false;
            msg ="NullPointerException doesn't thrown.";
        }catch (NullPointerException nex){
            //test pass if NullPointerException was thrown    
        }
        
        try{
            assertTrue(msg,result);
        }catch (AssertionFailedException afex){
            
        }
    }

   

}
