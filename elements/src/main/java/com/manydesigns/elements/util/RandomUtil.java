/*
 * Copyright (C) 2005-2010 ManyDesigns srl.  All rights reserved.
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

package com.manydesigns.elements.util;

import com.manydesigns.elements.ElementsProperties;
import com.manydesigns.elements.logging.LogUtil;
import org.apache.commons.lang.RandomStringUtils;

import java.io.File;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.logging.Logger;

/*
* @author Paolo Predonzani     - paolo.predonzani@manydesigns.com
* @author Angelo Lupo          - angelo.lupo@manydesigns.com
* @author Giampiero Granatella - giampiero.granatella@manydesigns.com
*/
public class RandomUtil {
    public static final String copyright =
            "Copyright (c) 2005-2010, ManyDesigns srl";

    public final static int DEFAULT_RANDOM_CODE_LENGTH = 20;

    public final static Logger logger = LogUtil.getLogger(RandomUtil.class);

    protected static final int codeLength;
    protected static final File tempDir;

    static {
        Properties properties = ElementsProperties.getProperties();
        String stringValue =
                properties.getProperty(
                        ElementsProperties.RANDOM_CODE_LENGTH_PROPERTY);
        int tmp;
        try {
            tmp = Integer.parseInt(stringValue);
        } catch (Throwable e) {
            tmp = DEFAULT_RANDOM_CODE_LENGTH;
            LogUtil.finerMF(logger,
                    "Cannot use value ''{0}''. Using default: {2}",
                    stringValue, tmp);
        }
        codeLength = tmp;

        tempDir = new File(System.getProperty("java.io.tmpdir"));
    }

    public static File getTempDir() {
        return tempDir;
    }

    public static String createRandomCode() {
        return RandomStringUtils.randomAlphanumeric(codeLength);
    }

    public static File getTempCodeFile(String fileNameFormat,
                                       String randomCode) {
        return getCodeFile(tempDir, fileNameFormat, randomCode);
    }

    public static File getCodeFile(File dir,
                                   String fileNameFormat,
                                   String code) {
        return new File(dir, getCodeFileName(fileNameFormat, code));
    }

    public static String getCodeFileName(String fileNameFormat,
                                         String randomCode) {
        return MessageFormat.format(fileNameFormat, randomCode);
    }

}
