<%@ page pageEncoding="UTF-8" language="java" trimDirectiveWhitespaces="true" session="false" %>
<%@ include file="../init.jsp" %>


chat....
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
		console.log('msg='+msg);
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
					cm.append(jQuery('<div></div>').text(messages.messages[i].userId + ": " + messages.messages[i].message));
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
<div id="chat-messages">
</div>
<form id="chat-message-form">
	<input name="message" id="chat-message" /><button type="button" id="send-chat-message">Send</button>
</form>