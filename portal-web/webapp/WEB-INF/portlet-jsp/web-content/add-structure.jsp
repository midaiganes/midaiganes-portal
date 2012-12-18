<%@ page pageEncoding="UTF-8" language="java" trimDirectiveWhitespaces="false" session="false" %>
<%@ include file="../init.jsp" %>

<portlet:actionURL portletMode="edit" var="addStructureUrl">
	<portlet:param name="action" value="add-structure"/>
</portlet:actionURL>

<form action="${addStructureUrl}" method="post">
	<portal-ui:form-content>
		<portal-ui:form-title title="Add new structure" />
		<div class="form-row">
			<label for="add-structure-name">
				<span>Name</span>
				<input name="name" id="add-structure-name" />
			</label>
		</div>
		<div>
			<input type="submit" value="Save"/>
		</div>
	</portal-ui:form-content>
</form>
