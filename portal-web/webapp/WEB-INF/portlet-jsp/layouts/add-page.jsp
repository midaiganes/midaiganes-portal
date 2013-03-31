<%@ page pageEncoding="UTF-8" language="java" trimDirectiveWhitespaces="true" session="false" %>
<%@ include file="../init.jsp" %>

<portlet:actionURL var="addLayoutUrl" />

<form:form modelAttribute="addLayoutModel" acceptCharset="UTF-8" action="${addLayoutUrl}" id="add-layout-form">
	<portal-ui:form-content>
		<portal-ui:form-title title="Add layout" />
		<portal-ui:form-input-row message="URL" path="url" />
		<form:select path="parentId">
			<form:option value=""></form:option>
			<c:forEach items="${layouts}" var="layout">
				<form:option value="${layout.id}">${layout.friendlyUrl}</form:option>
			</c:forEach>
		</form:select>
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
			<portlet:renderURL var="editLayoutUrl">
				<portlet:param name="action" value="edit-layout"/>
				<portlet:param name="id" value="${layout.id}"/>
			</portlet:renderURL>
			<li><span><a href="${editLayoutUrl}" class="ajax-replace" data-replace-el="#add-layout-form">${layout.friendlyUrl}</a></span> <a href="${deleteLayoutUrl}">delete</a></li>
		</c:forEach>
	</ul>
</form:form>