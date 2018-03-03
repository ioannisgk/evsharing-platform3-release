	<div class="row">
                <div class="col-lg-16">
                    <div class="panel">
                      <div class="panel-body">
                          <p><span class="pull-right ">Copyright © Ioannis Gkourtzounis, 2018</span></p>
                      </div>
                      <!-- /.panel-body -->
                    </div>
                </div>
                <!-- /.col-lg-16 -->
            </div>
            <!-- /.row -->
        </div>
        <!-- /#page-wrapper -->

    </div>
    <!-- /#wrapper -->
    
    <!-- jQuery -->
    <script src="${pageContext.request.contextPath}/resources/vendor/jquery/jquery.min.js"></script>
    
    <!-- Maps Custom JavaScript -->
    <script src="${pageContext.request.contextPath}/resources/dist/js/map.js"></script>
    
    <script async defer
      src="https://maps.googleapis.com/maps/api/js?key=AIzaSyB3LX8fk3dtFnB47Rbf118RyVRjCjDrvvE&callback=initMap">
    </script>
    
    <script>
    	
    $(document).ready(function() {
    	
		// Ajax call when starting service
		
		$('#start-service').click(function() {
			var mode = $('#mode').val();
			$.ajax({
				type:'GET',
				url:'${pageContext.request.contextPath}/home/start-service/' + mode,
				success: function(result) {
					console.log('Service started');
					$('#mode').focus();
					$('#info').html(
						'<div class=\"alert alert-success col-md-5\"><strong>SERVICE IS RUNNING</strong></div>');
				}
			});
		});
		
		// Ajax call when stopping service
		
		$('#stop-service').click(function() {
			
			$.ajax({
				type:'GET',
				url:'${pageContext.request.contextPath}/home/stop-service',
				success: function(result) {
					console.log('Service stopped');
					$('#mode').focus();
					$('#info').html(
						'<div class=\"alert alert-danger col-md-5\"><strong>SERVICE IS STOPPED</strong></div>');
				}
			});
		});
		
		// Ajax call when starting simulation
		
		$('#start-simulation').click(function() {
			var mode = $('#mode').val();
			$.ajax({
				type:'GET',
				url:'${pageContext.request.contextPath}/simulation/start-simulation/' + mode,
				success: function(result) {
					console.log('Simulation started');
					$('#mode').focus();
					$('#info').html(
						'<div class=\"alert alert-success col-md-5\"><strong>SIMULATION IS RUNNING</strong></div>');
				}
			});
		});
		
		// Ajax call when stopping simulation
		
		$('#stop-simulation').click(function() {
			
			$.ajax({
				type:'GET',
				url:'${pageContext.request.contextPath}/simulation/stop-simulation',
				success: function(result) {
					console.log('Simulation stopped');
					$('#mode').focus();
					$('#info').html(
						'<div class=\"alert alert-danger col-md-5\"><strong>SIMULATION IS STOPPED</strong></div>');
				}
			});
		});
		
		// Ajax call when selecting a user
		
		$('#select-user').click(function() {
			event.preventDefault();
			var user = $('#user').val();
			$.ajax({
				type:'GET',
				url:'${pageContext.request.contextPath}/simulation/select-user/' + user,
				success: function(result) {
					console.log('User selected');
					$('#user').focus();
					$('#show-user-id').html(result);
				}
			});
		});
		
		// Ajax call when selecting a station
		
		$('#select-station').click(function() {
			event.preventDefault();
			var station = $('#station').val();
			$.ajax({
				type:'GET',
				url:'${pageContext.request.contextPath}/simulation/select-station/' + station,
				success: function(result) {
					console.log('Station selected');
					$('#station').focus();
					$('#show-station-id').html(result);
				}
			});
		});
		
		// Setup morris area chart

		var morrisAreaChart = Morris.Area({
	        element: 'morris-area-chart',
	        xkey: 'period',
	        ykeys: ['accepted', 'denied'],
	        labels: ['Accepted', 'Denied'],
	        pointSize: 2,
	        hideHover: 'auto',
	        resize: true
	    });
		
		// Setup morris donut chart
		
		var morrisDonutChart = Morris.Donut({
	        element: 'morris-donut-chart',
	        data: [{
	            label: '',
	            value: ''
	        }],
	        resize: true
	    });
		
		// Setup morris bar chart 1
		
		var morrisBarChart1 = Morris.Bar({
	        element: 'morris-bar-chart1',
	        xkey: 'y',
	        ykeys: ['a'],
	        labels: ['Number of Vehicles'],
	        hideHover: 'auto',
	        resize: true,
	        barColors: ['#8190a1']
	    });
		
		// Setup morris bar chart 2
		
		var morrisBarChart2 = Morris.Bar({
	        element: 'morris-bar-chart2',
	        xkey: 'y',
	        ykeys: ['a'],
	        labels: ['Charge Level %'],
	        hideHover: 'auto',
	        resize: true,
	        barColors: ['#3b5ea1']
	    });
		
		// Function to update requests chart via ajax
		
		function updateRequestsChart() {
			var data = [];
			$.ajax({
				type:'GET',
				url:'${pageContext.request.contextPath}/charts/update-requests',
				async: false,
				success: function(result) {
					var userRequests = eval('[' + result + ']');
					for (var i = 0; i < 16; i++) {
						data.push({
							period: userRequests[0][i*12][0],
				            accepted: userRequests[0][i*12][2],
				            denied: userRequests[0][i*12][3]
					    });
					}
					morrisAreaChart.setData(data);
				}
			})
		}
		
		// Function to update efficiency rate via ajax
		
		function updateEfficiencyRate() {
			var data = [];
			$.ajax({
				type:'GET',
				url:'${pageContext.request.contextPath}/charts/update-efficiency',
				async: false,
				success: function(result) {
					var efficiencyRate = eval('[' + result + ']');
					for (var i = 0; i < 2; i++) {
						data.push({
							label: efficiencyRate[0][i][0],
				            value: efficiencyRate[0][i][1]
					    });
					}
					morrisDonutChart.setData(data);
				}
			})
		}
		
		// Function to update stations chart via ajax
		
		function updateStationsChart(station, stationName) {
			var data = [];
			$.ajax({
				type:'GET',
				url:'${pageContext.request.contextPath}/charts/update-stations/' + station,
				async: false,
				success: function(result) {
					var stationsStats = eval('[' + result + ']');
					for (var i = 0; i < 16; i++) {
						data.push({
							y: stationsStats[0][i*12][0],
				            a: stationsStats[0][i*12][1]
					    });
					}
					morrisBarChart1.setData(data);
					$('#selected-station').html(stationName);
				}
			})
		}
		
		// Function to update charge chart via ajax
		
		function updateChargeChart(vehicle, vehicleName) {
			var data = [];
			$.ajax({
				type:'GET',
				url:'${pageContext.request.contextPath}/charts/update-charge/' + vehicle,
				async: false,
				success: function(result) {
					var chargeStats = eval('[' + result + ']');
					for (var i = 0; i < 16; i++) {
						data.push({
							y: chargeStats[0][i*12][0],
				            a: chargeStats[0][i*12][1]
					    });
					}
					morrisBarChart2.setData(data);
					$('#selected-vehicle').html(vehicleName);
				}
			})
		}
		
		// Function to update vehicles table via ajax

		function updateVehiclesTable(index, time) {
			var data = [];
			$.ajax({
				type:'GET',
				url:'${pageContext.request.contextPath}/charts/update-vehicles/' + index,
				async: false,
				success: function(result) {
					var vehiclesTable = eval('[' + result + ']');
					
					// Empty table data
					
					$('#vehicles-table').html(
								'<thead><tr><th>#</th><th>Vehicle</th>' +
                                '<th>Station</th></tr></thead>' +
                        		'<tbody></tbody>');
					
					for (var i = 0; i < vehiclesTable[0].length; i++) {
						
						// Update table data
						
						$('#vehicles-table').append(
				        		'<tr><td>' + (i+1) + '</td><td>' +
				        		vehiclesTable[0][i][0] +
				        		'</td><td>' +
				        		vehiclesTable[0][i][2] +
				        		'</td></tr>');

					}
					$('#selected-time').html(time);
				}
			})
		}
		
		// Set ids and names for default station, default vehicle and default time
		
		var station = $('ul#select-station-chart li:first').attr('id');
		var stationName = $('ul#select-station-chart li:first').text();
		var vehicle = $('ul#select-vehicle-chart li:first').attr('id');
		var vehicleName = $('ul#select-vehicle-chart li:first').text();
		var index = $('ul#select-time-table li:first').attr('id');
		var time = $('ul#select-time-table li:first').text();
		
		// Show default station chart, charge chart and vehicles table on page load
		
		updateStationsChart(station, stationName);
		updateChargeChart(vehicle, vehicleName);
		updateVehiclesTable(index, time);
		
		// Update station chart, charge chart and vehicles table on user selection
		
		$('#select-station-chart li').click(function() {
			event.preventDefault();
			var station = this.id;
			var stationName = $(this).text();
			updateStationsChart(station, stationName);
		});
		
		$('#select-vehicle-chart li').click(function() {
			event.preventDefault();
			var vehicle = this.id;
			var vehicleName = $(this).text();
			updateChargeChart(vehicle, vehicleName);
		});
		
		
		$('#select-time-table li').click(function() {
			event.preventDefault();
			var index = this.id;
			var time = $(this).text();
			updateVehiclesTable(index, time);
		});
		
		// Get current path and set intervals if user stays on 'chars/list' page

		var href = document.location.href;
		var currentPath = href.substr(href.length - 11);
		
		if (currentPath == 'charts/list') {
			setInterval(updateRequestsChart, 1000);
			setInterval(updateEfficiencyRate, 1000);
		}

	});
    	
    </script>

    <!-- Bootstrap Core JavaScript -->
    <script src="${pageContext.request.contextPath}/resources/vendor/bootstrap/js/bootstrap.min.js"></script>

    <!-- Metis Menu Plugin JavaScript -->
    <script src="${pageContext.request.contextPath}/resources/vendor/metisMenu/metisMenu.min.js"></script>

    <!-- Morris Charts JavaScript -->
    <script src="${pageContext.request.contextPath}/resources/vendor/raphael/raphael.min.js"></script>
    <script src="${pageContext.request.contextPath}/resources/vendor/morrisjs/morris.min.js"></script>

    <!-- Custom Theme JavaScript -->
    <script src="${pageContext.request.contextPath}/resources/dist/js/sb-admin-2.js"></script>
    
</body>

</html>
