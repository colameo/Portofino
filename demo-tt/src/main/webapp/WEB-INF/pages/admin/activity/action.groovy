package com.manydesigns.portofino.pageactions.activitystream

import com.manydesigns.portofino.tt.TtUtils

import com.manydesigns.elements.ElementsThreadLocals
import com.manydesigns.elements.blobs.Blob
import com.manydesigns.elements.blobs.BlobManager
import com.manydesigns.elements.servlet.ServletUtils
import com.manydesigns.portofino.di.Inject
import com.manydesigns.portofino.modules.DatabaseModule
import com.manydesigns.portofino.persistence.Persistence
import com.manydesigns.portofino.security.AccessLevel
import com.manydesigns.portofino.security.RequiresPermissions
import javax.servlet.http.HttpServletResponse
import net.sourceforge.stripes.action.RedirectResolution
import net.sourceforge.stripes.action.Resolution
import net.sourceforge.stripes.action.StreamingResolution
import org.apache.shiro.authz.annotation.RequiresAuthentication
import org.hibernate.Session

@RequiresAuthentication
@RequiresPermissions(level = AccessLevel.VIEW)
class MyActivityStreamAction extends ActivityStreamAction {

    public static String SYSTEM_ACTIVTY_SQL = TtUtils.ACTIVITY_SQL +
            "WHERE act.project IS NULL ORDER BY act.id DESC";

    @Inject(DatabaseModule.PERSISTENCE)
    private Persistence persistence;

    @Override
    public void populateActivityItems() {
        Locale locale = context.request.locale;
        Session session = persistence.getSession("tt");
        List items = session.createSQLQuery(SYSTEM_ACTIVTY_SQL).setMaxResults(30).list();

        String keyPrefix = "system.";

        String userImageFormat = "/admin/activity?userImage=&userId=%s&code=%s";

        TtUtils.populateActivityItems(items, activityItems, keyPrefix, locale, userImageFormat);
    }

    //**************************************************************************
    // member image
    //**************************************************************************

    private Long userId;

    public Resolution userImage() {
        if(userId == null) {
            return new RedirectResolution("/images/user-placeholder-40x40.png");
        }
        Map user = (Map) persistence.getSession("tt").get("users", userId);
        if(user.avatar == null) {
            return new RedirectResolution("/images/user-placeholder-40x40.png");
        } else {
            BlobManager mgr = ElementsThreadLocals.blobManager;
            Blob blob = mgr.loadBlob(user.avatar);
            long contentLength = blob.getSize();
            String contentType = blob.getContentType();
            InputStream inputStream = new FileInputStream(blob.getDataFile());

            //Cache blobs (they're immutable)
            HttpServletResponse response = context.getResponse();
            ServletUtils.markCacheableForever(response);

            return new StreamingResolution(contentType, inputStream)
                    .setLength(contentLength)
                    .setLastModified(blob.getCreateTimestamp().getMillis());
        }
    }

    Long getUserId() {
        return userId
    }

    void setUserId(Long userId) {
        this.userId = userId
    }

}