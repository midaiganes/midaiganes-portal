<%@ page pageEncoding="UTF-8" language="java" trimDirectiveWhitespaces="false" session="false" %>
<%@ include file="../init.jsp" %>

<portlet:actionURL var="addLayoutSetUrl" />
<div>
	<h3>LayoutSets:</h3>
	<ul>
		<c:forEach items="${layoutSets}" var="layoutSet">
			<li>${layoutSet.id} ${layoutSet.virtualHost}</li>
		</c:forEach>
	</ul>
</div>

<form:form modelAttribute="addLayoutSetModel" acceptCharset="UTF-8" action="${addLayoutSetUrl}">
	<portal-ui:form-content>
		<portal-ui:form-title title="Add Layout Set" />
		<portal-ui:form-input-row message="Virtual host" path="host" />
		<div>
			<input type="submit" value="Add layout Set"/>
		</div>
	</portal-ui:form-content>
</form:form>