// Returns employee name by employeeId
function getEmployeeName(employeeId, callback) {
  $.post("rest/getEmployee",
  {
    employeeId: employeeId,
  },
  function(data){
    callback(data["name"]);
  })
  .done(function(data) {
    //alert("done");
  })
  .fail(function(data) {
    //alert("failure");
  })
  .always(function() {
    //alert("finished");
  });
}

function takeEquipment() {
  var employeeId = $('#txt_employeeId').val();
  var serial = $('#txt_serial').val();
  var reservationType = $('input[name=rad_type]:checked').val();


  $.post("rest/take",
  {
    employeeId: employeeId,
    serial: serial,
    reservationType: reservationType
  },
  function(data){
    var employeeName = "";
    $('#txt_serial').val("");
    $('#txt_serial').focus();
    if($("#status").hasClass("hidden")) {$("#status").removeClass("hidden");}
    if($("#status").hasClass("alert-info")) {$("#status").removeClass("alert-info");}
    if($("#status").hasClass("alert-danger")) {$("#status").removeClass("alert-danger");}
    $("#status").addClass("alert-success");

    // Callback function needed to get result from ajax
    getEmployeeName(employeeId, function(employeeName) {
      $("#msg_type").text("Success: ");
      $("#msg_text").text(data["equipment"]["serial"] + " / " +  data["equipment"]["name"] + " succesfully reserved for user " + employeeName + ".");
    });
    
  })
  .done(function() {
  //alert( "second success" );
  })
  .fail(function(jqXHR, textStatus) {
    $('#txt_serial').val("");
    if($("#status").hasClass("hidden")) {$("#status").removeClass("hidden");}
    if($("#status").hasClass("alert-info")) {$("#status").removeClass("alert-info");}
    if($("#status").hasClass("alert-success")) {$("#status").removeClass("alert-success");}
    $("#status").addClass("alert-danger");

    $("#msg_type").text("Error: ");
    $("#msg_text").text(jqXHR["responseJSON"]["message"]);
  })
  .always(function() {
    //alert( "finished" );
  });
}

function statusWorking() {
  if($("#status").hasClass("hidden")) {$("#status").removeClass("hidden");}
  if($("#status").hasClass("alert-danger")) {$("#status").removeClass("alert-danger");}
  if($("#status").hasClass("alert-success")) {$("#status").removeClass("alert-success");}
  $("#status").addClass("alert-info");
  $("#msg_type").text("Working, please wait... ");
  $("#msg_text").text("If this is your first time taking an item, this may take up to two minutes...");
}

function statusDone() {
  //$("#status").removeclass('alert-info');
}