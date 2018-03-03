<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@include file="header.jsp"%>

<c:choose>
    <c:when test="${simulationStatus}" >
    <c:set var = "myclass" value = "alert alert-success col-md-5"/>
    <c:set var = "text" value = "SIMULATION IS RUNNING"/>
    </c:when>
    <c:otherwise>
    <c:set var = "myclass" value = "alert alert-danger col-md-5"/>
    <c:set var = "text" value = "SIMULATION IS STOPPED"/>
    </c:otherwise>
</c:choose>

<!-- Pass stations string variable to javascript file -->
<input type="hidden" id="stations" value="${stations}"/>

        <div id="page-wrapper">
            <div class="row">
                <div class="col-lg-12">
                    <h1 class="page-header">Simulation Panel</h1>
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
                            <i class="fa fa-road fa-fw"></i> Requests for Routes Details
                        </div>
                        <!-- /.panel-heading -->
                        <div class="panel-body">
                            <div class="table-responsive">
                                <table class="table table-hover">
                                    <thead>
                                        <tr>
                                        	<th>Request Message</th>
                                            <th>Start Station</th>
                                            <th>Finish Station</th>
                                            <th>Start Time</th>
                                            <th>Username</th>
                                            <th>Action</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        
                                    <!-- Loop over and print the simulations -->
									<c:forEach var="tempSimulation" items="${simulations}">
										
										<!-- Construct an delete link with simulation id -->
										<c:url var="deleteLink" value="/simulation/delete-simulation" >
											<c:param name="simulationId" value="${tempSimulation.id}" />
										</c:url>
                                    
                                        <tr>
                                        	<td>"${tempSimulation.message}"</td>
                                            <td>${tempSimulation.currentStationStartName}</td>
                                            <td>${tempSimulation.currentStationFinishName}</td>
                                            <td>${tempSimulation.currentStartTime}</td>
                                            <td>${tempSimulation.currentUsername}</td>
                                            <td>

												<!-- Button trigger modal -->
					                            <button class="btn btn-primary btn-outline btn-danger btn-xs" data-toggle="modal" data-target="#myModal-${tempSimulation.id}">
					                                Delete
					                            </button>
					                            <!-- Modal -->
					                            <div class="modal fade" id="myModal-${tempSimulation.id}" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
					                                <div class="modal-dialog">
					                                    <div class="modal-content">
					                                        <div class="modal-header">
					                                            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					                                            <h4 class="modal-title" id="myModalLabel">Warning!</h4>
					                                        </div>
					                                        <div class="modal-body">
					                                            Are you sure you want to delete this message request <strong>"${tempSimulation.message}"</strong>?
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
                  <div class="panel panel-primary">
                      <div class="panel-heading">
                          <i class="fa fa-users fa-fw"></i> Users
                      </div>
                      <div class="panel-body">
                          <p>
                          <form role="form">
                              
                              <fieldset>
                                  <div class="form-group">
                                      <select id="user" class="form-control">
                                      
                                      <!-- Loop over and print the users -->
                                      <c:forEach items="${usersOptions}" var="entry">
                                          <option value="${entry.key}">${entry.value}</option>
                                      </c:forEach>
                                      
                                      </select>
                                  </div>
                                  <button type="submit" id="select-user" class="btn btn-outline btn-primary col-md-9">Show ID</button>
                              </fieldset>
                              
                          </form>
                          </p>
                          <div class="list-group">
                                <a href="#" class="list-group-item">
                                    User ID: 
                                    <span id="show-user-id"></span>
                                </a>
                          </div>
                      </div>
                  </div>
                  <!-- /.panel -->
                </div>
                <!-- /.col-lg-2 -->
                <div class="col-lg-2">
                  <div class="panel panel-green">
                      <div class="panel-heading">
                          <i class="fa fa-sitemap fa-fw"></i> Stations
                      </div>
                      <div class="panel-body">
                          <p>
                          <form role="form">
                              
                              <fieldset>
                                  <div class="form-group">
                                      <select id="station" class="form-control">
                                      
                                      <!-- Loop over and print the stations -->
                                      <c:forEach items="${stationsOptions}" var="entry">
                                          <option value="${entry.key}">${entry.value}</option>
                                      </c:forEach>
                                          
                                      </select>
                                  </div>
                                  <button type="submit" id="select-station" class="btn btn-outline btn-primary col-md-9">Show ID</button>
                              </fieldset>
                              
                          </form>
                          </p>
                          <div class="list-group">
                                <a href="#" class="list-group-item">
                                    Station ID: 
                                    <span id="show-station-id"></span>
                                </a>
                          </div>
                      </div>
                  </div>
                  <!-- /.panel -->
                </div>
                <!-- /.col-lg-2 -->
                <div class="col-lg-2">
                  <div class="panel panel-default">
                      <div class="panel-heading">
                          <i class="fa fa-info-circle fa-fw"></i> Info Panel
                      </div>
                      <div class="panel-body">
                          <p>The list shows each request details, and requests can be loaded from a file or deleted.</p>
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
                          
                          <form:form action="upload-simulation" method="POST" enctype="multipart/form-data">
                          
	                          <div class="form-group">
	                              <input type="file" name="file" /></br>
	                              <input type="submit" value="Upload XML" class="btn btn-outline btn-success col-md-10"/>
	                          </div>
                          
                          </form:form>
                          <p>${infoSimulationMessage}<p>
                          
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
                              <button type="button" id="start-simulation" class="btn btn-outline btn-success col-md-5">START SIMULATION</button>
                              <div class="col-md-2"></div>
                              <button type="button" id="stop-simulation" class="btn btn-outline btn-danger col-md-5">STOP SIMULATION</button>
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