<%@ page pageEncoding="UTF-8" language="java" trimDirectiveWhitespaces="true" session="false" %>
<%@ taglib prefix="lti" tagdir="/WEB-INF/tags/portlet-tags/layouts" %>
<%@ include file="../init.jsp" %>

<portlet:actionURL var="addLayoutUrl" escapeXml="false" />

<form accept-charset="UTF-8" action="${addLayoutUrl}" id="add-layout-form">
	<portal-ui:form-content>
		<portal-ui:form-title title="Add layout" />
		<portal-ui:form-input-row message="URL:" path="url" value="${addLayoutModel.url}" />
		<div>
			<label for="parentId">Parent:</label>
			<select name="parentId">
				<option value=""></option>
				<c:forEach items="${layouts}" var="layout">
					<option value="${layout.layout.id}"${addLayoutModel.parentId eq layout.layout.id ? ' selected="selected"' : '' }>${layout.layout.friendlyUrl}</option>
				</c:forEach>
			</select>
		</div>
		<div>
			<button type="button" class="ajax-submit">Add layout</button>
		</div>
	</portal-ui:form-content>
	
	<h3>Layouts:</h3>
	<lti:layout-tree-item layouts="${layouts}"/>
</form>