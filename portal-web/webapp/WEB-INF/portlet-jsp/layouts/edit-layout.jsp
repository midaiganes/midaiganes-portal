<%@ page pageEncoding="UTF-8" language="java" trimDirectiveWhitespaces="true" session="false" %>
<%@ include file="../init.jsp" %>

<portlet:actionURL var="editLayoutUrl">
	<portlet:param name="action" value="edit-layout" />
	<portlet:param name="id" value="${layout.id}"/>
</portlet:actionURL>

<form:form modelAttribute="editLayoutModel" acceptCharset="UTF-8" action="${editLayoutUrl}">
	<portal-ui:form-content>
		<portal-ui:form-title title="Edit layout" />
		<portal-ui:form-input-row-spring message="URL" path="url" />
		<form:select path="parentId">
			<form:option value=""></form:option>
			<c:forEach items="${layouts}" var="l">
				<c:if test="${l.id ne layout.id}">
					<form:option value="${l.id}">${l.friendlyUrl}</form:option>
				</c:if>
			</c:forEach>
		</form:select>
		<portal-ui:form-input-row-spring message="Default layout title language" path="defaultLayoutTitleLanguageId" />
		<c:forEach items="${editLayoutModel.layoutTitles}" var="layoutTitle">
			<portal-ui:form-input-row-spring message="${layoutTitle.key}" path="layoutTitles[${layoutTitle.key}]"/>
		</c:forEach>
		<div>
			<input type="submit" value="Edit layout"/>
		</div>
	</portal-ui:form-content>
</form:form>