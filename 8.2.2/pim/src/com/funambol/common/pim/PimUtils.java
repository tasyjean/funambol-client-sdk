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

package com.funambol.common.pim;

import javax.microedition.pim.PIMItem;
import javax.microedition.pim.PIMList;
import javax.microedition.pim.PIMException;
import java.io.UnsupportedEncodingException;
import java.io.OutputStream;
import java.io.IOException;

import com.funambol.util.Log;
import com.funambol.util.StringUtil;

/**
 * This class provides basic utilities to handle standard formats such as vCard
 * and vCal. The class extends Utils, providing utilities JSR75 related and
 * specific. This class shall only be compiled/used on platforms where JSR75 is
 * available.
 */
public class PimUtils extends Utils {

    public PimUtils(String charset) {
        super(charset);
    }

    /**
     * This function will add Category data to provided PIMItem.
     * 
     * @param field The categories field extracted from vCard
     * @param list This is the PIMList that the PIMItem belongs to.
     * @param item The PIMItem that will have Category data added to it.
     * @param CategoryTag The string which is SIF tag for Categories.
     */
    public void addCategories(String field, PIMList list, PIMItem item,
                              boolean addNewCategory) throws PIMException
    {
        int max = item.maxCategories();
        max = max < 0 ? Integer.MAX_VALUE : max;
        String sep[] = new String[2];
        sep[0] = "; ";
        sep[1] = ", ";
        final String[] categories = StringUtil.split(field, sep);
        
        if (categories.length > max){
            Log.info("Some categories being dropped (max = " + max + ", length = " + categories.length + ")");
        }
        for (int j = 0; j < (max < categories.length ? max : categories.length); j++){

            if (!list.isCategory(categories[j]) && addNewCategory){
                  list.addCategory(categories[j]);
            }
            
            if(list.isCategory(categories[j])){
                try {
                    item.addToCategory(categories[j]);
                }catch (PIMException e) {
                    Log.info("Exception while adding category ["+categories[j]+"]");
                }
            }
        }
    }

    /**
     * Print a new line to an output stream
     * @param os the output stream
     * @param msg the message to print
     * @throws javax.microedition.pim.PIMException
     */
    public static void println(OutputStream os, String msg) throws PIMException {
        try {
            os.write(msg.getBytes());
            os.write("\n".getBytes());
        } catch (IOException ioe) {
            throw new PIMException(ioe.toString());
        }
    }
}

