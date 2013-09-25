<%@ page pageEncoding="UTF-8" language="java" trimDirectiveWhitespaces="true" session="false" %>
<%@ include file="../init.jsp" %>
<portlet:actionURL var="sendMessageUrl" windowState="exclusive">
	<portlet:param name="send-message" value="1"/>
	<portlet:param name="chat" value="${chatid}"/>
</portlet:actionURL>
<portlet:resourceURL var="getMessagesUrl">
	<portlet:param name="chat" value="${chatid}"/>
</portlet:resourceURL>

<div class="row">
	<div class="box9">
		<div class="chat" id="chat-messages" data-get-messages-url="${getMessagesUrl}"></div>
	</div>
	<div class="box3">
		<ul id="chat-users" class="chat">
			<c:forEach items="${chatusers}" var="user">
				<li data-user-id="${user.id}"><c:out value="${user.username}" escapeXml="true"/></li>
			</c:forEach>
		</ul>
	</div>
</div>
<form action="${sendMessageUrl}" id="chat-message-form" method="post">
	<div class="row">
		<div class="box9 text-right">
			<label for="private-chat">
				Private chat: <input type="checkbox" id="to-user-id" name="to" value="" disabled="disabled"/>
			</label>
		</div>
	</div>
	<div class="inline-row input-append">
		<input type="text" class="chat-input" name="message" id="chat-message" /><button type="button" id="send-chat-message">Send</button>
	</div>
</form>