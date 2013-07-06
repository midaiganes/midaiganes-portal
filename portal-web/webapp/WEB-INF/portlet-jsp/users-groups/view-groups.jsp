<%@ page pageEncoding="UTF-8" language="java" session="false" trimDirectiveWhitespaces="true" %>
<%@ include file="../init.jsp" %>

<table>
	<tr>
		<th>Group id</th>
		<th>Name</th>
		<th>Is user group</th>
	</tr>
	<c:forEach items="${groups}" var="g">
		<tr>
			<td>${g.id}</td>
			<td><c:out value="${g.name}"/></td>
			<td>${g.userGroup}</td>
			<td>
				<portlet:actionURL var="deleteGroupUrl">
					<portlet:param name="action" value="delete-group"/>
					<portlet:param name="group-id" value="${g.id}"/>
				</portlet:actionURL>
				<a href="${deleteGroupUrl}">Delete</a>
			</td>
		</tr>
	</c:forEach>
</table>

<portlet:actionURL var="addGroupUrl">
	<portlet:param name="action" value="add-group"/>
</portlet:actionURL>
<form method="post" action="${addGroupUrl}">
	<portal-ui:form-content>
		<portal-ui:form-title title="Add Group" />
		<portal-ui:form-input-row message="Name" path="groupName" />
		<portal-ui:form-select-row message="Is user group" path="userGroup">
			<option value="true">TRUE</option>
			<option value="false">FALSE</option>
		</portal-ui:form-select-row>
		<div>
			<input type="submit" value="Add Group"/>
		</div>
	</portal-ui:form-content>
</form>