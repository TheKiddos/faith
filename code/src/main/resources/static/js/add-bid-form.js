$(_ => {
    const addBidForm = $("#add-bid-form")
    const addBidUrl = addBidForm.attr("action");

    addBidForm.submit((event) => {
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
        });

        event.preventDefault();
    });
});
