<%@ page pageEncoding="UTF-8" language="java" trimDirectiveWhitespaces="true" session="false" %>
<%@ include file="../init.jsp" %>

<portlet:actionURL var="editLayoutSetUrl" escapeXml="false">
	<portlet:param name="action" value="edit-layout-set"/>
</portlet:actionURL>

<portlet:renderURL  var="backUrl"/>
<div>
	<a href="${backUrl}">Back</a>
</div>

<form accept-charset="UTF-8" action="${editLayoutSetUrl}" method="POST">
	<portal-ui:form-content>
		<portal-ui:form-hidden name="id" value="${editLayoutSetModel.id}" />
		<portal-ui:form-title title="Edit Layout Set" />
		<portal-ui:form-input-row message="Virtual host" path="host" value="${editLayoutSetModel.host}"/>
		<portal-ui:form-select-row-wrapper message="Theme" path="fullThemeName">
			<select name="fullThemeName">
				<c:forEach items="${themes}" var="theme">
					<option value="${theme.themeName.fullName}"${editLayoutSetModel.fullThemeName eq theme.themeName.fullName ? ' selected="selected"' : ''}><c:out value="${theme.themeName}" escapeXml="true" /></option>
				</c:forEach>
			</select>
		</portal-ui:form-select-row-wrapper>
		<portal-ui:form-buttons>
			<button type="submit">Edit layout Set</button>
		</portal-ui:form-buttons>
	</portal-ui:form-content>
</form>