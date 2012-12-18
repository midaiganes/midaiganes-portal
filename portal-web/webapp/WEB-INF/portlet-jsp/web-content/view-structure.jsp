<%@ page pageEncoding="UTF-8" language="java" trimDirectiveWhitespaces="false" session="false" %>
<%@ include file="../init.jsp" %>

<h3>Structure:</h3>
<dl>
	<dt>Name:</dt>
	<dd><c:out value="${structure.name}" /></dd>
</dl>
<h4>Fields:</h4>
<c:forEach items="${structure.structureFields}" var="field">
	<p><c:out value="${field.fieldName}"/> <c:out value="${field.fieldType}"/></p>
</c:forEach>

<portlet:actionURL var="addFieldUrl">
	<portlet:param name="action" value="add-structure-field"/>
	<portlet:param name="structureId" value="${structure.id}"/>
</portlet:actionURL>
<form method="post" action="${addFieldUrl}">
	<div class="form-row">
		<label>
			<span>Name</span>
			<input type="text" name="name" />
		</label>
	</div>
	<div class="form-row">
		<label>
			<span>Field type</span>
			<select name="fieldType">
				<option value="textarea">Textarea</option>
			</select>
		</label>
	</div>
	<div><input type="submit" value="Submit"/></div>
</form>