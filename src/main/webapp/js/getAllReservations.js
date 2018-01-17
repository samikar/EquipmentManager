
$(document).ready(function() {
    $.ajax({
        url: "http://localhost:8080/test"
    }).then(function(data) {
       $('id').append(data.id);       
    });
});

/*
$(document).ready(function() {
    $.ajax({
        url: "http://localhost:8080/getallreservations"
    }).then(function(data) {
       $('.reservationId').append(data[0].reservationId);
       $('.employeeId_return').append(data[0].dateReturn);
    });
});
*/