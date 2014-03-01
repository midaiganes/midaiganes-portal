<%@ page pageEncoding="UTF-8" language="java" trimDirectiveWhitespaces="true" session="false" %>
<%@ taglib prefix="lti" tagdir="/WEB-INF/tags/portlet-tags/layouts" %>
<%@ include file="../init.jsp" %>

<portlet:actionURL var="addLayoutUrl" escapeXml="false" />

<form:form modelAttribute="addLayoutModel" acceptCharset="UTF-8" action="${addLayoutUrl}" id="add-layout-form" htmlEscape="true">
	<portal-ui:form-content>
		<portal-ui:form-title title="Add layout" />
		<portal-ui:form-input-row message="URL:" path="url" />
		<div>
			<label for="parentId">Parent:</label>
			<form:select path="parentId">
				<form:option value=""></form:option>
				<c:forEach items="${layouts}" var="layout">
					<form:option value="${layout.layout.id}">${layout.layout.friendlyUrl}</form:option>
				</c:forEach>
			</form:select>
		</div>
		<div>
			<button type="button" class="ajax-submit">Add layout</button>
		</div>
	</portal-ui:form-content>
	
	<h3>Layouts:</h3>
	<lti:layout-tree-item layouts="${layouts}"/>
</form:form>