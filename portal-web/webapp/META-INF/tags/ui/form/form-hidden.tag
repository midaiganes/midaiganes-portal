<%@ tag body-content="empty" dynamic-attributes="false" language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ attribute name="name" required="true" %>
<%@ attribute name="value" required="true" %>
<input type="hidden" name="${name}" value="${empty value ? '' : fn:replace(value, '"', '&quot;')}" />