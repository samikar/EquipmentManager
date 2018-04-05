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

function updateTypeData() {
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


    var actions = '<button type="button" class="btn btn-sm btn-success" onclick="openEditEquipmentModal(' + 
                  equipmentId + ', \'' + 
                  name + '\', \'' +
                  serial + '\', ' +
                  equipmentTypeId + ', \'' + 
                  equipmentTypeName + '\')"' +
                  'data-toggle="modal" data-target="#modal_editEquipment"><span class="glyphicon glyphicon-wrench" aria-hidden="true"></span></button>&nbsp;' +
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

    var actions = '<button type="button" class="btn btn-sm btn-success" onclick="openEditTypeModal(' + 
              equipmentTypeId + ', \'' + 
              typeName + '\', \'' +
              typeCode + '\')"' +
              'data-toggle="modal" data-target="#modal_editType"><span class="glyphicon glyphicon-wrench" aria-hidden="true"></span></button>&nbsp;' +
              '<button type="button" class="btn btn-sm btn-danger" onclick="deleteType(' + equipmentTypeId + ', \'' + typeName  + 
              '\')"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></button>';

    var equipmentType = [equipmentTypeId, 
                         typeCode, 
                         typeName,
                         actions];
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

function openEditEquipmentModal(equipmentId, name, serial, equipmentTypeId, typeName) {
  editTitle(name);
  $("#hid_equipmentId_edit").val(equipmentId);
  $("#txt_equipmentName_edit").val(name);
  $("#txt_serial_edit").val(serial);
  $("#txt_type_edit").val(typeName);
  $("#hid_equipmentTypeId_edit").val(equipmentTypeId);
  getTypes();
}

function openEditTypeModal(equipmentTypeId, name, typeCode) {
  editTitle(name);
  $("#hid_equipmentTypeId_edit").val(equipmentTypeId);
  $("#txt_typeName_edit").val(name);
  $("#txt_typeCode_edit").val(typeCode);
}

function openAddEquipmentModal() {
  $("#txt_equipmentName_add").val("");
  $("#txt_serial_add").val("");
  $("#txt_type_add").val("");
  $("#hid_equipmentTypeId_add").val("");
  getTypes();
}

function openAddTypeModal() {
  $("#txt_typeName_add").val("");
  $("#txt_typeCode_add").val("");
}

function addEquipment(name, serial, equipmentTypeId) {
  if (!$("#txt_equipmentName_add").val()) {
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
      $("#msg_text").text("Adding new equipment failed: " + jqXHR["responseJSON"]["message"]);  

    })
    .always(function() {
      updateEquipmentData();
    
  });
    }
}

function addType(typeName, typeCode) {
  if (!$("#txt_typeName_add").val()) {
    if($("#status_add").hasClass("hidden")) {$("#status").removeClass("hidden");}
    if($("#status_add").hasClass("alert-info")) {$("#status").removeClass("alert-info");}
    if($("#status_add").hasClass("alert-success")) {$("#status").removeClass("alert-danger");}
    $("#status_add").addClass("alert-danger");
    $("#msg_type_add").text("Error: ");
    $("#msg_text_add").text("Name field empty");  
  }
  else {
  $.post("rest/insertType",
    {
      typeName: typeName,
      typeCode: typeCode
    },
    function(data){
      if($("#status").hasClass("hidden")) {$("#status").removeClass("hidden");}
      if($("#status").hasClass("alert-info")) {$("#status").removeClass("alert-info");}
      if($("#status").hasClass("alert-danger")) {$("#status").removeClass("alert-danger");}
      $("#status").addClass("alert-success");
      $("#msg_type").text("Success: ");
      $("#msg_text").text(data["typeName"] + " succesfully added.");            
      
    })
    .done(function() {

    })
    .fail(function(jqXHR, textStatus) {
      if($("#status").hasClass("hidden")) {$("#status").removeClass("hidden");}
      if($("#status").hasClass("alert-info")) {$("#status").removeClass("alert-info");}
      if($("#status").hasClass("alert-success")) {$("#status").removeClass("alert-danger");}
      $("#status").addClass("alert-danger");
      $("#msg_type").text("Error: ");
      $("#msg_text").text("Adding new equipment type failed: " + jqXHR["responseJSON"]["message"]);  

    })
    .always(function() {
      updateTypeData();
    
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

function updateType(equipmentTypeId, typeName, typeCode) {
  $.post("rest/updateType",
    {
      equipmentTypeId: equipmentTypeId,
      typeName: typeName,
      typeCode: typeCode
    },
    function(data){
      if($("#status").hasClass("hidden")) {$("#status").removeClass("hidden");}
      if($("#status").hasClass("alert-info")) {$("#status").removeClass("alert-info");}
      if($("#status").hasClass("alert-danger")) {$("#status").removeClass("alert-danger");}
      $("#status").addClass("alert-success");
      $("#msg_type").text("Success: ");
      $("#msg_text").text(data["typeName"] + " succesfully updated.");            
      
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
      updateTypeData();
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

function deleteType(equipmentTypeId, name) {
  var result = confirm("Do you really want to delete " + name + "?");
  if (result) {
    $.post("rest/deleteType",
    {
      equipmentTypeId: equipmentTypeId,
    },
    function(data){
      if($("#status").hasClass("hidden")) {$("#status").removeClass("hidden");}
      if($("#status").hasClass("alert-info")) {$("#status").removeClass("alert-info");}
      if($("#status").hasClass("alert-danger")) {$("#status").removeClass("alert-danger");}
      $("#status").addClass("alert-success");
      $("#msg_type").text("Success: ");
      $("#msg_text").text(data["typeName"] + " succesfully deleted.");   
      
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
      updateTypeData();
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

function addEquipmentBtnAction() {
  if (!$("#txt_equipmentName_add").val() || !$("#txt_serial_add").val() || !$("#hid_equipmentTypeId_add").val()) {
    if($("#status_add").hasClass("hidden")) {$("#status").removeClass("hidden");}
    if($("#status_add").hasClass("alert-info")) {$("#status").removeClass("alert-info");}
    if($("#status_add").hasClass("alert-success")) {$("#status").removeClass("alert-danger");}
    $("#status_add").addClass("alert-danger");
    $("#msg_type_add").text("Error: ");
    $("#msg_text_add").text("Empty fields: ");
    }
    if(!$("#txt_equipmentName_add").val()) {
     $("#msg_text_add").append("Name "); 
    }
    if(!$("#txt_serial_add").val()) {
     $("#msg_text_add").append("Serial "); 
    }
    if(!$("#hid_equipmentTypeId_add").val()) {
     $("#msg_text_add").append("Type"); 
    }
  else {
    var name = $("#txt_equipmentName_add").val();
    var serial = $("#txt_serial_add").val();
    var equipmentTypeId = $("#hid_equipmentTypeId_add").val();
    addEquipment(name, serial, equipmentTypeId);
    $('.modal.in').modal('hide');
  }
}

function addTypeBtnAction() {
  if (!$("#txt_typeName_add").val() || !$("#txt_typeCode_add").val()) {
    if($("#status_add").hasClass("hidden")) {$("#status").removeClass("hidden");}
    if($("#status_add").hasClass("alert-info")) {$("#status").removeClass("alert-info");}
    if($("#status_add").hasClass("alert-success")) {$("#status").removeClass("alert-danger");}
    $("#status_add").addClass("alert-danger");
    $("#msg_type_add").text("Error: ");
    $("#msg_text_add").text("Empty fields: ");
  }
  if(!$("#txt_typeName_add").val()) {
   $("#msg_text_add").append("Type name "); 
  }
  if(!$("#txt_typeCode_add").val()) {
   $("#msg_text_add").append("Typecode "); 
  }
  
  else {
  var typeName = $("#txt_typeName_add").val();
  var typeCode = $("#txt_typeCode_add").val();
  addType(typeName, typeCode);
  closeModal();
  }
}

function saveEquipmentBtnAction() {
  var equipmentId = $("#hid_equipmentId_edit").val();
  var name = $("#txt_equipmentName_edit").val();
  var serial = $("#txt_serial_edit").val();
  var equipmentTypeId = $("#hid_equipmentTypeId_edit").val();
  updateEquipment(equipmentId, name, serial, equipmentTypeId);
  closeModal();
}


function saveTypeBtnAction() {
  var equipmentTypeId = $("#hid_equipmentTypeId_edit").val();
  var typeName = $("#txt_typeName_edit").val();
  var typeCode = $("#txt_typeCode_edit").val();
  updateType(equipmentTypeId, typeName, typeCode);
  closeModal();
}
// Set name of selected type to modal title
function editTitle(name) {
  $("#modal_title").text(name);
}

function closeModal() {
  $('.modal.in').modal('hide');
}