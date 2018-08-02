function getProperties() {
    $.post("rest/getEMProperties",
  {
    
  },
  function(data){
    $("#txt_DBurl").val(data["dburl"]);
    $("#txt_DBuser").val(data["dbuser"]);
    $("#pwd_DBpassword").val(data["dbpassword"]);
    $("#txt_DBdriver").val(data["dbdriver"]);
    $("#txt_ADuser").val(data["aduser"]);
    $("#pwd_ADpassword").val(data["adpassword"]);
    $("#txt_ADurl").val(data["adurl"]);
    $("#txt_WORKDAY").val(data["workday"]);
    $("#txt_STARTHOUR").val(data["starthour"]);
    $("#txt_STARTMINUTE").val(data["startminute"]);
    $("#txt_ENDHOUR").val(data["endhour"]);
    $("#txt_ENDMINUTE").val(data["endminute"]);
    $("#txt_TempFilePath").val(data["tempFilePath"]);
  })
  .done(function() {
    
  })
  .fail(function(data) {

  })
  .always(function() {
  
  });
}

function saveProperties(DBurl, DBuser, DBpassword, DBdriver, ADuser, 
                        ADpassword, ADurl, WORKDAY, STARTHOUR, STARTMINUTE,
                        ENDHOUR, ENDMINUTE, TempFilePath) {
    $.post("rest/setEMProperties",
    {
      DBurl: DBurl,
      DBuser: DBuser,
      DBpassword: DBpassword,
      DBdriver: DBdriver,
      ADuser: ADuser,
      ADpassword: ADpassword,
      ADurl: ADurl,
      WORKDAY: WORKDAY,
      STARTHOUR: STARTHOUR,
      STARTMINUTE: STARTMINUTE,
      ENDHOUR: ENDHOUR,
      ENDMINUTE: ENDMINUTE,
      TempFilePath: TempFilePath
    },
    function(data){
      if($("#status").hasClass("hidden")) {$("#status").removeClass("hidden");}
      if($("#status").hasClass("alert-info")) {$("#status").removeClass("alert-info");}
      if($("#status").hasClass("alert-danger")) {$("#status").removeClass("alert-danger");}
      $("#status").addClass("alert-success");
      $("#msg_type").text("Success: ");
      $("#msg_text").text("jeejee");  
      console.log("Sheiving");
      
    })
    .done(function() {

    })
    .fail(function(jqXHR, textStatus) {
      if($("#status").hasClass("hidden")) {$("#status").removeClass("hidden");}
      if($("#status").hasClass("alert-info")) {$("#status").removeClass("alert-info");}
      if($("#status").hasClass("alert-success")) {$("#status").removeClass("alert-danger");}
      $("#status").addClass("alert-danger");
      $("#msg_type").text("Error: ");
      $("#msg_text").text("nounou");  
      console.log("Feiling");
    })
    .always(function() {
      
  });
}

function saveEquipmentBtnAction() {
    if (!$("#txt_DBurl").val() || !$("#txt_DBuser").val() || !$("#pwd_DBpassword").val() ||
      !$("#txt_DBdriver").val() || !$("#txt_ADuser").val() || !$("#pwd_ADpassword").val() ||
      !$("#txt_ADurl").val() || !$("#txt_WORKDAY").val() || !$("#txt_STARTHOUR").val() ||
      !$("#txt_STARTMINUTE").val() || !$("#txt_ENDHOUR").val() || !$("#txt_ENDMINUTE").val() ||
      !$("#txt_TempFilePath").val()) {

        if($("#status").hasClass("hidden")) {$("#status").removeClass("hidden");}
        if($("#status").hasClass("alert-info")) {$("#status").removeClass("alert-info");}
        if($("#status").hasClass("alert-success")) {$("#status").removeClass("alert-danger");}
        $("#status").addClass("alert-danger");
        $("#msg_type").text("Error: ");
        $("#msg_text").text("Empty fields: ");

        if(!$("#txt_DBurl").val()) {
         $("#msg_text").append("DatabaseURL "); 
        }
        if(!$("#txt_DBuser").val()) {
         $("#msg_text").append("DBUser "); 
        }
        if(!$("#pwd_DBpassword").val()) {
         $("#msg_text").append("DBPassword "); 
        }
        if(!$("#txt_DBdriver").val()) {
         $("#msg_text").append("DBDriver "); 
        }
        if(!$("#txt_ADuser").val()) {
         $("#msg_text").append("ADUser "); 
        }
        if(!$("#pwd_ADpassword").val()) {
         $("#msg_text").append("ADPassword "); 
        }
        if(!$("#txt_ADurl").val()) {
         $("#msg_text").append("ADurl "); 
        }
        if(!$("#txt_WORKDAY").val()) {
         $("#msg_text").append("WORKDAY "); 
        }
        if(!$("#txt_STARTHOUR").val()) {
         $("#msg_text").append("STARTHOUR "); 
        }
        if(!$("#txt_STARTMINUTE").val()) {
         $("#msg_text").append("STARTMINUTE "); 
        }
        if(!$("#txt_ENDHOUR").val()) {
         $("#msg_text").append("ENDHOUR "); 
        }
        if(!$("#txt_ENDMINUTE").val()) {
         $("#msg_text").append("ENDMINUTE "); 
        }
        if(!$("#txt_TempFilePath").val()) {
         $("#msg_text").append("TempFilePath"); 
        }
    } 
    else {
        var DBurl = $("#txt_DBurl").val();
        var DBuser = $("#txt_DBuser").val();
        var DBpassword = $("#pwd_DBpassword").val();
        var DBdriver = $("#txt_DBdriver").val();
        var ADuser = $("#txt_ADuser").val();
        var ADpassword = $("#pwd_ADpassword").val();
        var ADurl = $("#txt_ADurl").val();
        var WORKDAY = $("#txt_WORKDAY").val();
        var STARTHOUR = $("#txt_STARTHOUR").val();
        var STARTMINUTE = $("#txt_STARTMINUTE").val();
        var ENDHOUR = $("#txt_ENDHOUR").val();
        var ENDMINUTE = $("#txt_ENDMINUTE").val();
        var TempFilePath = $("#txt_TempFilePath").val();

        console.dir(TempFilePath);
        saveProperties(DBurl, DBuser, DBpassword, DBdriver, ADuser, ADpassword, ADurl, WORKDAY,
            STARTHOUR, STARTMINUTE, ENDHOUR, ENDMINUTE, TempFilePath);
        /*
        saveProperties($("#txt_DBurl").val(), $("#txt_DBuser").val(), $("#pwd_DBpassword").val(),
            $("#txt_DBdriver").val(), $("#txt_ADuser").val(), $("#pwd_ADpassword").val(),
            $("#txt_ADurl").val(), $("#txt_WORKDAY").val(), $("#txt_STARTHOUR").val(),
            $("#txt_STARTMINUTE").val(), $("#txt_ENDHOUR").val(), $("#txt_ENDMINUTE").val(),
            $("#txt_TempFilePath").val());
            */
    }
}