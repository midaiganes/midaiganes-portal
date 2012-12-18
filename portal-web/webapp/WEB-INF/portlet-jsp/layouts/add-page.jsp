<%@ page pageEncoding="UTF-8" language="java" trimDirectiveWhitespaces="false" session="false" %>
<%@ include file="../init.jsp" %>

<portlet:actionURL var="addLayoutUrl" />

<form:form modelAttribute="addLayoutModel" acceptCharset="UTF-8" action="${addLayoutUrl}">
	<portal-ui:form-content>
		<portal-ui:form-title title="Add layout" />
		<portal-ui:form-input-row message="URL" path="url" />
		<div>
			<input type="submit" value="Add layout"/>
		</div>
	</portal-ui:form-content>
	
	<h3>Layouts:</h3>
	<ul>
		<c:forEach items="${layouts}" var="layout">
			<portlet:actionURL var="deleteLayoutUrl">
				<portlet:param name="action" value="delete"/>
				<portlet:param name="id" value="${layout.id}"/>
			</portlet:actionURL>
			<li>${layout.friendlyUrl} <a href="${deleteLayoutUrl}">delete</a></li>
		</c:forEach>
	</ul>
</form:form>