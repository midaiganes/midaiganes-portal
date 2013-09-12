<%@ page pageEncoding="UTF-8" language="java" trimDirectiveWhitespaces="true" session="false" %>
<%@ include file="../init.jsp" %>
<%-- TODO --%>
<c:if test="${chatisfull}">
	<div>Chat is full</div>
</c:if>
<c:if test="${chatnotfound}">
	<div>Chat not found</div>
</c:if>
<%-- TODO --%>
<div class="row">
	<c:forEach items="${chats}" var="chat">
		<portlet:actionURL var="joinChatUrl">
			<portlet:param name="join" value="${chat.id}"/>
		</portlet:actionURL>
		<div class="box3">
			<div class="well">
				<h3><a href="${joinChatUrl}">One and only chat</a></h3>
				<div>Users: ${chat.users}</div>
				<%-- TODO
				<div>Playing: 20</div>
				 --%>
				<a href="${joinChatUrl}" class="text-right">Join</a>
			</div>
		</div>
	</c:forEach>
</div>