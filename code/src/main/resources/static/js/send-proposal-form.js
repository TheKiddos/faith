$(_ => {
    const sendProposalForm = $("#send-proposal-form")
    const sendProposalUrl = sendProposalForm.attr("action");
    const sendProposalSpinner = $("#send-proposal-spinner");
    const sendProposalBtn = $("#send-proposal-btn")

    sendProposalForm.submit((event) => {
        sendProposalSpinner.removeClass("visually-hidden");
        sendProposalBtn.prop('disabled', true);

        // let formData = {
        //     amount: $("#amount").val(),
        //     comment: $("#comment").val(),
        //     projectId: $("#project-id").val(),
        //     _csrf: $("#add-bid-form input[name=_csrf]").val()
        // };
        const formData = sendProposalForm.serialize()

        postFormData(sendProposalUrl, formData, sendProposalSpinner, sendProposalBtn)

        event.preventDefault();
    });
});
