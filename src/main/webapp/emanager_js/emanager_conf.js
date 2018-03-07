function updateEquipmentData() {
 equipmentTable.clear();
  $.post("rest/getEquipment",

    function(data){
      dataSet = parseEquipmentJSONToDataSet(data);
    })
    .done(function() {
      equipmentTable.rows.add(dataSet).draw(false);
    })
    .fail(function(jqXHR, textStatus) {

    })
    .always(function() {
    });
}

function updateTypesData() {
  equipmentTypeTable.clear();
  $.post("rest/getEquipmentTypes",

    function(data){
      dataSet = parseTypesJSONToDataSet(data);
    })
    .done(function() {
      equipmentTypeTable.rows.add(dataSet).draw(false);
    })
    .fail(function(jqXHR, textStatus) {

    })
    .always(function() {
    });
}

function parseEquipmentJSONToDataSet(data) {
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
                  'data-toggle="modal" data-target="#modal_edit"><span class="glyphicon glyphicon-wrench" aria-hidden="true"></span></button>&nbsp;' +
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




function parseTypesJSONToDataSet(data) {
  var arrayLength = data.length;
  var dataSet = [];
  // Parse JSON-data to array for Data Tables
  for (var i=0; i< arrayLength; i++) {
    var equipmentTypeId = data[i]["equipmentTypeId"];
    var typeCode = data[i]["typeCode"];
    var typeName = data[i]["typeName"];

    var equipmentType = [equipmentTypeId, 
                         typeCode, 
                         typeName];
    dataSet.push(equipmentType);
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
    updateEquipmentData();
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
    updateEquipmentData();
  });
}

function openEditModal(equipmentId, name, serial, equipmentTypeId, typeName) {
  editTitle(name);
  $("#hid_equipmentId_edit").val(equipmentId);
  $("#txt_name_edit").val(name);
  $("#txt_serial_edit").val(serial);
  $("#txt_type_edit").val(typeName);
  $("#hid_equipmentTypeId_edit").val(equipmentTypeId);
  getTypes();
}

function openAddModal() {
  $("#txt_name_add").val("");
  $("#txt_serial_add").val("");
  $("#txt_type_add").val("");
  $("#hid_equipmentTypeId_add").val("");
  getTypes();
}

function addEquipment(name, serial, equipmentTypeId) {
  if (!$("#txt_name_add").val()) {
    if($("#status_add").hasClass("hidden")) {$("#status").removeClass("hidden");}
    if($("#status_add").hasClass("alert-info")) {$("#status").removeClass("alert-info");}
    if($("#status_add").hasClass("alert-success")) {$("#status").removeClass("alert-danger");}
    $("#status_add").addClass("alert-danger");
    $("#msg_type_add").text("Error: ");
    $("#msg_text_add").text("Name field empty");  
  }
  else {
  $.post("rest/insertEquipment",
    {
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
      $("#msg_text").text(data["serial"] + " / " +  data["name"] + " succesfully added.");            
      
    })
    .done(function() {

    })
    .fail(function(jqXHR, textStatus) {
      if($("#status").hasClass("hidden")) {$("#status").removeClass("hidden");}
      if($("#status").hasClass("alert-info")) {$("#status").removeClass("alert-info");}
      if($("#status").hasClass("alert-success")) {$("#status").removeClass("alert-danger");}
      $("#status").addClass("alert-danger");
      $("#msg_type").text("Error: ");
      $("#msg_text").text("Adding equipment failed: " + jqXHR["responseJSON"]["message"]);  

    })
    .always(function() {
      updateEquipmentData();
    
  });
    }
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
      updateEquipmentData();
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
      updateEquipmentData();
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
      $(".drp_types").append('<li role="presentation" value="' + data[i]["equipmentTypeId"] + '"><a class="typeItem" role="menuitem" tabindex="-1" href="#">' + data[i]["typeName"] + '</a></li>');
    }
  })
  .done(function() {
    updateEquipmentData();
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