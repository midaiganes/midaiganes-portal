<%@ page pageEncoding="UTF-8" language="java" trimDirectiveWhitespaces="true" session="false" %>
<%@ include file="../init.jsp" %>
<portlet:actionURL var="sendMessageUrl" windowState="exclusive">
	<portlet:param name="send-message" value="1"/>
	<portlet:param name="chat-id" value="1"/>
</portlet:actionURL>
<portlet:resourceURL var="getMessagesUrl">
	<portlet:param name="chat-id" value="1"/>
</portlet:resourceURL>
<script type="text/javascript">
jQuery(function() {
	function sendChatMessageSuccess(msg) {
		jQuery('#chat-message').val('');
	}
	function sendChatMessage() {
		var _d = jQuery('#chat-message-form').serialize();
		jQuery.ajax({
			url:'${sendMessageUrl}',
			type: "POST",
			data:_d,
			dataType:'text',
			success: sendChatMessageSuccess
		});
	}
	function waitMessagesSuccess(msg) {
		if(msg) {
			var messages = jQuery.parseJSON(msg);
			if(messages) {
				var cm = jQuery('#chat-messages');
				for(var i in messages.messages) {
					var m = messages.messages[i];
					if(m.cmd == 'join') {
						jQuery('<li data-user-id="' + m.userId + '"></li>').text(m.username).appendTo(jQuery('#chat-users'));
					} else if(m.cmd == 'msg') {
						var userName = jQuery('#chat-users li[data-user-id='+ m.userId +']').text();
						cm.append(jQuery('<div></div>').text(userName + ": " + m.message));
					} else if(m.cmd == 'quit') {
						jQuery('#chat-users li[data-user-id='+ m.userId +']').remove();
					}
					$("#chat-messages").animate({ scrollTop: $("#chat-messages")[0].scrollHeight}, 1000);
				}
			}
		}
	}
	function waitMessages() {
		jQuery.ajax({
			url:'${getMessagesUrl}',
			success:waitMessagesSuccess
		});
	}
	jQuery('body').on('click', '#send-chat-message', sendChatMessage);
	setInterval(waitMessages, 500);
});
</script>
<div id="chat">
	<div id="chat-messages" class="s9">
	</div>
	<ul id="chat-users" class="s3">
		<c:forEach items="${users}" var="user">
			<li data-user-id="${user.id}"><c:out value="${user.username}" escapeXml="true"/></li>
		</c:forEach>
	</ul>
	<form id="chat-message-form" class="s12">
		<input name="message" id="chat-message" /><button type="button" id="send-chat-message">Send</button>
	</form>
</div>
<style type="text/css">
#chat {
	overflow: hidden;
}
#chat-messages {
	border: 1px solid black;
	box-sizing: border-box;
	height: 400px;
	font-size: 0.9em;
	overflow-y: scroll;
}
#chat-users li {
	margin-left:1em;
}
</style>