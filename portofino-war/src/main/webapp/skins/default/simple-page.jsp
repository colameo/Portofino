<%
    // Avoid caching of dynamic pages
    response.setHeader("Pragma", "no-cache");
    response.addHeader("Cache-Control", "must-revalidate");
    response.addHeader("Cache-Control", "no-cache");
    response.addHeader("Cache-Control", "no-store");
    response.setDateHeader("Expires", 0);
%>
<%@ page contentType="text/html;charset=ISO-8859-1" language="java" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>
<%@ taglib prefix="mde" uri="/manydesigns-elements"%>
<stripes:layout-definition><%--
--%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
    <html xmlns="http://www.w3.org/1999/xhtml" lang="en">
    <head>
        <meta http-equiv="Content-Type" content="text/html;charset=ISO-8859-1"/>
        <meta http-equiv="Content-Script-Type" content="text/javascript"/>
        <meta http-equiv="Content-Style-Type" content="text/css"/>
        <link rel="stylesheet" type="text/css"
              href="<stripes:url value="/yui-2.8.1/build/reset-fonts-grids/reset-fonts-grids.css"/>"/>
        <link rel="stylesheet" type="text/css"
              href="<stripes:url value="/yui-2.8.1/build/base/base-min.css"/>"/>
        <link rel="stylesheet" type="text/css"
              href="<stripes:url value="/jquery-ui-1.8.9/css/smoothness/jquery-ui-1.8.9.custom.css"/>"/>
        <link rel="stylesheet" type="text/css"
              href="<stripes:url value="/skins/default/portofino.css"/>"/>
        <script type="text/javascript"
                src="<stripes:url value="/jquery-ui-1.8.9/js/jquery-1.4.4.min.js"/>"></script>
        <script type="text/javascript"
                src="<stripes:url value="/jquery-ui-1.8.9/js/jquery-ui-1.8.9.custom.min.js"/>"></script>
        <script type="text/javascript"
                src="<stripes:url value="/elements.js"/>"></script>
        <script type="text/javascript"
                src="<stripes:url value="/skins/default/portofino.js"/>"></script>
        <stripes:layout-component name="customScripts" />
        <title><c:out value="${dispatch.lastPageInstance.page.description}"/></title>
    </head>
    <body>
    <div id="doc3" class="yui-t2">
        <stripes:url var="profileUrl" value="/Profile.action"/>
        <jsp:useBean id="portofinoConfiguration" scope="application" type="org.apache.commons.configuration.Configuration"/>
        <div id="hd">
            <jsp:include page="header.jsp"/>
        </div>
        <div id="bd">
            <stripes:layout-component name="bd" />
        </div>
        <div id="ft">
            <div id="responseTime">
                Page response time: <c:out value="${stopWatch.time}"/> ms.
            </div>
            Powered by <a href="http://www.manydesigns.com/">ManyDesigns Portofino</a>
            <c:out value="${mde:getString(portofinoConfiguration, 'portofino.version')}"/>
        </div>
    </div>
    </body>
    </html>
</stripes:layout-definition>