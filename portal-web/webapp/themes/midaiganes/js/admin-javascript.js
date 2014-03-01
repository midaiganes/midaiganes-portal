(function(window, jQuery) {
	jQuery('.layout-hole').sortable({
		placeholder: "ui-state-highlight",
		revert: true,
		update: function(event, ui) {
			console.log('update..')
			console.log(ui);
			if(ui.item.hasClass('draggable-portlet-name')) {
				// new portlet
				var portletBoxId = jQuery(this).data('portlet-box');
				jQuery.ajax({
					url: jQuery(ui.item).data('add-portlet-url').replace('PORTLET_BOX_ID', portletBoxId),
					success: function(data) {
						// window.location.reload();
						console.log('add portlet success');
					},
					error: function() {
						// TODO
						console.log('error add portlet');
					}
				});
			} else if(ui.item.hasClass('portlet')) {
				var prevIndex = ui.item.midaiganes.startIndex;
				var currentIndex = jQuery(ui.item).index();
				var portletBoxId = jQuery(this).data('portlet-box');
				var windowId = jQuery(ui.item).data('window-id');
				var movePortletUrl = window.Midaiganes.Admin.movePortletUrl;
				movePortletUrl = movePortletUrl.replace('WINDOW_ID', windowId).replace('PORTLET_BOX_ID', portletBoxId).replace('BOX_INDEX', currentIndex);
				jQuery.ajax({
					url : movePortletUrl,
					success: function(data) {
						// window.location.reload()
						console.log('move portlet success');
					},
					error: function() {
						// TODO
						console.log('error move portlet');
					}
				});
			}
		},
		start: function(event, ui) {
			console.log('start...');
			console.log(ui);
			var index = jQuery(ui.item).index();
			ui.helper.midaiganes = {
				'startIndex' : index
			}
		},
		receive: function(event, ui) {
			console.log('receive...');
			console.log(ui);
		},
		over: function(event, ui) {
			console.log('over...');
			console.log(ui);
		}
	});
	jQuery('.layout-hole').disableSelection();
	window.Midaiganes.UI.Dialog.bodyFunctions.push(function(bodyContent) {
		// TODO admin
		jQuery('#add-portlet .draggable-portlet-name', bodyContent).draggable({
			revert: "invalid",
			//helper: 'clone',
			helper: function() {
				var t = jQuery(this);
				var c = t.clone();
				return c;
			},
			connectToSortable: '.layout-hole',
			start: function(event, ui) {
				ui.helper.midaiganes = 'new';
			},
			stop: function() {
			}
		});
	});
})(window, jQuery);