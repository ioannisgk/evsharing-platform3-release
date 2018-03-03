<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@include file="header.jsp"%>

        <div id="page-wrapper">
            <div class="row">
                <div class="col-lg-12">
                    <h1 class="page-header">Manage Stations</h1>
                </div>
                <!-- /.col-lg-12 -->
            </div>
            <!-- /.row -->
            <div class="row">
                <div class="col-lg-8">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <i class="fa fa-sitemap fa-fw"></i> Stations Details
                        </div>
                        <!-- /.panel-heading -->
                        <div class="panel-body">
                            <div class="table-responsive">
                                <table class="table table-hover">
                                    <thead>
                                        <tr>
                                            <th>Station Name</th>
                                            <th>Latitude</th>
                                            <th>Longitude</th>
                                            <th>Traffic Level</th>
                                            <th>Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                    
                                    <!-- Loop over and print the stations -->
									<c:forEach var="tempStation" items="${stations}">
									
										<!-- Construct an update link with station id -->
										<c:url var="updateLink" value="/station/update-station" >
											<c:param name="stationId" value="${tempStation.id}" />
										</c:url>
										
										<!-- Construct an delete link with station id -->
										<c:url var="deleteLink" value="/station/delete-station" >
											<c:param name="stationId" value="${tempStation.id}" />
										</c:url>
                                    
                                        <tr>
                                            <td>${tempStation.name}</td>
                                            <td>${tempStation.latitude}</td>
                                            <td>${tempStation.longitude}</td>
                                            <td>${tempStation.trafficLevel}</td>
                                            <td>
                                            	<a href="${updateLink}" class="btn btn-primary btn-outline btn-warning btn-xs">Update</a>

												<!-- Button trigger modal -->
					                            <button class="btn btn-primary btn-outline btn-danger btn-xs" data-toggle="modal" data-target="#myModal-${tempStation.id}-${tempStation.used}">
					                                Delete
					                            </button>
					                            <!-- Modal -->
					                            <div class="modal fade" id="myModal-${tempStation.id}-false" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
					                                <div class="modal-dialog">
					                                    <div class="modal-content">
					                                        <div class="modal-header">
					                                            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					                                            <h4 class="modal-title" id="myModalLabel">Warning!</h4>
					                                        </div>
					                                        <div class="modal-body">
					                                            Are you sure you want to delete this station <strong>${tempStation.name}</strong>?
					                                            </br>This action can not be undone.
					                                        </div>
					                                        <div class="modal-footer">
					                                            <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
					                                            <a href="${deleteLink}" class="btn btn-primary btn-outline btn-danger">Delete</a>
					                                        </div>
					                                    </div>
					                                    <!-- /.modal-content -->
					                                </div>
					                                <!-- /.modal-dialog -->
					                            </div>
					                            <!-- /.modal -->
					                            <!-- Modal -->
					                            <div class="modal fade" id="myModal-${tempStation.id}-true" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
					                                <div class="modal-dialog">
					                                    <div class="modal-content">
					                                        <div class="modal-header">
					                                            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					                                            <h4 class="modal-title" id="myModalLabel">Warning!</h4>
					                                        </div>
					                                        <div class="modal-body">
					                                            You can not delete this station <strong>${tempStation.name}</strong>.
					                                            </br>It is already used in a saved route.
					                                        </div>
					                                        <div class="modal-footer">
					                                            <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
					                                        </div>
					                                    </div>
					                                    <!-- /.modal-content -->
					                                </div>
					                                <!-- /.modal-dialog -->
					                            </div>
					                            <!-- /.modal -->
                                            </td>
                                        </tr>
                                        
                                    </c:forEach>
                                        
                                    </tbody>
                                </table>
                            </div>
                            <!-- /.table-responsive -->
                        </div>
                        <!-- /.panel-body -->
                    </div>
                    <!-- /.panel -->
                </div>
                <!-- /.col-lg-8 -->
                <div class="col-lg-2">
                  <div class="panel panel-green">
                      <div class="panel-heading">
                          <div class="row">
                              <div class="col-xs-3">
                                  <i class="fa fa-sitemap fa-5x"></i>
                              </div>
                              <div class="col-xs-9 text-right">
                                  <div class="huge">${countStations}</div>
                                  <div>Total Stations!</div>
                              </div>
                          </div>
                      </div>
                      <a href="#">
                          <div class="panel-footer">
                              <span class="pull-left">Viewing Details</span>
                              <span class="pull-right"><i class="fa fa-dot-circle-o"></i></span>
                              <div class="clearfix"></div>
                          </div>
                      </a>
                  </div>
                </div>
                <!-- /.col-lg-2 -->
                <div class="col-lg-2">
                  <div class="panel panel-default">
                      <div class="panel-heading">
                          <i class="fa fa-plus-circle fa-fw"></i> Add Stations
                      </div>
                      <div class="panel-body">
                          <p></p>
                          <form action="add-new-station" method="get" id="form1"></form>
                          <button type="submit" form="form1" class="btn btn-outline btn-success col-md-10">Add New Station</button>
                          </br></br></br>
                      </div>
                  </div>
                  <!-- /.panel -->
                </div>
                <!-- /.col-lg-2 -->
                <div class="col-lg-2">
                  <div class="panel panel-default">
                      <div class="panel-heading">
                          <i class="fa fa-arrow-circle-left fa-fw"></i> Manage Stations
                      </div>
                      <div class="panel-body">
                          <p>Station names, coordinates and traffic levels are shown and they can be created or updated. This panel is protected.</p>
                      </div>
                  </div>
                  <!-- /.panel -->
                </div>
                <!-- /.col-lg-2 -->
                <div class="col-lg-2">
                  <div class="panel panel-default">
                      <div class="panel-heading">
                          <i class="fa fa-cloud-upload fa-fw"></i> Upload XML
                      </div>
                      <div class="panel-body">
                          <p></p>
                          
                          <form:form action="upload-map" method="POST" enctype="multipart/form-data">
                          
	                          <div class="form-group">
	                              <input type="file" name="file" /></br>
	                              <input type="submit" value="Upload XML" class="btn btn-outline btn-success col-md-10"/>
	                          </div>
                          
                          </form:form>
                          <p>${infoMapMessage}<p>
                          
                      </div>
                  </div>
                  <!-- /.panel -->
                </div>
                <!-- /.col-lg-2 -->
            </div>
            <!-- /.row -->
            
        <%@include file="footer.jsp"%>