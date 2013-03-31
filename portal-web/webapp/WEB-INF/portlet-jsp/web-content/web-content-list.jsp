<%@ page pageEncoding="UTF-8" language="java" trimDirectiveWhitespaces="true" session="false" %>
<%@ include file="../init.jsp" %>

<table>
	<tbody>
		<tr>
			<th>ID</th>
			<th>Title</th>
			<td></td>
		</tr>
	</tbody>
	<tbody>
		<c:forEach items="${webContents}" var="webContent">
			<portlet:actionURL var="setWebContentUrl">
				<portlet:param name="action" value="set-web-content"/>
				<portlet:param name="id" value="${webContent.id}"/>
			</portlet:actionURL>
			<tr>
				<td><a href="${setWebContentUrl}"><c:out value="${webContent.id}"/></a></td>
				<td><a href="${setWebContentUrl}"><c:out value="${webContent.title}"/></a></td>
				<td>
					<portlet:renderURL var="editWebContentUrl">
						<portlet:param name="action" value="edit-web-content"/>
						<portlet:param name="id" value="${webContent.id}"/>
					</portlet:renderURL>
					<a href="${editWebContentUrl}">edit</a>
				</td>
			</tr>
		</c:forEach>
	</tbody>
</table>