<%@ tag body-content="scriptless" dynamic-attributes="false" language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="portal-ui" uri="http://midaiganes.ee/portal-tags/ui" %>
<%@ attribute name="message" required="true" %>
<%@ attribute name="path" required="true" %>

<portal-ui:form-select-row-wrapper message="${message}" path="${path}">
	<select name="${path}">
		<jsp:doBody />
	</select>
</portal-ui:form-select-row-wrapper>