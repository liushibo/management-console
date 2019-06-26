$(document).ready(function () {
    if($("#notifiertype").val() === "AWS"){
        $(".rabbitmq-config").each(function() {
            $(this).hide();
        });
    }else{
        $(".aws-config").each(function() {
            $(this).hide();
        });
    }
    $("#notifiertype").change(function () {
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
            $(".aws-config").each(function() {
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