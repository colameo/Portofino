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

package com.manydesigns.elements.fields;

import com.manydesigns.elements.Mode;
import com.manydesigns.elements.blobs.Blob;
import com.manydesigns.elements.blobs.BlobsManager;
import com.manydesigns.elements.reflection.PropertyAccessor;
import com.manydesigns.elements.servlet.Upload;
import com.manydesigns.elements.servlet.WebFramework;
import com.manydesigns.elements.util.MemoryUtil;
import com.manydesigns.elements.xml.XhtmlBuffer;
import org.apache.commons.lang.StringEscapeUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/*
* @author Paolo Predonzani     - paolo.predonzani@manydesigns.com
* @author Angelo Lupo          - angelo.lupo@manydesigns.com
* @author Giampiero Granatella - giampiero.granatella@manydesigns.com
*/
public class FileBlobField extends AbstractField
        implements MultipartRequestField {
    public static final String copyright =
            "Copyright (c) 2005-2010, ManyDesigns srl";

    public static final String UPLOAD_KEEP = "_keep";
    public static final String UPLOAD_MODIFY = "_modify";
    public static final String UPLOAD_DELETE = "_delete";

    public static final String OPERATION_SUFFIX = "_operation";
    public static final String CODE_SUFFIX = "_code";
    public static final String INNER_SUFFIX = "_inner";

    protected String innerId;
    protected String operationInputName;
    protected String codeInputName;

    protected final BlobsManager blobsManager;
    protected Blob blob;
    protected String blobError;

    //**************************************************************************
    // Costruttori
    //**************************************************************************
    public FileBlobField(PropertyAccessor accessor, Mode mode) {
        this(accessor, mode, null);
    }

    public FileBlobField(PropertyAccessor accessor, Mode mode, String prefix) {
        super(accessor, mode, prefix);
        blobsManager = BlobsManager.getManager();

        innerId = id + INNER_SUFFIX;
        operationInputName = inputName + OPERATION_SUFFIX;
        codeInputName = inputName + CODE_SUFFIX;
    }

    //**************************************************************************
    // AbstractField implementation
    //**************************************************************************
    public void valueToXhtml(XhtmlBuffer xb) {
        if (mode.isView(immutable, autogenerated)) {
            valueToXhtmlView(xb);
        } else if (mode.isEdit()) {
            valueToXhtmlEdit(xb);
        } else if (mode.isPreview()) {
            valueToXhtmlView(xb);
        } else if (mode.isHidden()) {
            // do nothing
        } else {
            throw new IllegalStateException("Unknown mode: " + mode);
        }
    }

    public String getStringValue() {
        return null;
    }

    private void valueToXhtmlView(XhtmlBuffer xb) {
        xb.openElement("div");
        xb.addAttribute("id", id);
        xb.addAttribute("class", "value");
        if (blobError != null) {
            xb.openElement("div");
            xb.addAttribute("class", "error");
            xb.write(blobError);
            xb.closeElement("div");
        } else if (blob != null) {
            if (href != null) {
                xb.openElement("a");
                xb.addAttribute("href", href);
            }
            writeBlobShortName(xb);
            if (href != null) {
                xb.closeElement("a");
            }
        }
        xb.closeElement("div");
    }

    private void writeBlobShortName(XhtmlBuffer xb) {
        xb.write(blob.getFilename());
        xb.write(" (");
        xb.write(MemoryUtil.bytesToHumanString(blob.getSize()));
        xb.write(")");
    }

    private void valueToXhtmlEdit(XhtmlBuffer xb) {
        if (blob == null) {
            xb.writeInputHidden(operationInputName, UPLOAD_MODIFY);
            xb.writeInputFile(id, inputName, false);
        } else {
            xb.openElement("div");
            xb.addAttribute("class", "value");
            xb.addAttribute("id", id);

            xb.openElement("div");
            writeBlobShortName(xb);
            xb.closeElement("div");

            xb.openElement("div");
            String keepIdStr = id + UPLOAD_KEEP;
            String script = "var inptxt = document.getElementById('"
                    + StringEscapeUtils.escapeJavaScript(innerId) + "');"
                    + "inptxt.disabled=true;inptxt.value='';";
            xb.writeInputRadio(keepIdStr, operationInputName, UPLOAD_KEEP,
                    true, false, script);
            xb.writeLabel(getText("elements.field.upload.keep"), keepIdStr, null);
            xb.closeElement("div");

            xb.openElement("div");
            String modifyIdStr = id + UPLOAD_MODIFY;
            script = "var inptxt = document.getElementById('"
                    + StringEscapeUtils.escapeJavaScript(innerId) + "');"
                    + "inptxt.disabled=false;inptxt.value='';";
            xb.writeInputRadio(modifyIdStr, operationInputName,
                    UPLOAD_MODIFY, false, false, script);
            xb.writeLabel(getText("elements.field.upload.update"), modifyIdStr, null);
            xb.closeElement("div");

            xb.openElement("div");
            String deleteIdStr = id + UPLOAD_DELETE;
            script = "var inptxt = document.getElementById('"
                    + StringEscapeUtils.escapeJavaScript(innerId) + "');"
                    + "inptxt.disabled=true;inptxt.value='';";
            xb.writeInputRadio(deleteIdStr, operationInputName,
                    UPLOAD_DELETE, false, false, script);
            xb.writeLabel(getText("elements.field.upload.delete"), deleteIdStr, null);
            xb.closeElement("div");

            xb.writeInputFile(innerId, inputName, true);
            xb.writeInputHidden(codeInputName, blob.getCode());

            xb.closeElement("div");
        }
    }

    //**************************************************************************
    // Element implementation
    //**************************************************************************
    public void readFromRequest(HttpServletRequest req) {
        super.readFromRequest(req);

        if (mode.isView(immutable, autogenerated)) {
            return;
        }

        String updateTypeStr = req.getParameter(operationInputName);
        if (updateTypeStr == null) {
            // do nothing
        } else if (updateTypeStr.equals(UPLOAD_KEEP)) {
            if (mode.isCreate()) {
                String code = req.getParameter(codeInputName);
                safeLoadBlob(code);
            }
        } else if (updateTypeStr.equals(UPLOAD_MODIFY)) {
            saveUpload(req);
        } else if (updateTypeStr.equals(UPLOAD_DELETE)) {
            blob = null;
        }
    }

    private void saveUpload(HttpServletRequest req) {
        WebFramework webFramework = WebFramework.getWebFramework();
        try {
            Upload upload = webFramework.getUpload(req, inputName);
            if (upload == null) {
                blob = null;
            } else {
                blob = blobsManager.saveBlob(
                        upload.getInputStream(), 
                        upload.getFilename(),
                        upload.getContentType());
            }
        } catch (Throwable e) {
            logger.warn("Cannot save upload", e);
            throw new Error(e);
        }
    }

    public boolean validate() {
        if (mode.isView(immutable, autogenerated) || (mode.isBulk() && !bulkChecked)) {
            return true;
        }

        boolean result = true;
        if (required && (blob == null)) {
            errors.add(getText("elements.error.field.required"));
            result = false;
        }
        return result;
    }

    public void readFromObject(Object obj) {
        super.readFromObject(obj);
        if (obj == null) {
            blob = null;
        } else {
            String code = (String) accessor.get(obj);
            safeLoadBlob(code);
        }
    }

    protected void safeLoadBlob(String code) {
        if (code == null) {
            blob = null;
        } else {
            try {
                blob = blobsManager.loadBlob(code);
            } catch (IOException e) {
                blob = null;
                blobError = "Cannot load blob";
                logger.warn("Cannot load blob with code {}. Cause: {}",
                        code, e.getMessage());
            }
        }
    }

    public void writeToObject(Object obj) {
        if (blob == null) {
            writeToObject(obj, null);
        } else {
            writeToObject(obj, blob.getCode());
        }
    }

    public Blob getBlob() {
        return blob;
    }

    public void setBlob(Blob blob) {
        this.blob = blob;
    }

    public String getOperationInputName() {
        return operationInputName;
    }

    public void setOperationInputName(String operationInputName) {
        this.operationInputName = operationInputName;
    }

    public String getBlobError() {
        return blobError;
    }

    public void setBlobError(String blobError) {
        this.blobError = blobError;
    }
}
