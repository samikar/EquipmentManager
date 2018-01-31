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