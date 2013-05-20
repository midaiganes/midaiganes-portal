<%@ tag body-content="empty" dynamic-attributes="false" language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ attribute name="message" required="true" %>
<%@ attribute name="path" required="true" %>

<div>
	<label for="${path}">${message}</label>
	<input type="text" name="${path}" id="${path}"/>
</div>