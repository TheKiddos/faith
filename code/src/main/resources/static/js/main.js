AOS.init();

function postFormData(url, data, spinner, btn) {
    $.post({
        url: url,
        data: data,
    }).done((data) => {
        toastr.success(data);
        setTimeout(_ => {
            this.window.location.reload();
        }, 1000)
    }).fail((data) => {
        toastr.error(data.responseText);
    }).always(_ => {
        btn.prop('disabled', false);
        spinner.addClass("visually-hidden");
    });
}
