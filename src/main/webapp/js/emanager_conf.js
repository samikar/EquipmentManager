function updateData() {
 equipmentTable.clear();
  $.post("rest/getEquipment",

    function(data){
      dataSet = parseJSONDataToDataSet(data);
    })
    .done(function() {
      equipmentTable.rows.add(dataSet).draw(false);
    })
    .fail(function(jqXHR, textStatus) {

    })
    .always(function() {
    });
}

function parseJSONDataToDataSet(data) {
  console.dir(data);
  var arrayLength = data.length;
  var dataSet = [];
  // Parse JSON-data to array for Data Tables
  for (var i=0; i< arrayLength; i++) {
    var equipmentId = data[i]["equipmentId"];
    var name = data[i]["name"];
    var serial = data[i]["serial"];
    var equipmentType = data[i]["equipmenttype"];
    var equipmentTypeId = 0;
    var equipmentTypeName = "";
    var status = data[i]["status"];

    if (status == 1) {
      status = '<button type="button" class="btn btn-sm btn-success" onclick="disableEquipment(' + equipmentId + ')">Enabled</button>';
    }
    else {
      status = '<button type="button" class="btn btn-sm btn-danger" onclick="enableEquipment(' + equipmentId + ')">Disabled</button>';
    }

    if (equipmentType !== null) {
      equipmentTypeId = data[i]["equipmenttype"]["equipmentTypeId"];
      equipmentTypeName = data[i]["equipmenttype"]["typeName"];

    }

    var actions = '<button type="button" class="btn btn-sm btn-success" onclick="openEditModal(' + 
                  equipmentId + ', \'' + 
                  name + '\', \'' +
                  serial + '\', ' +
                  equipmentTypeId + ', \'' + 
                  equipmentTypeName + '\')"' +
                  'data-toggle="modal" data-target="#edit"><span class="glyphicon glyphicon-wrench" aria-hidden="true"></span></button>&nbsp;' +
                  '<button type="button" class="btn btn-sm btn-danger" onclick="deleteEquipment(' + equipmentId + ', \'' + name  + 
                  '\')"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></button>';

    var equipment = [equipmentId, 
                       name, 
                       serial, 
                       equipmentTypeName,
                       status,
                       actions];
  dataSet.push(equipment);
  }
  return dataSet;
}

function disableEquipment(equipmentId) {
  $.post("rest/disableEquipment",
  {
    equipmentId: equipmentId,
  },
  function(data){
    if($("#status").hasClass("hidden")) {$("#status").removeClass("hidden");}
    if($("#status").hasClass("alert-info")) {$("#status").removeClass("alert-info");}
    if($("#status").hasClass("alert-danger")) {$("#status").removeClass("alert-danger");}
    $("#status").addClass("alert-success");
    $("#msg_type").text("Success: ");
    $("#msg_text").text(data["serial"] + " / " +  data["name"] + " disabled.");
  })
  .done(function() {
    
  })
  .fail(function(data) {
    if($("#status").hasClass("hidden")) {$("#status").removeClass("hidden");}
    if($("#status").hasClass("alert-info")) {$("#status").removeClass("alert-info");}
    if($("#status").hasClass("alert-success")) {$("#status").removeClass("alert-danger");}
    $("#status").addClass("alert-danger");
    $("#msg_type").text("Error: ");
    $("#msg_text").text("Disabling equipment failed: " + jqXHR["responseJSON"]["message"]);
  })
  .always(function() {
    updateData();
  });
}

function enableEquipment(equipmentId) {
  $.post("rest/enableEquipment",
  {
    equipmentId: equipmentId,
  },
  function(data){
    if($("#status").hasClass("hidden")) {$("#status").removeClass("hidden");}
      if($("#status").hasClass("alert-info")) {$("#status").removeClass("alert-info");}
      if($("#status").hasClass("alert-danger")) {$("#status").removeClass("alert-danger");}
      $("#status").addClass("alert-success");
      $("#msg_type").text("Success: ");
      $("#msg_text").text(data["serial"] + " / " +  data["name"] + " enabled.");
  })
  .done(function() {
    
  })
  .fail(function(jqXHR, textStatus) {
    if($("#status").hasClass("hidden")) {$("#status").removeClass("hidden");}
    if($("#status").hasClass("alert-info")) {$("#status").removeClass("alert-info");}
    if($("#status").hasClass("alert-success")) {$("#status").removeClass("alert-danger");}
    $("#status").addClass("alert-danger");
    $("#msg_type").text("Error: ");
    $("#msg_text").text("Enabling equipment failed: " + jqXHR["responseJSON"]["message"]);
  })
  .always(function() {
    updateData();
  });
}

function openEditModal(equipmentId, name, serial, equipmentTypeId, typeName) {
  editTitle(name);
  $("#hid_equipmentId").val(equipmentId);
  $("#txt_name").val(name);
  $("#txt_serial").val(serial);
  $("#txt_type").val(typeName);
  $("#hid_equipmentTypeId").val(equipmentTypeId);
  getTypes();
}

function updateEquipment(equipmentId, name, serial, equipmentTypeId) {
  $.post("rest/updateEquipment",
    {
      equipmentId: equipmentId,
      name: name,
      serial: serial,
      equipmentTypeId: equipmentTypeId
    },
    function(data){
      if($("#status").hasClass("hidden")) {$("#status").removeClass("hidden");}
      if($("#status").hasClass("alert-info")) {$("#status").removeClass("alert-info");}
      if($("#status").hasClass("alert-danger")) {$("#status").removeClass("alert-danger");}
      $("#status").addClass("alert-success");
      $("#msg_type").text("Success: ");
      $("#msg_text").text(data["serial"] + " / " +  data["name"] + " succesfully updated.");            
      
    })
    .done(function() {

    })
    .fail(function(jqXHR, textStatus) {
      if($("#status").hasClass("hidden")) {$("#status").removeClass("hidden");}
      if($("#status").hasClass("alert-info")) {$("#status").removeClass("alert-info");}
      if($("#status").hasClass("alert-success")) {$("#status").removeClass("alert-danger");}
      $("#status").addClass("alert-danger");
      $("#msg_type").text("Error: ");
      $("#msg_text").text("Updating failed: " + jqXHR["responseJSON"]["message"]);  

    })
    .always(function() {
      updateData();
  });
}

function deleteEquipment(equipmentId, name) {
  var result = confirm("Do you really want to delete " + name + "?");
  if (result) {
    $.post("rest/deleteEquipment",
    {
      equipmentId: equipmentId,
    },
    function(data){
      if($("#status").hasClass("hidden")) {$("#status").removeClass("hidden");}
      if($("#status").hasClass("alert-info")) {$("#status").removeClass("alert-info");}
      if($("#status").hasClass("alert-danger")) {$("#status").removeClass("alert-danger");}
      $("#status").addClass("alert-success");
      $("#msg_type").text("Success: ");
      $("#msg_text").text(data["serial"] + " / " +  data["name"] + " succesfully deleted.");   
      
    })
    .done(function() {
      
    })
    .fail(function(jqXHR, textStatus) {
      if($("#status").hasClass("hidden")) {$("#status").removeClass("hidden");}
      if($("#status").hasClass("alert-info")) {$("#status").removeClass("alert-info");}
      if($("#status").hasClass("alert-success")) {$("#status").removeClass("alert-danger");}
      $("#status").addClass("alert-danger");
      $("#msg_type").text("Error: ");
      $("#msg_text").text("Deleting failed: " + jqXHR["responseJSON"]["message"]);   
    })
    .always(function() {
      updateData();
    });
  }
}

function getTypes() {
  $.post("rest/getEquipmentTypes",
  {
    
  },
  function(data){
    var arrayLength = data.length;          
    for (var i=0; i< arrayLength; i++) {
      $("#drp_types").append('<li role="presentation" value="' + data[i]["equipmentTypeId"] + '"><a class="typeItem" role="menuitem" tabindex="-1" href="#">' + data[i]["typeName"] + '</a></li>');
    }
  })
  .done(function() {
    updateData();
  })
  .fail(function(data) {

  })
  .always(function() {
  
  });
}

// Set name of selected type to modal title
function editTitle(name) {
  $("#modal_title").text(name);
}