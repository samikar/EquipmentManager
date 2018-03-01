function drawSelectedChart() {
  chartType = parseInt($("input[name=rad_chartType]:checked").val());; 
  dataType = parseInt($("input[name=rad_dataType]:checked").val());;
  getDateRange();
  switch (chartType) {
    case 0:
      drawEquipmentChart(typeCode, start, end, dataType);
      break;
    case 1:
      drawMonthlyChart(typeCode, start, end, dataType);
      break;
  }
}

function drawEquipmentChart(typeCode, start, end, dataType) {
  clearCanvas();
  initChart();
  removeData();
  getEquipmentData(typeCode, start, end, dataType);
  redrawChart();
}

function drawMonthlyChart(typeCode, start, end, dataType) {
  clearCanvas();
  initChart();
  removeData();
  getMonthlyData(typeCode, start, end, dataType);
  redrawChart();
}

function findTypes() {
  $.post("rest/getEquipmentTypesWithEquipment",
    function(data){
      $.each(data, function(i, item) {
        $("#sel_types").append('<option value="' + item["typeCode"] + '">' + item["typeName"] + '</option>');
      });
      
    })
    .done(function() {
      //alert( "second success" );
    })
    .fail(function(jqXHR, textStatus) {
     
    })
    .always(function() {
      //alert( "finished" );
    });
}

function getEquipmentData(typeCode, start, end, dataType) {
  $.post("rest/usageByType",
    {
          typeCode : typeCode,
          start : start,
          end : end
    },
  function(data){
    $.each(data, function(i, item) {
      
      switch (dataType) {
        // Show usage in percentage
        case 0:
          insertDataInPercentage(item, i);
          insertDeviceNameLabels(item, i);
          usageBarChart.options.scales.yAxes[0].scaleLabel.labelString = "Usage (%)";
          break;
        // Show usage in work hours
        case 1:  
          insertDataInHours(item, i);
          insertDeviceNameLabels(item, i);
          usageBarChart.options.scales.yAxes[0].scaleLabel.labelString = "Usage (h)";
          break;
      }
    });
  })
  .done(function() {
    redrawChart();
    //alert( "second success" );
  })
  .fail(function(jqXHR, textStatus) {
    //$('#txt_equipmentId').val("");
    //$("#status").removeClass('alert-success');
  })
  .always(function() {
    //alert( "finished" );
  });
}

function getMonthlyData(typeCode, start, end, dataType) {
  $.post("rest/monthlyUsageByType",
    {
          typeCode : typeCode,
          start : start,
          end : end
    },
  function(data){
    $.each(data, function(i, item) {
       switch (dataType) {
        // Show usage in percentage
        case 0:
          insertDataInPercentage(item, i);
          insertMonthLabels(item, i);
          usageBarChart.options.scales.yAxes[0].scaleLabel.labelString = "Usage (%)";
          break;
        // Show usage in work hours
        case 1:  
          insertDataInHours(item, i);
          insertMonthLabels(item, i);
          usageBarChart.options.scales.yAxes[0].scaleLabel.labelString = "Usage (h)";
          break;
      }
    });
  })
  .done(function() {
    redrawChart();
    //alert( "second success" );
  })
  .fail(function(jqXHR, textStatus) {
    //$('#txt_equipmentId').val("");
    //$("#status").removeClass('alert-success');
  })
  .always(function() {
    //alert( "finished" );
  });
}

function insertDeviceNameLabels(jsonItem, i) {
  usageBarChartData.labels.push(jsonItem.name + "(" + jsonItem.serial + ")");
}

function insertMonthLabels(jsonItem, i) {
  usageBarChartData.labels.push(jsonItem.month);
}

function insertDataInPercentage(jsonItem, i) {       
  var totalHours = jsonItem.maintenance + jsonItem.calibration + jsonItem.inUse + jsonItem.available;
  

  // Usage in %, rounded to two decimals
  usageBarChartData.datasets[0].data.push(((jsonItem.maintenance / totalHours) * 100).toFixed(2));
  usageBarChartData.datasets[1].data.push(((jsonItem.calibration / totalHours) * 100).toFixed(2));
  usageBarChartData.datasets[2].data.push(((jsonItem.inUse / totalHours) * 100).toFixed(2));
  usageBarChartData.datasets[3].data.push(((jsonItem.available / totalHours) * 100).toFixed(2));
}

function insertDataInHours(jsonItem, i) {       
  var totalHours = jsonItem.maintenance + jsonItem.calibration + jsonItem.inUse + jsonItem.available;     
  // Usage in workhours
  usageBarChartData.datasets[0].data.push(jsonItem.maintenance.toFixed(2));
  usageBarChartData.datasets[1].data.push(jsonItem.calibration.toFixed(2));
  usageBarChartData.datasets[2].data.push(jsonItem.inUse.toFixed(2));
  usageBarChartData.datasets[3].data.push(jsonItem.available.toFixed(2));
}

function initChart() {
  var ctx = document.getElementById("usageChart").getContext("2d");
  usageBarChart = new Chart(ctx, {
      type: 'bar',
      data: usageBarChartData,
      options: {
          title:{
              display:false,
              text:"EquipmentType usage"
          },
          tooltips: {
              mode: 'index',
              intersect: false
          },
          responsive: true,
          scales: {
              xAxes: [{
                  stacked: true,
                  ticks: {
                    autoSkip: false
                  }
              }],
              yAxes: [{
                  stacked: true,
                  scaleLabel: {
                    display: true,
                    labelString: "Usage (%)"
                },
                ticks : {
                        suggestedMax  : 100,    
                        
                    }
              }]
          },
      }
    });
}

// Redraws chart to canvas
function redrawChart() {
  usageBarChart.data = usageBarChartData;
  usageBarChart.update();
}

// Clears canvas for drawing new chart
function clearCanvas() {
  var chartContent = document.getElementById("chart_container");
  chartContent.innerHTML = "&nbsp;";
  $("#chart_container").append('<canvas id="usageChart" width="800" height="600"></canvas>');
}

function initData() {
  usageBarChartData = {
        labels: [],
        datasets: [{
            label: 'Maintenance',
            //backgroundColor: window.chartColors.red,
            backgroundColor: '#ff3030',
            data: []
        }, {
            label: 'Calibration',
            //backgroundColor: window.chartColors.orange,
            backgroundColor: '#ffed6d',
            data: []
        }, {
            label: 'In use',
            //backgroundColor: window.chartColors.blue,
            backgroundColor: '#1c73ff',
            data: []
        },  {
            label: 'Available',
            //backgroundColor: window.chartColors.green,
            backgroundColor: '#54ff68',
            data: []
        }]
  };
}

// Removes data and labels from dataset
function removeData() {
  usageBarChartData.datasets[0]["data"] = [];
  usageBarChartData.datasets[1]["data"] = [];
  usageBarChartData.datasets[2]["data"] = [];
  usageBarChartData.datasets[3]["data"] = [];
  usageBarChartData.labels = [];
}

// Set name of selected type to modal title
function selectType() {
  $("#modal_title").text($("#sel_types option:selected").text());
  typeCode = $("#sel_types option:selected" ).val();
}

// Reads date range from relevant field
function getDateRange() {
  // Parse dates from daterange-field and convert to epoch
  start = (Date.parse($("#daterange").val().substring(0,10))).valueOf() / 1000;
  end = (Date.parse($("#daterange").val().substring(13,23))).valueOf() / 1000;

  var startDate = new Date(0);
  var endDate = new Date(0);
  startDate.setUTCSeconds(start);
  endDate.setUTCSeconds(end);
}