<%@ page pageEncoding="UTF-8" language="java" session="false" trimDirectiveWhitespaces="true" %>
<%@ include file="../init.jsp" %>

<h2>Change page layout</h2>
<ul>
	<c:forEach items="${pageLayouts}" var="pageLayout">
		<portlet:actionURL var="updatePageLayoutUrl">
			<portlet:param name="pageLayoutId" value="${pageLayout.pageLayoutName.fullName}"/>
		</portlet:actionURL>
		<li><a href="${updatePageLayoutUrl}">${pageLayout.pageLayoutName}</a><c:if test="${pageLayout.pageLayoutName eq pageLayoutName}"> (selected)</c:if></li>
	</c:forEach>
</ul>