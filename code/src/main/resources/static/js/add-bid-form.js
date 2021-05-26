$(_ => {
    const addBidForm = $("#add-bid-form")
    const addBidUrl = addBidForm.attr("action");
    const addBidSpinner = $("#add-bid-spinner");
    const addBidBtn = $("#add-bid-btn")

    addBidForm.submit((event) => {
        addBidSpinner.removeClass("visually-hidden");
        addBidBtn.prop('disabled', true);

        let formData = {
            amount: $("#amount").val(),
            comment: $("#comment").val(),
            projectId: $("#project-id").val(),
            _csrf: $("#add-bid-form input[name=_csrf]").val()
        };

        postFormData(addBidUrl, formData, addBidSpinner, addBidBtn)

        event.preventDefault();
    });
});
