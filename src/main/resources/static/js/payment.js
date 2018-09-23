(function ($) {
    "use strict"; // Start of use strict

    console.log("Payment Ready");

    $('.openPaymentModal').click(function (event) {
        console.log("openPaymentModal click");
        event.preventDefault(); // To prevent following the link (optional)

        $.get("/checkout/", function (data, status) {
            console.log("Data: " + data + "\nStatus: " + status);

            var title = 'Hi, <span>' + data.account.displayName + '</span>' +
                '<img  src="' + data.account.profileImageUrl + '" class="img-circle" style="max-height: 40px;" /> ' +
                'Let\'s buy a Step!';

            $("#paymentModal .modal-title").html(title);


            $("#paymentModal").modal("show");

            var form = document.querySelector('#payment-form');
            // braintree integration
            braintree.dropin.create({
                authorization: data.paymentToken,
                container: '#bt-dropin',
                paypal: {
                    flow: 'vault'
                }
            }, function (createErr, instance) {

                form.addEventListener('submit', function (event) {
                    event.preventDefault();

                    instance.requestPaymentMethod(function (err, payload) {
                        if (err) {
                            console.log('Error', err);

                            var alertElement = '<div class="alert alert-warning alert-dismissible" role="alert">\n' +
                                                    '<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>\n' +
                                                    '<strong>Error!</strong> <span>' + err + '</span> ' +
                                                '</div>';

                            $("#paymentModal .alertContainer").html(alertElement);
                            return;
                        }
                        // Add the nonce to the form and submit
                        document.querySelector('#amount').value = getAmount();
                        document.querySelector('#nonce').value = payload.nonce;
                        //form.submit();
                        alert("Submit payment");
                    });
                });

            });
        });

    });

    function getAmount() {
        return  $('#amountButtons input:radio:checked').val();
    }

})(jQuery); // End of use strict



