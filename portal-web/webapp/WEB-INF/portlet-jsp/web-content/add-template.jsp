<%@ page pageEncoding="UTF-8" language="java" trimDirectiveWhitespaces="false" session="false" %>
<%@ include file="../init.jsp" %>

<portlet:actionURL portletMode="edit" var="addTemplateUrl">
	<portlet:param name="action" value="add-template"/>
</portlet:actionURL>

<form method="post" action="${addTemplateUrl}">
	<portal-ui:form-content>
		<portal-ui:form-title title="Add new template" />
		<div class="form-row">
			<label for="add-template-name">
				<span>Name</span>
				<input name="name" id="add-template-name" />
			</label>
		</div>
		<div class="form-row">
			<label for="add-template-structureId">
				<span>Structure</span>
				<select name="structureId" id="add-template-structureId">
					<c:forEach items="${structures}" var="structure">
						<option value="${structure.id}"><c:out value="${structure.name}"/></option>
					</c:forEach>
				</select>
			</label>
		</div>
		<div class="form-row">
			<label for="add-template-templateContent">
				<span>Content</span>
				<textarea rows="10" cols="50" name="templateContent" id="add-template-templateContent"></textarea>
			</label>
		</div>
		<div>
			<input type="submit" value="Save"/>
		</div>
	</portal-ui:form-content>
</form>