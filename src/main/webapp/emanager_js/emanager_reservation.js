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

function returnSingle() {
   var serial = $('#txt_serial').val();
  $.post("rest/returnSingle",
    {
      serial: serial,
    },
    function(data){
      $('#txt_serial').val("");
      $('#txt_serial').focus();
      if($("#status").hasClass("hidden")) {$("#status").removeClass("hidden");}
      if($("#status").hasClass("alert-info")) {$("#status").removeClass("alert-info");}
      if($("#status").hasClass("alert-danger")) {$("#status").removeClass("alert-danger");}
      $("#status").addClass("alert-success");
      $("#msg_type").text("Success: ");
      $('#msg_text').text("Equipment with serial number " + serial + " returned");
    })
    .done(function() {
      //alert( "second success" );
    })
    .fail(function(jqXHR, textStatus) {
      $("#status").removeClass('alert-success');
      $("#status").addClass('alert-danger');
      $("#status").removeClass('hidden');
      $("#msg_type").text("Error: ");
      $('#msg_text').text(jqXHR["responseJSON"]["message"]); 
    })
    .always(function() {
      //alert( "finished" );
  });
}

function findReservations() {
  var employeeId = $('#txt_employeeId').val();
  
  $.post("rest/getbyEmployeeId",
  {
    employeeId: employeeId,
  },
  function(data){
    $('#tbl_reservations tbody').empty();
    $.each(data, function(i, item) {
      // Converts epoch date to browser timezone date
      var date_taken = new Date(item["dateTake"] / 1000);
      var date_taken_timezoned = new Date(0); // The 0 there is the key, which sets the date to the epoch
      date_taken_timezoned.setUTCSeconds(date_taken);
       
      $('#tbl_reservations tbody').append('<tr class="child"><td><input type="checkbox" class="form-check-input check" name="resIds" value="' + 
        item.reservationId + '"></td><td>' +
        item["equipment"]["serial"] + '</td><td>' + 
        item["equipment"]["name"] + '</td><td>' + 
        date_taken_timezoned + '</td><td>' +                                     
        item["equipment"]["equipmenttype"]["typeName"] + '</td></tr>');
    });

    var employeeName = "";
    if($("#tbl_reservations").hasClass("hidden")) {$("#tbl_reservations").removeClass('hidden');}
    if($("#btn_submit").hasClass("hidden")) {$("#btn_submit").removeClass('hidden');}
    if($("#status").hasClass("hidden")) {$("#status").removeClass('hidden');}
    if($("#status").hasClass("alert-danger")) {$("#status").removeClass('alert-danger');}
    if($("#status").hasClass("alert-info")) {$("#status").removeClass('alert-info');}
    $("#status").addClass('alert-success');
    // Callback function needed to get result from ajax
    getEmployeeName(employeeId, function(employeeName) {
      $("#msg_type").text("Success: ");
      $('#msg_text').text("Reservations for " + employeeName + " retrieved");
    });
    
  })
  .done(function() {
    //alert( "second success" );
  })
  .fail(function(jqXHR, textStatus) {
    $('#txt_equipmentId').val("");
    //$("#status").removeClass('alert-success');
    
    if($("#status").hasClass("hidden")) {$("#status").removeClass('hidden');}
    if($("#status").hasClass("alert-success")) {$("#status").removeClass('alert-success');}
    //if($("#status").hasClass("alert-danger")) {$("#status").removeClass('alert-danger');}
    $("#status").addClass('alert-danger');
    $("#msg_type").text("Error: ");
    $('#msg_text').text(jqXHR["responseJSON"]["message"]);  
  })
  .always(function() {
    //alert( "finished" );
  });
}

function returnMultiple() {
   var ids = [];

  // Initializing array with Checkbox checked values
    $("input[name='resIds']:checked").each(function(){
        ids.push(this.value);
    });

  $.post("rest/returnMultiple",
    {
      resIds: JSON.stringify(ids),
    },
    function(data){
      findReservations();
    })
    .done(function() {
      //$('#status').text("Equipment returned.");
      $('#tbl_reservations tbody').text("");
    })
    .fail(function(data) {
      //$("#status").removeClass('alert-success');
      //$("#status").addClass('alert-danger');
      //$("#status").removeClass('hidden');
      //$('#status').text("Error");  
    })
    .always(function() {
      //alert( "finished" );
  });
}