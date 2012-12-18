<%@ page pageEncoding="UTF-8" language="java" trimDirectiveWhitespaces="false" session="false" %>
<%@ include file="../init.jsp" %>
<portlet:renderURL portletMode="edit" var="editUrl" />
web content view <a href="${editUrl}">go to edit</a>


<div>
	${content}
</div>