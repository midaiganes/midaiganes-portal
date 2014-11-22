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

<portlet:actionURL var="addLayoutSetUrl" escapeXml="false">
	<portlet:param name="action" value="add-layout-set"/>
</portlet:actionURL>
<form accept-charset="UTF-8" action="${addLayoutSetUrl}">
	<portal-ui:form-content>
		<portal-ui:form-title title="Add Layout Set" />
		<portal-ui:form-input-row message="Virtual host" path="host" value="${addLayoutSetModel.host}"/>
		<portal-ui:form-select-row-wrapper message="Theme" path="fullThemeName">
			<select name="fullThemeName">
				<c:forEach items="${themes}" var="theme">
					<option value="${theme.themeName.fullName}"${addLayoutSetModel.fullThemeName eq theme.themeName.fullName ? ' selected="selected"' : '' }>${theme.themeName}</option>
				</c:forEach>
			</select>
		</portal-ui:form-select-row-wrapper>
		<portal-ui:form-buttons>
			<button type="submit">Add layout Set</button>
		</portal-ui:form-buttons>
	</portal-ui:form-content>
</form>