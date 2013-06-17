<%@ page pageEncoding="UTF-8" language="java" trimDirectiveWhitespaces="true" session="false" %>
<%@ include file="../init.jsp" %>

<style type="text/css">
	dl.user-info {
		width: 70%;
	}
	div.display-picture {
		width: 30%;
		padding: 1em;
	}
	dl.user-info, div.display-picture {
		box-sizing:border-box;
		-moz-box-sizing:border-box; /* Firefox */
		float:left;
	}
	.user-profile {
		overflow:hidden;
	}
</style>
<section class="user-profile current-user">
	<div class="display-picture">
		<c:choose>
			<c:when test="${not empty userProfile.pictureUrl}">
				<c:set var="displayPictureAlt"><c:out value="${userProfile.username}" escapeXml="true"/> display picture</c:set>
				<img alt="${displayPictureAlt}" src="${userProfile.pictureUrl}" />
			</c:when>
			<c:otherwise>
			</c:otherwise>
		</c:choose>
	</div>
	<dl class="user-info">
		<dt>Username:</dt>
		<dd><c:out value="${userProfile.username}" escapeXml="true"/></dd>
		
		<dt>Real name:</dt>
		<dd>Test TODO</dd>
		
		<dt>Date of birth:</dt>
		<dd>01.01.2000</dd>
		
		<dt>Age:</dt>
		<dd>100</dd>
	</dl>
</section>
