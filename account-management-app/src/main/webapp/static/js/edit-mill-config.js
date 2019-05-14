$(document).ready(function () {
    if($("#queuetype").val() === "AWS"){
        $(".rabbitmq-config").each(function() {
            $(this).hide();
        });
    }
    $("#queuetype").change(function () {
        if ($(this).val() == "AWS") {
            $(".rabbitmq-config").each(function() {
                $(this).hide();
            });
        }
        else {
            $(".rabbitmq-config").each(function() {
                if($(this).val() === "N/A"){
                    $(this).val("");
                }
                $(this).show();
            });
        }
    });
});