(function ($) {
    "use strict"; // Start of use strict

    console.log("Payment Ready");

    $('.openPaymentModal').click(function (event) {
        console.log("openPaymentModal click"); 

        $.get("/checkout/", function (data, status) {
            console.log("Data: " + data + "\nStatus: " + status);

            var title = 'Hi, <span>' + data.account.displayName + '</span>' +
                '<img  src="' + data.account.profileImageUrl + '" class="img-circle" style="max-height: 40px;" /> ' +
                'Let\'s buy a Step!';

            $("#paymentModal .modal-title").html(title);
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
                            addAlert(err);
                            return;
                        }
                        $.ajax({
                            type: "POST",
                            url: "/checkout/",
                            data: { amount: getAmount(), payment_method_nonce: payload.nonce },
                            beforeSend: function(xhr) {
                            	var token = $("meta[name='_csrf']").attr("content");
                            	var header = $("meta[name='_csrf_header']").attr("content");
                                xhr.setRequestHeader(header, token);
                                $("#payment-form :submit").prop('disabled', true);
                            },
                            success: function(data, textStatus, jqXHR) {
                            	console.log("success", status);
                            	if(data.status==='FAIL') {
                            		addAlert(data.errors[0]);
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

    
    function addAlert(message) {
    	 var alertElement = '<div class="alert alert-warning alert-dismissible" role="alert">\n' +
         	'<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>\n' +
         	'<strong>Error!</strong> <span>' + message + '</span> ' +
         '</div>';
    	 $("#paymentModal .alertContainer").html(alertElement);
    }
    
    function getAmount() {
        return  $('#amountButtons input:radio:checked').val();
    }

    
})(jQuery); // End of use strict



