<%@ page pageEncoding="UTF-8" language="java" trimDirectiveWhitespaces="false" session="false" %>
<%@ include file="../init.jsp" %>

<portlet:renderURL portletMode="edit" var="addWebContentUrl">
	<portlet:param name="action" value="add-web-content-with-template"/>
</portlet:renderURL>

<form action="${addWebContentUrl}" method="post">
	<portal-ui:form-content>
		<portal-ui:form-title title="Add new web content" />
		<div class="form-row">
			<label for="add-web-content-templateId">
				<span>Choose template</span>
				<select name="templateId" id="add-web-content-templateId">
					<c:forEach items="${templates}" var="template">
						<option value="${template.id}"><c:out value="${template.name}"/></option>
					</c:forEach>
				</select>
			</label>
		</div>
		<div>
			<input type="submit" value="Publish"/>
		</div>
	</portal-ui:form-content>
</form>
