$(_ => {
    const addBidForm = $("#add-bid-form")
    const addBidUrl = addBidForm.attr("action");
    const addBidSpinner = $("#add-bid-spinner");

    addBidForm.submit((event) => {
        addBidSpinner.removeClass("visually-hidden");
        let formData = {
            amount: $("#amount").val(),
            comment: $("#comment").val(),
            projectId: $("#project-id").val(),
            _csrf: $("input[name=_csrf]").val()
        };

        $.post({
            url: addBidUrl,
            data: formData,
        }).done((data) => {
            toastr.success(data);
            setTimeout(_ => {
                this.window.location.reload();
            }, 1000)
        }).fail((data) => {
            toastr.error(data.responseText);
        }).always(_ => {
            addBidSpinner.addClass("visually-hidden");
        });

        event.preventDefault();
    });
});
