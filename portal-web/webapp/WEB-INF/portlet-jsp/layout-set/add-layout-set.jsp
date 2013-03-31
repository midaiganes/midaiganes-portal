<%@ page pageEncoding="UTF-8" language="java" trimDirectiveWhitespaces="true" session="false"%>
<%@ include file="../init.jsp" %>

<div>
	<h3>LayoutSets:</h3>
	<ul>
		<c:forEach items="${layoutSets}" var="layoutSet">
			<li>
				<portlet:renderURL var="editLayoutSetUrl">
					<portlet:param name="action" value="edit-layout-set"/>
					<portlet:param name="id" value="${layoutSet.id}"/>
				</portlet:renderURL>
				<a href="${editLayoutSetUrl}">${layoutSet.virtualHost}</a>
			</li>
		</c:forEach>
	</ul>
</div>

<portlet:actionURL var="addLayoutSetUrl">
	<portlet:param name="action" value="add-layout-set"/>
</portlet:actionURL>
<form:form modelAttribute="addLayoutSetModel" acceptCharset="UTF-8" action="${addLayoutSetUrl}">
	<portal-ui:form-content>
		<portal-ui:form-title title="Add Layout Set" />
		<portal-ui:form-input-row message="Virtual host" path="host" />
		<div>
			<input type="submit" value="Add layout Set"/>
		</div>
	</portal-ui:form-content>
</form:form>