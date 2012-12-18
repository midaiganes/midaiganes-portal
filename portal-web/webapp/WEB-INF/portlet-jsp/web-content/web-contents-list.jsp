<%@ page pageEncoding="UTF-8" language="java" trimDirectiveWhitespaces="false" session="false" %>
<%@ include file="../init.jsp" %>

Web contents list

<ul>
	<c:forEach items="${webContents}" var="webContent">
		<portlet:actionURL var="setWebContent" portletMode="edit">
			<portlet:param name="action" value="set-web-content"/>
			<portlet:param name="id" value="${webContent.id}"/>
		</portlet:actionURL>
		<li><a href="${setWebContent}">${webContent.title}</a></li>
	</c:forEach>
</ul>