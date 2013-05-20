<%@ tag body-content="scriptless" dynamic-attributes="false" language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ attribute name="message" required="true" %>
<%@ attribute name="path" required="true" %>

<div class="form-row">
	<label for="${path}">
		<span>${message}</span>
		<jsp:doBody />
	</label>
</div>