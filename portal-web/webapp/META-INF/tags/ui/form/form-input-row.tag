<%@ tag body-content="empty" dynamic-attributes="false" language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="portal-ui" uri="http://midaiganes.ee/portal-tags/ui" %>
<%@ attribute name="message" required="true" %>
<%@ attribute name="path" required="true" %>
<%@ attribute name="value" required="false" %>

<div>
	<label for="${path}">${message}</label>
	<portal-ui:form-input name="${path}" id="${path}" value="${value}" />
</div>