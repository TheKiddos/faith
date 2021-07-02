$(_ => {
    $(".send-proposal-form").submit(function(event) {
        const sendProposalForm = $(this)

        const sendProposalUrl = sendProposalForm.attr("action");
        const sendProposalSpinner = sendProposalForm.find(".send-proposal-spinner");
        const sendProposalBtn = sendProposalForm.find(".send-proposal-btn")

        sendProposalSpinner.removeClass("visually-hidden");
        sendProposalBtn.prop('disabled', true);

        const formData = sendProposalForm.serialize()

        postFormData(sendProposalUrl, formData, sendProposalSpinner, sendProposalBtn)

        event.preventDefault();
    });
});
