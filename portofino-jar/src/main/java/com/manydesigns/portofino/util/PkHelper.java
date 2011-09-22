/*
 * Copyright (C) 2005-2011 ManyDesigns srl.  All rights reserved.
 * http://www.manydesigns.com/
 *
 * Unless you have purchased a commercial license agreement from ManyDesigns srl,
 * the following license terms apply:
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
 *
 * There are special exceptions to the terms and conditions of the GPL
 * as it is applied to this software. View the full text of the
 * exception in file OPEN-SOURCE-LICENSE.txt in the directory of this
 * software distribution.
 *
 * This program is distributed WITHOUT ANY WARRANTY; and without the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see http://www.gnu.org/licenses/gpl.txt
 * or write to:
 * Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307  USA
 *
 */

package com.manydesigns.portofino.util;

import com.manydesigns.elements.ognl.OgnlUtils;
import com.manydesigns.elements.reflection.ClassAccessor;
import com.manydesigns.elements.reflection.PropertyAccessor;
import com.manydesigns.elements.text.OgnlTextFormat;
import com.manydesigns.elements.text.TextFormat;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/*
* @author Paolo Predonzani     - paolo.predonzani@manydesigns.com
* @author Angelo Lupo          - angelo.lupo@manydesigns.com
* @author Giampiero Granatella - giampiero.granatella@manydesigns.com
* @author Alessio Stalla       - alessio.stalla@manydesigns.com
*/
public class PkHelper {
    public static final String copyright =
            "Copyright (c) 2005-2011, ManyDesigns srl";

    //**************************************************************************
    // Fields
    //**************************************************************************

    public final static Logger logger = LoggerFactory.getLogger(PkHelper.class);

    protected final ClassAccessor classAccessor;


    //**************************************************************************
    // Constructor
    //**************************************************************************

    public PkHelper(ClassAccessor classAccessor) {
        this.classAccessor = classAccessor;
    }


    //**************************************************************************
    // Methods
    //**************************************************************************

    public TextFormat createPkGenerator() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (PropertyAccessor property : classAccessor.getKeyProperties()) {
            if (first) {
                first = false;
            } else {
                sb.append(",");
            }
            sb.append("%{");
            sb.append(property.getName());
            sb.append("}");
        }
        return OgnlTextFormat.create(sb.toString());
    }

    public Serializable parsePkString(String pkString) {
        String[] pkList = StringUtils.split(pkString, ",");

        int i = 0;
        Serializable result = (Serializable)classAccessor.newInstance();

        for(PropertyAccessor property : classAccessor.getKeyProperties()) {
            String stringValue = pkList[i];
            Object value = OgnlUtils.convertValue(stringValue, property.getType());
            property.set(result, value);
            i++;
        }

        return result;
    }

    public String generatePkString(Object object) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;

        for(PropertyAccessor property : classAccessor.getKeyProperties()) {
            if (first) {
                first = false;
            } else {
                sb.append(",");
            }
            Object value = property.get(object);
            String stringValue =
                    (String) OgnlUtils.convertValue(value, String.class);
            sb.append(stringValue);
        }
        return sb.toString();
    }

}