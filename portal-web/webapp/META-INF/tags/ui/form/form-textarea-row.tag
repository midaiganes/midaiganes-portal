<%@ tag body-content="empty" dynamic-attributes="false" language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ attribute name="message" required="true" %>
<%@ attribute name="path" required="true" %>

<div class="form-row">
	<label for="${path}">
		<span>${message}</span>
		<textarea name="${path}" rows="4" cols="50"></textarea>
	</label>
</div>