<%@ page contentType="text/html;charset=ISO-8859-1" language="java"
         pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>
<%@taglib prefix="mde" uri="/manydesigns-elements" %>
<stripes:layout-definition>
    <div class="portlet">
        <jsp:useBean id="actionBean" scope="request"
                     type="com.manydesigns.portofino.actions.PortletAction"/>
        <stripes:form action="${actionBean.dispatch.absoluteOriginalPath}" method="post">
            <div class="portletHeader">
                <stripes:layout-component name="portletHeader">
                    <div class="yui-g">
                        <div class="portletTitle">
                            <h1>
                            <stripes:layout-component name="portletTitle">
                                portletTitle
                            </stripes:layout-component>
                            </h1>
                        </div>
                        <div class="portletHeaderButtons">
                            <button name="configure" class="wrench">Configure</button>
                        </div>
                    </div>
                </stripes:layout-component>
            </div>
            <div class="portletBody">
                <stripes:layout-component name="portletBody">
                    Portlet body
                </stripes:layout-component>
            </div>
            <div class="portletFooter">
                <stripes:layout-component name="portletFooter">
                    Portlet footer
                </stripes:layout-component>
            </div>
        </stripes:form>
    </div>
</stripes:layout-definition>
