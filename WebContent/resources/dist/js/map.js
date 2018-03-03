function initMap() {
	
		// Get stations data from jsp page in string format
		var stationsFromJsp = $('#stations').val();
		
		// Convert stations string to array
		var stations = eval('[' + stationsFromJsp + ']');

        var infos = [];

        var map = new google.maps.Map(document.getElementById('map'), {
              zoom: 10,
              center: new google.maps.LatLng(0, 0)
        });

        // Create bounds for map
        var bounds = new google.maps.LatLngBounds();
        
        // Iterate stations array and create markers

        for (var i = 0; i < stations[0].length; i++) {
	          var station = stations[0][i];
	          var myLatLng = new google.maps.LatLng(station[1], station[2]);
	
	          var marker = new google.maps.Marker({
	              position: myLatLng,
	              map: map
	          });
	          
	          // Create info window and add click listener for each marker
	
	          var infowindow = new google.maps.InfoWindow;
	          var content = '<strong>' + station[0] + '</strong>' + '</br>Traffic Level: ' + station[3];
	          
	          google.maps.event.addListener(marker,'click', (function(marker, content, infowindow){
	              return function() {
	                  closeInfos();
	                  infowindow.setContent(content);
	                  infowindow.open(map,marker);
	                  infos[0] = infowindow;
	              };
	          })(marker, content, infowindow));
	
	          bounds.extend(myLatLng);
        }
        map.fitBounds(bounds);

        // Function to close previous info window
        
        function closeInfos(){
	          if(infos.length > 0){
	            infos[0].set("marker", null);
	            infos[0].close();
	            infos.length = 0;
	          }
        }
}
