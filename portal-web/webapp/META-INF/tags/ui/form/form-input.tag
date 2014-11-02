<%@ tag body-content="empty" dynamic-attributes="false" language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ attribute name="name" required="true" %>
<%@ attribute name="id" required="false" %>
<%@ attribute name="value" required="false" %>
<c:if test="${not empty id}"><c:set var="idAttr" value=' id="${id}"' /></c:if>
<input type="text" name="${name}"${empty idAttr ? '' : idAttr} value="${empty value ? '' : fn:replace(value, '"', '&quot;')}" />