
// As you can see the ids are nearly the same so I think I can extract duplication into a function but I think it's fine since there are two for now
$(_ => {
    const addCommentForm = $("#add-comment-form")
    const addCommentUrl = addCommentForm.attr("action");
    const addCommentSpinner = $("#add-comment-spinner");
    const addCommentBtn = $("#add-comment-btn")

    addCommentForm.submit((event) => {
        event.preventDefault();

        addCommentSpinner.removeClass("visually-hidden");
        addCommentBtn.prop('disabled', true);

        let formData = {
            text: $("#new-comment-text").val(),
            bidId: $("#new-comment-bid-id").val(),
            _csrf: $("#add-comment-form input[name=_csrf]").val()
        };

        postFormData(addCommentUrl, formData, addCommentSpinner, addCommentBtn);
    });
});
