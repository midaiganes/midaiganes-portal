<%@ page pageEncoding="UTF-8" language="java" trimDirectiveWhitespaces="true" session="false" %>
<%@ include file="../init.jsp" %>

<portlet:actionURL var="editLayoutSetUrl">
	<portlet:param name="action" value="edit-layout-set"/>
</portlet:actionURL>

<portlet:renderURL  var="backUrl"/>
<div>
	<a href="${backUrl}">Back</a>
</div>

<form:form modelAttribute="editLayoutSetModel" acceptCharset="UTF-8" action="${editLayoutSetUrl}">
	<portal-ui:form-content>
		<portal-ui:form-title title="Edit Layout Set" />
		<portal-ui:form-input-row-spring message="Virtual host" path="host" />
		<portal-ui:form-select-row-wrapper message="Theme" path="fullThemeName">
			<form:select path="fullThemeName">
				<c:forEach items="${themes}" var="theme">
					<form:option value="${theme.themeName.fullName}">${theme.themeName}</form:option>
				</c:forEach>
			</form:select>
		</portal-ui:form-select-row-wrapper>
		<div>
			<form:hidden path="id"/>
			<input type="submit" value="Edit layout Set"/>
		</div>
	</portal-ui:form-content>
</form:form>