window.SPortal = {
	UI: {
		Dialog : {
			counter: 0,
			showOverlay: function(zindex) {
				jQuery('body').append('<div id="m-ui-overlay" class="m-ui-overlay" style="z-index: ' + zindex + '"></div>');
			},
			hideOverlay: function() {
				jQuery('#m-ui-overlay').remove();
			},
			open: function(elem, content, opts) {
				var options = {overlay: true, draggable: false, title: null, closeButton: true};
				jQuery.extend(true, options, opts);
				var c = SPortal.UI.Dialog.counter++;
				var dialogId ='dialog' + c;
				jQuery('body').append('<div id="' + dialogId + '" class="m-ui-dialog" style="z-index:' + (1000 + (c * 2)) +'"></div>');
				dialogId = '#' + dialogId;
				if(!content) {
					content = jQuery(elem).remove();
				}
				var cntnt = '<div class="m-ui-dialog-title">';
				if(options.closeButton) {
					cntnt = cntnt + '<div class="close-dialog">close</div>';
				}
				if(options.title) {
					cntnt = cntnt + options.title;
				}
				content = cntnt + '</div><div class="m-ui-content">' + content + '</div>';
				jQuery(dialogId).html(content);
				
				if(options.overlay) {
					SPortal.UI.Dialog.showOverlay(999 + (c * 2));
				}
				if(options.draggable) {
					jQuery(dialogId).draggable();
				}
				return dialogId;
			},
			close : function() {
				for(i = SPortal.UI.Dialog.counter; i >= 0; i--) {
					var e = jQuery('#dialog' +i);
					if(e.length > 0) {
						jQuery(e).remove();
						SPortal.UI.Dialog.hideOverlay();
						break;
					}
				}
				
			}
		}
	},
	Dialog: {
		open : function(elem, content) {
			SPortal.UI.Dialog.close();
			SPortal.UI.Dialog.open(elem, content);

		},
		close : function() {
			if(SPortal.Dialog.dialog != null) {
				SPortal.Dialog.dialog.dialog('destroy');
				SPortal.Dialog.dialog = null;
			}
		},
		dialog : null
	},
	Portlets: {
		AddRemovePortlet: {
			RemovePortletUrl: null,
			// http://api.jqueryui.com/droppable/
			start: function() {
				jQuery('body').on('click', '.remove-portlet', function(e) {
					e.preventDefault();
					window.location.href = SPortal.Portlets.AddRemovePortlet.RemovePortletUrl.replace('PORTLET_WINDOW_ID', jQuery(this).data('window-id'));
					return false;
				});
				jQuery('#add-portlet .draggable-portlet-name').draggable({
					revert: "invalid",
					start: function() {
						jQuery('.portlet-box').css('background', 'yellow').css('padding', '10px 0 10px 0');
					},
					stop: function() {
						jQuery('.portlet-box').css('background', '').css('padding', '');
					}
				});
				jQuery('.portlet-box').droppable({
					accept: '.draggable-portlet-name',
					activeClass: 'portlet-dropped',
					greedy: true,
					drop: function(event, ui) {
						var portletBoxId = jQuery(this).data('portlet-box');
						jQuery.ajax({
							url: jQuery(ui.draggable).data('add-portlet-url').replace('PORTLET_BOX_ID', portletBoxId),
							success: function(data) {
								// window.location.reload();
							}
						});
					},
					activate: function(event, ui) {
						
					},
					out: function(event, ui) {
						
					}
				});
			},
			end: function() {
				jQuery('.portlet-box').droppable('destroy');
				jQuery('#add-portlet .draggable-portlet-name').draggable('destroy');
			}
		}
	}
};

jQuery(function() {
	jQuery('body').on('click', 'a.open-dialog', function(e) {
		e.preventDefault();
		var u = jQuery(this).attr('href');
		jQuery.ajax({
			url : u,
			dataType: 'html',
			success: function(d) {
				SPortal.Dialog.open(null, d);
			},
			error: function() {
				// TODO
			}
		});
		return false;
	});
	jQuery('body').on('click', 'a.open-modal', function(e) {
		e.preventDefault();
		var u = jQuery(this).attr('href');
		var modalTitle = jQuery(this).data('modal-title');
		jQuery.ajax({
			url : u,
			dataType: 'html',
			success: function(d) {
				var options = {overlay : false, draggable: true};
				if(modalTitle) {
					options.title = modalTitle;
				}
				SPortal.UI.Dialog.open(null, d, options);
			},
			error: function() {
				// TODO
			}
		});
		return false;
	});
	jQuery('body').on('click', 'form .ajax-submit', function() {
		var f = jQuery(this).closest('form');
		var d = f.serialize();
		var _url = f.attr('action');
		jQuery.ajax({
			type: 'POST',
			url: _url,
			data: d,
			dataType: "html",
			success: function(data) {
				f.parent().html(data);
			},
			error: function() {
				// TODO
			}
		});
	});
	jQuery('body').on('click', '.m-ui-dialog .close-dialog', function() {
		SPortal.UI.Dialog.close();
	});
	jQuery('body').on('click', 'a.ajax-replace', function(e) {
		e.preventDefault();
		var _url = jQuery(this).attr('href');
		var replaceElem = jQuery(this).data('replace-el');
		var replaceBody = jQuery(this).data('replace-body');
		jQuery.ajax({
			type: 'GET',
			url: _url,
			dataType: 'html',
			success: function(data) {
				if(replaceElem) {
					jQuery(replaceElem).replaceWith(data);
				}
				else if(replaceBody) {
					jQuery(replaceBody).html(data);
				}
			},
			error: function() {
				// TODO
			}
		});
	});
});