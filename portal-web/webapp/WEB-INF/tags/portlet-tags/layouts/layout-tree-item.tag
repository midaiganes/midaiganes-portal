<%@ tag body-content="empty" dynamic-attributes="false" language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="lti" tagdir="/WEB-INF/tags/portlet-tags/layouts" %>
<%@ attribute name="layouts" type="java.util.List" required="true" %>

<ul>
	<c:forEach items="${layouts}" var="layoutItem" varStatus="layoutStatus">
		<c:set var="layout" value="${layoutItem.layout}"/>
		<portlet:actionURL var="deleteLayoutUrl">
			<portlet:param name="action" value="delete"/>
			<portlet:param name="id" value="${layout.id}"/>
		</portlet:actionURL>
		<portlet:renderURL var="editLayoutUrl">
			<portlet:param name="action" value="edit-layout"/>
			<portlet:param name="id" value="${layout.id}"/>
		</portlet:renderURL>
		<li>
			<span>
				<a href="${editLayoutUrl}" class="ajax-replace" data-replace-el="#add-layout-form">${layout.friendlyUrl}</a>
			</span>
			<a href="${deleteLayoutUrl}" class="ajax-replace" data-replace-el="#add-layout-form">delete</a>
			<span>
				<c:if test="${!layoutStatus.first}">
					<portlet:actionURL var="moveUpUrl">
						<portlet:param name="action" value="move-up"/>
						<portlet:param name="id" value="${layout.id}"/>
					</portlet:actionURL>
					<a href="${moveUpUrl}" title="move up" class="ajax-replace" data-replace-el="#add-layout-form">&uarr;</a>
				</c:if>
				<c:if test="${!layoutStatus.last}">
					<portlet:actionURL var="moveDownUrl">
						<portlet:param name="action" value="move-down"/>
						<portlet:param name="id" value="${layout.id}"/>
					</portlet:actionURL>
					<a href="${moveDownUrl}" title="move down" class="ajax-replace" data-replace-el="#add-layout-form">&darr;</a>
				</c:if>
			</span>
			<c:if test="${not empty layoutItem.childs}">
				<lti:layout-tree-item layouts="${layoutItem.childs}"/>
			</c:if>
		</li>
	</c:forEach>
</ul>