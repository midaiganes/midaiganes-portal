<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" language="java" trimDirectiveWhitespaces="true" session="false" %>
<%@ taglib prefix="portal-taglib" uri="http://midaiganes.ee/portal-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="content-box">
	<div class="content-top">
		<c:if test="${!portletNamespace.defaultWindowID}">
			<a href="#" data-window-id="${portletNamespace.windowID}" class="remove-portlet">X</a>
		</c:if>
	</div>
	<div class="content-body">
		${portletContent}
	</div>
</div>