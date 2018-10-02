(function ($) {
    "use strict"; // Start of use strict

    console.log("Payment Ready");

    $('.openPaymentModal').click(function (event) {
        console.log("openPaymentModal click"); 

        var productCode = $(this).data('product-code');
        
        $("#payment-form input[name=productCode]").val(productCode);
        
        
        $.get("/checkout/", function (data, status) {
            console.log("Data: " + data + "\nStatus: " + status);

            $("#paymentModal").modal("show");

            
            // braintree integration
            braintree.dropin.create({
                authorization: data.paymentToken,
                container: '#bt-dropin',
                paypal: {
                    flow: 'vault'
                }
            }, function (createErr, instance) {
            	
            	$("#payment-form").submit(function(event) {
                	/* stop form from submitting normally */
                    event.preventDefault();
                    
                    instance.requestPaymentMethod(function (err, payload) {
                        if (err) {
                            console.log('Error', err);
                            showMessage('warning', err);
                            return;
                        }
                        $.ajax({
                            type: "POST",
                            url: "/checkout/",
                            data: { code: getSelectedCode(), payment_method_nonce: payload.nonce },
                            beforeSend: function(xhr) {
                            	var token = $("meta[name='_csrf']").attr("content");
                            	var header = $("meta[name='_csrf_header']").attr("content");
                                xhr.setRequestHeader(header, token);
                                $("#payment-form :submit").prop('disabled', true);
                            },
                            success: function(data, textStatus, jqXHR) {
                            	console.log("success", status);
                            	if(!data.isSuccess) {
                            		showMessage('warning', data.errors);
                            	} else {
                            		showMessage('success', 'Great you are now the prode owner of a new Step!'
                            				+ '<br>Your transaction id is ' + data.transactionId 
                            				+ '<br>You can see it under your payments!');
                            	}
                            },
                            error: function(request, status, error) {
                            	console.log("error", status);
                            },
                            complete: function() {
                            	$("#payment-form :submit").removeAttr('disabled');
                            }
                        });
                    });
                });
            });
        });
    });

    
    function showMessage(sevirity, message) {
    	var msg;
    	if($.isArray( message )) {
    		msg = blkstr.join("<br>")
    	} else {
    		msg = message;
    	}
    	
    	 var alertElement = '<div class="alert alert-' + sevirity + ' alert-dismissible" role="alert">\n' +
         	'<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>\n' +
         	'<strong>Error!</strong> <span>' + message + '</span> ' +
         '</div>';
    	 
    	 $("#paymentModal .alertContainer").html(alertElement);
    }
    
    function getSelectedCode() {
        return  $('#amountButtons input:radio:checked').val();
    }

    
})(jQuery); // End of use strict



