function getEquipmentData() {
  $.post("rest/getEquipmentStatus",
    function(data){
      // success
      var arrayLength = data.length;
      var dataSet = [];
      // Parse JSON-data to array for Data Tables
      for (var i=0; i< arrayLength; i++) {
        var equipment = [data[i]["serial"], 
                         data[i]["name"], 
                         data[i]["employeeName"], 
                         data[i]["equipmenttype"]["typeName"], 
                         data[i]["availability"]];
        dataSet.push(equipment);
      }
      
      $('#tbl_equipment').DataTable( {
              data: dataSet,
      columns: [
        { title: "Serial number" },
        { title: "Name" },
        { title: "User" },
        { title: "Type" },
        { title: "Availability" }
      ],
      "order": [[ 3, "asc" ]]
      });

    })
    .done(function() {
    //alert( "second success" );

    })
    .fail(function(jqXHR, textStatus) {
      $('#txt_equipmentId').val("");
      $("#status").addClass('alert-danger');
      $("#status").removeClass('hidden');
      $('#status').text("Error: " + jqXHR["responseJSON"]["message"]);       
    })
    .always(function() {
      //alert( "finished" );
    });
}