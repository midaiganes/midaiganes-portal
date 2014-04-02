<%@ page pageEncoding="UTF-8" language="java" session="false" trimDirectiveWhitespaces="true" %>
<%@ include file="../init.jsp" %>

<portlet:actionURL var="changePermissionsUrl" escapeXml="false">
	<portlet:param name="id" value="${id}"/>
	<portlet:param name="resource-id" value="${resourceId}"/>
	<portlet:param name="resource-prim-key" value="${resourcePrimKey}"/>
</portlet:actionURL>
<form:form action="${changePermissionsUrl}" modelAttribute="permissionsData" htmlEscape="true">
	<c:if test="${not empty success and success}">
		<p class="message">
			Permissions updated.
		</p>
	</c:if>
	<table>
		<tr>
			<td></td>
			<c:forEach items="${actionsList}" var="actionItem">
				<td>${actionItem}</td>
			</c:forEach>
		</tr>
		<c:forEach items="${permissionsData.rows}" var="permissionsRow" varStatus="permissionsRowStatus">
			<tr>
				<th>
					<span>${permissionsRow.resourceText}</span>
					<form:hidden path="rows[${permissionsRowStatus.index}].resourcePrimKey"/>
				</th>
				<c:forEach items="${permissionsRow.permissions}" var="permissionValue" varStatus="permissionsValueStatus">
					<td><form:checkbox path="rows[${permissionsRowStatus.index}].permissions[${permissionsValueStatus.index}]" value="true"/></td>
				</c:forEach>
			</tr>
		</c:forEach>
	</table>
	<div>
		<button type="button" class="ajax-submit">Save</button>
	</div>
</form:form>