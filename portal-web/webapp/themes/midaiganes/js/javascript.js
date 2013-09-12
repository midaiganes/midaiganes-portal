(function(window, jQuery) {
	
	var Midaiganes = {
		UI : {
			Dialog : {
				bodyFunctions : [],
				/*
				 * title
				 * body
				 * footer
				 * 
				 * hasLayer
				 */
				open : function(opts) {
					opts = opts || {};
					var existingModal = jQuery('.modal');
					
					if(existingModal.length == 0) {
						var body = jQuery('body');
						if(opts.hasLayer) {
							body.append(jQuery('<div class="curtain"></div>'));							
						}
						var modalHeader = '<div class="modal-header"><button type="button">&times;</button><h3></h3></div>';
						var modalBody = '<div class="modal-body"></div>';
						var modalFooter = '<div class="modal-footer"></div>';
						var modal = jQuery('<div class="modal">' + modalHeader + modalBody + modalFooter + '</div>');
						if(opts.title) {
							jQuery('.modal-header h3', modal).text(opts.title);
						}
						if(opts.footer) {
							jQuery('.modal-footer', modal).html(opts.footer);
						}
						if(opts.body) {
							var modalBodyContent = jQuery('.modal-body', modal);
							modalBodyContent.html(opts.body);
							for(var i in Midaiganes.UI.Dialog.bodyFunctions) {
								Midaiganes.UI.Dialog.bodyFunctions[i](modalBodyContent);
							}
						}
						body.append(modal);
					}
				},
				destroy : function() {
					jQuery('.curtain, .modal').remove();
				}
			}
		}
	};
	
	window.Midaiganes = Midaiganes;
	
	
	Midaiganes.UI.Dialog.bodyFunctions.push(function(bodyContent) {
		// TODO admin
		jQuery('#add-portlet .draggable-portlet-name', bodyContent).draggable({
			revert: "invalid",
			start: function() {
				jQuery('.layout-hole').css('background', 'yellow').css('padding', '10px 0 10px 0');
			},
			stop: function() {
				jQuery('.layout-hole').css('background', '').css('padding', '');
			}
		});
		// TODO end admin		
	});
	
	// TODO admin
	jQuery('.layout-hole').droppable({
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
	// TODO end admin
	
	function openModal(el, hasModalLayer) {
		var that = jQuery(el);
		var _url = that.attr('href');
		var modalTitle = that.data('modal-title');
		var modalHasLayer = that.data('modal-has-layer');
		var hasLayer = modalHasLayer == 'true' ? true : (modalHasLayer == 'false' ? false : hasModalLayer);
		jQuery.ajax({
			url : _url,
			dataType: 'html',
			success: function(d) {
				var options = {'hasLayer': hasLayer};
				if(modalTitle) {
					options.title = modalTitle;
				}
				options.body = d;
				Midaiganes.UI.Dialog.open(options);
			},
			error: function() {
				// TODO
			}
		});
	}
	
	jQuery('body').on('click', 'a.open-modal', function(e) {
		e.preventDefault();
		openModal(jQuery(this), false);
		return false;
	});
	jQuery('body').on('click', 'a.open-dialog', function(e) {
		e.preventDefault();
		openModal(jQuery(this), true);
		return false;
	});
	jQuery('body').on('click', '.modal .modal-header button', function(e) {
		e.preventDefault();
		Midaiganes.UI.Dialog.destroy();
		return false;
	});
	jQuery('body').on('click', 'form .ajax-submit', function(e) {
		e.preventDefault();
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
		return false;
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
		return false;
	});
})(window, jQuery);