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

package com.manydesigns.portofino.reflection;

import com.manydesigns.elements.annotations.*;
import com.manydesigns.elements.annotations.impl.*;
import com.manydesigns.elements.reflection.PropertyAccessor;
import com.manydesigns.portofino.model.database.Column;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.Map;

/*
* @author Paolo Predonzani     - paolo.predonzani@manydesigns.com
* @author Angelo Lupo          - angelo.lupo@manydesigns.com
* @author Giampiero Granatella - giampiero.granatella@manydesigns.com
* @author Alessio Stalla       - alessio.stalla@manydesigns.com
*/
public class ColumnAccessor
        extends AbstractAnnotatedAccessor 
        implements PropertyAccessor {
    public static final String copyright =
            "Copyright (c) 2005-2011, ManyDesigns srl";

    //**************************************************************************
    // Fields
    //**************************************************************************

    protected final Column column;
    protected final PropertyAccessor nestedPropertyAccessor;

    public static final Logger logger =
            LoggerFactory.getLogger(ColumnAccessor.class);


    //**************************************************************************
    // Constructors
    //**************************************************************************

    public ColumnAccessor(Column column, boolean inPk, boolean autoGenerated,
                          PropertyAccessor nestedPropertyAccessor) {
        super(column.getAnnotations());
        this.column = column;
        this.nestedPropertyAccessor = nestedPropertyAccessor;

        annotations.put(Required.class, new RequiredImpl(!column.isNullable()));
        if(String.class.equals(column.getActualJavaType())) {
            annotations.put(MaxLength.class, new MaxLengthImpl(column.getLength()));
        }
        annotations.put(PrecisionScale.class,
                new PrecisionScaleImpl(column.getLength(), column.getScale()));
        annotations.put(Enabled.class, new EnabledImpl(true));
        annotations.put(Updatable.class, new UpdatableImpl(!inPk));
        annotations.put(Insertable.class,
                new InsertableImpl(!column.isAutoincrement() && !autoGenerated));
        annotations.put(Searchable.class,
                new SearchableImpl(column.isSearchable()));
    }


    //**************************************************************************
    // PropertyAccessor implementation
    //**************************************************************************

    public String getName() {
        return column.getActualPropertyName();
    }

    public Class getType() {
        return column.getActualJavaType();
    }

    public int getModifiers() {
        return Modifier.PUBLIC;
    }

    public Object get(Object obj) {
        if (nestedPropertyAccessor == null) {
            return ((Map)obj).get(column.getActualPropertyName());
        } else {
            return nestedPropertyAccessor.get(obj);
        }
    }

    public void set(Object obj, Object value) {
        if (nestedPropertyAccessor == null) {
            //noinspection unchecked
            ((Map)obj).put(column.getActualPropertyName(), value);
        } else {
            nestedPropertyAccessor.set(obj, value);
        }
    }

    //**************************************************************************
    // Getters/setters
    //**************************************************************************

    public Column getColumn() {
        return column;
    }

    //**************************************************************************
    // toString()
    //**************************************************************************

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("column", column.getQualifiedName()).toString();
    }
}
