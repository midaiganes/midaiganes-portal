<%@ page pageEncoding="UTF-8" language="java" trimDirectiveWhitespaces="true" session="false" %>
<%@ include file="../init.jsp" %>

<div id="add-portlet">
	<c:forEach items="${portletNames}" var="name">
		<portlet:actionURL var="addPortletUrl">
			<portlet:param name="action" value="add-portlet"/>
			<portlet:param name="portletId" value="${name.fullName}"/>
			<portlet:param name="portletBoxId" value="PORTLET_BOX_ID" />
		</portlet:actionURL>
		<div class="draggable-portlet-name" data-add-portlet-url="${addPortletUrl}"><a href="#"><c:out value="${name}" /></a></div>
	</c:forEach>
</div>