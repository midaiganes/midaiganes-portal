<%@ page pageEncoding="UTF-8" language="java" session="false" trimDirectiveWhitespaces="true" %>
<%@ include file="../init.jsp" %>

<portlet:actionURL var="changePermissionsUrl" escapeXml="false">
	<portlet:param name="id" value="${id}"/>
	<portlet:param name="resource-id" value="${resourceId}"/>
	<portlet:param name="resource-prim-key" value="${resourcePrimKey}"/>
</portlet:actionURL>
<form action="${changePermissionsUrl}" accept-charset="UTF-8" method="POST">
	<c:if test="${not empty success and success}">
		<p class="message">
			Permissions updated.
		</p>
	</c:if>
	<table class="table-1">
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
					<portal-ui:form-hidden name="rows[${permissionsRowStatus.index}].resourcePrimKey" value="${permissionsData.rows[permissionsRowStatus.index].resourcePrimKey }" />
				</th>
				<c:forEach items="${permissionsRow.permissions}" var="permissionValue" varStatus="permissionsValueStatus">
					<td>
						<input type="checkbox" name="rows[${permissionsRowStatus.index}].permissions[${permissionsValueStatus.index}]" value="true"${permissionsData.rows[permissionsRowStatus.index].permissions[permissionsValueStatus.index] eq 'true' ? ' checked="checked"' : ''} />
					</td>
				</c:forEach>
			</tr>
		</c:forEach>
	</table>
	<div class="buttons">
		<button type="button" class="ajax-submit">Save</button>
	</div>
</form>