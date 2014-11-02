<%@ page pageEncoding="UTF-8" language="java" trimDirectiveWhitespaces="true" session="false" %>
<%@ include file="../init.jsp" %>

<portlet:actionURL var="editLayoutUrl" escapeXml="false">
	<portlet:param name="action" value="edit-layout" />
	<portlet:param name="id" value="${layout.id}"/>
</portlet:actionURL>

<form accept-charset="UTF-8" method="POST" action="${editLayoutUrl}">
	<portal-ui:form-content>
		<portal-ui:form-title title="Edit layout" />
		<portal-ui:form-input-row message="URL:" path="url" value="${editLayoutModel.url}" />
		<div>
			<label for="parentId">Parent:</label>
			<select name="parentId">
				<option value=""></option>
				<c:forEach items="${layouts}" var="l">
					<c:if test="${l.id ne layout.id}">
						<option value="${l.id}"${editLayoutModel.parentId eq l.id ? ' selected="selected"' : ''}>${l.friendlyUrl}</option>
					</c:if>
				</c:forEach>
			</select>
		</div>
		<portal-ui:form-input-row message="Default layout title language" path="defaultLayoutTitleLanguageId" value="${editLayoutModel.defaultLayoutTitleLanguageId}" />
		<c:forEach items="${editLayoutModel.layoutTitles}" var="layoutTitle">
			<portal-ui:form-input-row message="${layoutTitle.key}" path="layoutTitles[${layoutTitle.key}]" value="${editLayoutModel.layoutTitles[layoutTitle.key]}"/>
		</c:forEach>
		<div>
			<button type="button" class="ajax-submit">Edit layout</button>
		</div>
	</portal-ui:form-content>
</form>