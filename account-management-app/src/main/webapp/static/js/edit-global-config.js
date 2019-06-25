$(document).ready(function () {
    if($("#notifierType").val() === "AWS"){
        $(".rabbitmq-config").each(function() {
            $(this).hide();
        });
    }else{
        $(".aws-config").each(function() {
            $(this).hide();
        });
    }
    $("#notifierType").change(function () {
        if ($(this).val() == "AWS") {
            $(".rabbitmq-config").each(function() {
                $(this).hide();
            });
            $(".aws-config").each(function() {
                if($(this).val() === "N/A"){
                    $(this).val("");
                }
                $(this).show();
            });
        }
        else {
            $(".aws").each(function() {
                $(this).hide();
            });
            $(".rabbitmq-config").each(function() {
                if($(this).val() === "N/A"){
                    $(this).val("");
                }
                $(this).show();
            });
        }
    });
});