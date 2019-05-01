$(document).ready(function () {
    if($("#queuetype").val() === "AWS"){
        $('#queuehost-container').hide();
    }
    $("#queuetype").change(function () {
        if ($(this).val() == "AWS" ) {
            $("#queuehost").val("N/A");
            $("#queuehost-container").hide();
        }
        else {
            $("#queuehost").val("");
            $("#queuehost-container").show();
        }
    });
});