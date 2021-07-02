AOS.init();

function postFormData( url, data, spinner, btn ) {
    $.post({
        url: url,
        data: data,
    }).done( ( data ) => {
        toastr.success( data );
        setTimeout( _ => {
            this.window.location.reload();
        }, 1000 )
    }).fail(( data ) => {
        toastr.error( data.responseText );
    }).always(_ => {
        btn.prop( 'disabled', false );
        spinner.addClass( "visually-hidden" );
    });
}

const isNavTransparent = $( "nav" ).hasClass( "navbar-transparent" );
const navBar = $( ".navbar" );
$(window).scroll(_ => {
    if ( $(this).scrollTop() > 250 ) {
        navBar.addClass( "sticky-top" );
        navBar.removeClass( "navbar-transparent" );
    }

    if ( $(this).scrollTop() < 220 ) { // Extra space for navbar taking height from the dom during it's change
        navBar.removeClass("sticky-top");
        if (isNavTransparent)
            navBar.addClass("navbar-transparent");
    }
})

// TODO: move to separte file and conditionally add it
$(_ =>{
   try {
       
   } catch ( e ) {
       
   }
});
