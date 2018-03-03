<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@include file="header.jsp"%>

<c:choose>
    <c:when test="${serviceStatus}" >
    <c:set var = "myclass" value = "alert alert-success col-md-5"/>
    <c:set var = "text" value = "SERVICE IS RUNNING"/>
    </c:when>
    <c:otherwise>
    <c:set var = "myclass" value = "alert alert-danger col-md-5"/>
    <c:set var = "text" value = "SERVICE IS STOPPED"/>
    </c:otherwise>
</c:choose>

<!-- Pass stations string variable to javascript file -->
<input type="hidden" id="stations" value="${stations}"/>

        <div id="page-wrapper">
            <div class="row">
                <div class="col-lg-12">
                    <h1 class="page-header">Dashboard Panel</h1>
                </div>
                <!-- /.col-lg-12 -->
            </div>
            <!-- /.row -->
            <div class="row">
                <div class="col-lg-8">
                    <div id="map" style="width: 100%; height: 400px; background-color: grey;"></div>
                    </br>
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <i class="fa fa-road fa-fw"></i> Accepted Routes Details
                        </div>
                        <!-- /.panel-heading -->
                        <div class="panel-body">
                            <div class="table-responsive">
                                <table class="table table-hover">
                                    <thead>
                                        <tr>
                                            <th>Start Station</th>
                                            <th>Finish Station</th>
                                            <th>Start Time</th>
                                            <th>End Time</th>
                                            <th>Username</th>
                                            <th>License Plates</th>
                                            <th>Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                    
                                    <!-- Loop over and print the routes -->
									<c:forEach var="tempRoute" items="${routes}">
									
										<!-- Construct an update link with route id -->
										<c:url var="updateLink" value="/home/route/update-route" >
											<c:param name="routeId" value="${tempRoute.id}" />
										</c:url>
										
										<!-- Construct an delete link with route id -->
										<c:url var="deleteLink" value="/home/route/delete-route" >
											<c:param name="routeId" value="${tempRoute.id}" />
										</c:url>
					
                                        <tr>
                                            <td>${tempRoute.currentStationStartName}</td>
                                            <td>${tempRoute.currentStationFinishName}</td>
                                            <td>${tempRoute.startTime}</td>
                                            <td>${tempRoute.endTime}</td>
                                            <td>${tempRoute.currentUsername}</td>
                                            <td>${tempRoute.currentLicensePlates}</td>
                                            <td>
                                            	<a href="${updateLink}" class="btn btn-primary btn-outline btn-warning btn-xs">Update</a>
                                            		
												<!-- Button trigger modal -->
					                            <button class="btn btn-primary btn-outline btn-danger btn-xs" data-toggle="modal" data-target="#myModal-${tempRoute.id}">
					                                Delete
					                            </button>
					                            <!-- Modal -->
					                            <div class="modal fade" id="myModal-${tempRoute.id}" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
					                                <div class="modal-dialog">
					                                    <div class="modal-content">
					                                        <div class="modal-header">
					                                            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					                                            <h4 class="modal-title" id="myModalLabel">Warning!</h4>
					                                        </div>
					                                        <div class="modal-body">
					                                            Are you sure you want to delete this route <strong>${tempRoute.currentStationStartName}</strong>?
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
                  <div class="panel panel-red">
                      <div class="panel-heading">
                          <div class="row">
                              <div class="col-xs-3">
                                  <i class="fa fa-road fa-5x"></i>
                              </div>
                              <div class="col-xs-9 text-right">
                                  <div class="huge">${countRoutes}</div>
                                  <div>Total Routes!</div>
                              </div>
                          </div>
                      </div>
                      <a href="#">
                          <div class="panel-footer">
                              <span class="pull-left">View Details</span>
                              <span class="pull-right"><i class="fa fa-arrow-circle-right"></i></span>
                              <div class="clearfix"></div>
                          </div>
                      </a>
                  </div>
                  <!-- /.panel -->
                </div>
                <!-- /.col-lg-2 -->
                <div class="col-lg-2">
                  <div class="panel panel-primary">
                      <div class="panel-heading">
                          <div class="row">
                              <div class="col-xs-3">
                                  <i class="fa fa-users fa-5x"></i>
                              </div>
                              <div class="col-xs-9 text-right">
                                  <div class="huge">${countUsers}</div>
                                  <div>Total Users!</div>
                              </div>
                          </div>
                      </div>
                      <a href="../user/list">
                          <div class="panel-footer">
                              <span class="pull-left">View Details</span>
                              <span class="pull-right"><i class="fa fa-arrow-circle-right"></i></span>
                              <div class="clearfix"></div>
                          </div>
                      </a>
                  </div>
                </div>
                <!-- /.col-lg-2 -->
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
                      <a href="../station/list">
                          <div class="panel-footer">
                              <span class="pull-left">View Details</span>
                              <span class="pull-right"><i class="fa fa-arrow-circle-right"></i></span>
                              <div class="clearfix"></div>
                          </div>
                      </a>
                  </div>
                </div>
                <!-- /.col-lg-2 -->
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
                      <a href="../vehicle/list">
                          <div class="panel-footer">
                              <span class="pull-left">View Details</span>
                              <span class="pull-right"><i class="fa fa-arrow-circle-right"></i></span>
                              <div class="clearfix"></div>
                          </div>
                      </a>
                  </div>
                </div>
                <!-- /.col-lg-2 -->
                <div class="col-lg-2">
                  <div class="panel panel-default">
                      <div class="panel-heading">
                          <i class="fa fa-info-circle fa-fw"></i> Info Panel
                      </div>
                      <div class="panel-body">
                          <p>The map shows the stations from the database, and routes can be created or updated.</p>
                      </div>
                  </div>
                </div>
                <!-- /.col-lg-2 -->
                <div class="col-lg-2">
                  <div class="panel panel-default">
                      <div class="panel-heading">
                          <i class="fa fa-plus-circle fa-fw"></i> Add Routes
                      </div>
                      <div class="panel-body">
                          <p></p>
                          <form action="route/add-new-route" method="get" id="form1"></form>
                          <button type="submit" form="form1" class="btn btn-outline btn-danger col-md-10">Add New Route</button>
                          </br></br></br>
                      </div>
                  </div>
                  <!-- /.panel -->
                </div>
                <!-- /.col-lg-2 -->
                <div class="col-lg-4">
                  <div class="panel panel-default">
                      <div class="panel-heading">
                          <i class="fa fa-bell fa-fw"></i> System Status
                      </div>
                      <!-- /.panel-heading -->
                      <div class="panel-body">
                          <p>
                            <div class="col-md-5">
                              <fieldset>
                                  <div class="form-group">
                                      <select id="mode" class="form-control">
                                          <option value="shortMode">Short Term Mode</option>
                                          <option value="longMode">Long Term Mode</option>
                                      </select>
                                  </div>
                              </fieldset>
                            </div>
                            <div class="col-md-2"></div>
                            <div id="info">
	                            <div class="${myclass}"><strong>${text}</strong></div>
                            </div>
                          <p>
                          <p>
                              <button type="button" id="start-service" class="btn btn-outline btn-success col-md-5">START SERVICE</button>
                              <div class="col-md-2"></div>
                              <button type="button" id="stop-service" class="btn btn-outline btn-danger col-md-5">STOP SERVICE</button>
                          </p>
                      </div>
                      <!-- .panel-body -->
                  </div>
                  <!-- /.panel -->
                  <div class="panel panel-default">
                      <div class="panel-heading">
                          <i class="fa fa-info-circle fa-fw"></i> Notifications Panel
                      </div>
                      <!-- /.panel-heading -->
                      <div class="panel-body">
                          <p>Requests Accepted <span class="pull-right ">${acceptedRequests}</span></p>
                          <p>Requests Denied <span class="pull-right ">${deniedRequests}</span></p>
                          <p>Efficiency Rate <span class="pull-right ">${efficiencyRatio}%</span></p>
                      </div>
                      <!-- /.panel-body -->
                  </div>
                  <!-- /.panel -->
                </div>
                <!-- /.col-lg-4 -->
            </div>
            <!-- /.row -->
	
    <%@include file="footer.jsp"%>