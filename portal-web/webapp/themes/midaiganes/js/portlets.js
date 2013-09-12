jQuery(function() {
// chat
	function sendChatMessageSuccess(msg) {
		jQuery('#chat-message').val('');
		console.log('send response = ' + msg);
	}
	function sendChatMessage() {
		var chatMessageForm = jQuery('#chat-message-form');
		var _d = chatMessageForm.serialize();
		var _url = chatMessageForm.attr('action');
		jQuery.ajax({
			url:_url,
			type: "POST",
			data:_d,
			dataType:'text',
			async: true,
			success: sendChatMessageSuccess,
			error: function() {
				console.log('send chat message error');
			}
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
		var _url = jQuery('#chat-messages').data('get-messages-url');
		console.log('wait messages...');
		jQuery.ajax({
			url:_url,
			async: true,
			success:function(resp) {
				console.log('success: "' + resp + '"');
				waitMessagesSuccess(resp);
				setTimeout(waitMessages, 100);
			},
			error: function() {
				console.log('wait messages error');
				setTimeout(waitMessages, 100);
			}
		});
	}
	jQuery('body').on('click', '#send-chat-message', sendChatMessage);
	if(jQuery('#chat-messages').length > 0) {
		setTimeout(waitMessages, 100);
	}
// end of chat
});