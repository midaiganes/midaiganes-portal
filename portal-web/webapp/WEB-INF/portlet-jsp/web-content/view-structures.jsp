<%@ page pageEncoding="UTF-8" language="java" trimDirectiveWhitespaces="false" session="false" %>
<%@ include file="../init.jsp" %>

Structures: 
<ul>
	<c:forEach items="${structures}" var="structure">
		<portlet:renderURL var="viewStructureUrl" portletMode="edit">
			<portlet:param name="action" value="view-structure"/>
			<portlet:param name="id" value="${structure.id}"/>
		</portlet:renderURL>
		<li><a href="${viewStructureUrl}"><c:out value="${structure.name}" /></a></li>
	</c:forEach>
</ul>