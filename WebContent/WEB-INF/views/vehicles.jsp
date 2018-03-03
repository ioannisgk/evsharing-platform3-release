<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@include file="header.jsp"%>

        <div id="page-wrapper">
            <div class="row">
                <div class="col-lg-12">
                    <h1 class="page-header">Manage Vehicles</h1>
                </div>
                <!-- /.col-lg-12 -->
            </div>
            <!-- /.row -->
            <div class="row">
                <div class="col-lg-8">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <i class="fa fa-automobile fa-fw"></i> Vehicles Details
                        </div>
                        <!-- /.panel-heading -->
                        <div class="panel-body">
                            <div class="table-responsive">
                                <table class="table table-hover">
                                    <thead>
                                        <tr>
                                            <th>License Plates</th>
                                            <th>Model Name</th>
                                            <th>Charge %</th>
                                            <th>At Station</th>
                                            <th></th>
                                            <th>Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                    
                                    <!-- Loop over and print the vehicles -->
									<c:forEach var="tempVehicle" items="${vehicles}">
									
										<!-- Construct an update link with vehicle id -->
										<c:url var="updateLink" value="/vehicle/update-vehicle" >
											<c:param name="vehicleId" value="${tempVehicle.id}" />
										</c:url>
										
										<!-- Construct an delete link with vehicle id -->
										<c:url var="deleteLink" value="/vehicle/delete-vehicle" >
											<c:param name="vehicleId" value="${tempVehicle.id}" />
										</c:url>

                                        <tr>
                                            <td>${tempVehicle.licensePlates}</td>
                                            <td>${tempVehicle.model}</td>
                                            <td>${tempVehicle.charge}</td>
                                            <td>${tempVehicle.currentStationName}</td>
                                            <td>
                                            
                                            	<c:choose>
												    <c:when test="${tempVehicle.available}" >
												    <c:set var = "description" value = "fa  fa-fw"/>
												    </c:when>
												    <c:otherwise>
												    <c:set var = "description" value = "fa fa-warning fa-fw"/>
												    </c:otherwise>
												</c:choose>
                                            
                                            	<i class="${description}"></i>

                                            </td>
                                            <td>
                                            	<a href="${updateLink}" class="btn btn-primary btn-outline btn-warning btn-xs">Update</a>

												<!-- Button trigger modal -->
					                            <button class="btn btn-primary btn-outline btn-danger btn-xs" data-toggle="modal" data-target="#myModal-${tempVehicle.id}-${tempVehicle.used}">
					                                Delete
					                            </button>
					                            <!-- Modal -->
					                            <div class="modal fade" id="myModal-${tempVehicle.id}-false" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
					                                <div class="modal-dialog">
					                                    <div class="modal-content">
					                                        <div class="modal-header">
					                                            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					                                            <h4 class="modal-title" id="myModalLabel">Warning!</h4>
					                                        </div>
					                                        <div class="modal-body">
					                                            Are you sure you want to delete this vehicle <strong>${tempVehicle.licensePlates}</strong>?
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
					                            <div class="modal fade" id="myModal-${tempVehicle.id}-true" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
					                                <div class="modal-dialog">
					                                    <div class="modal-content">
					                                        <div class="modal-header">
					                                            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					                                            <h4 class="modal-title" id="myModalLabel">Warning!</h4>
					                                        </div>
					                                        <div class="modal-body">
					                                            You can not delete this vehicle <strong>${tempVehicle.licensePlates}</strong>.
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
                  <div class="panel panel-yellow">
                      <div class="panel-heading">
                          <div class="row">
                              <div class="col-xs-3">
                                  <i class="fa fa-automobile fa-5x"></i>
                              </div>
                              <div class="col-xs-9 text-right">
                                  <div class="huge">${countVehicles}</div>
                                  <div>Total Vehicles!</div>
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
                          <i class="fa fa-plus-circle fa-fw"></i> Add Vehicles
                      </div>
                      <div class="panel-body">
                          <p></p>
                          <form action="add-new-vehicle" method="get" id="form1"></form>
                          <button type="submit" form="form1" class="btn btn-outline btn-warning col-md-10">Add New Vehicle</button>
                          </br></br></br>
                      </div>
                  </div>
                  <!-- /.panel -->
                </div>
                <!-- /.col-lg-2 -->
                <div class="col-lg-2">
                  <div class="panel panel-default">
                      <div class="panel-heading">
                          <i class="fa fa-arrow-circle-left fa-fw"></i> Manage Vehicles
                      </div>
                      <div class="panel-body">
                          <p>Vehicles plates, models and charge levels are shown and they can be created or updated. This panel is protected.</p>
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
                          
                          <form:form action="upload-vehicles" method="POST" enctype="multipart/form-data">
                          
	                          <div class="form-group">
	                              <input type="file" name="file" /></br>
	                              <input type="submit" value="Upload XML" class="btn btn-outline btn-warning col-md-10"/>
	                          </div>
                          
                          </form:form>
                          <p>${infoVehicleMessage}<p>
                          
                      </div>
                  </div>
                  <!-- /.panel -->
                </div>
                <!-- /.col-lg-2 -->
            </div>
            <!-- /.row -->
            
        <%@include file="footer.jsp"%>