<%@ tag body-content="empty" dynamic-attributes="false" language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ attribute name="paginatorContext" type="ee.midaiganes.model.PaginatorContext" required="true" %>

<c:forEach var="i" begin="1" end="${paginatorContext.numberOfPages}" step="1">
	<portlet:renderURL var="paginatorUrl">
		<portlet:param name="start" value="${i}"/>
		<portlet:param name="items-on-page" value="${paginatorContext.itemsOnPage}"/>
	</portlet:renderURL>
	<a href="${paginatorUrl}">${i}</a>
</c:forEach>
<table>
	<thead>
		<tr>
			<c:forEach items="${paginatorContext.headers}" var="header">
				<td><c:out value="${header}" /></td>
			</c:forEach>
		</tr>
	</thead>
	<tbody>
		<%-- TODO --%>
	</tbody>
</table> 