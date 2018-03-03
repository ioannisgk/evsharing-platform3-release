<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@include file="header.jsp"%>

<c:choose>
    <c:when test="${empty param.routeId}" >
    <c:set var = "description" value = "Add New Route"/>
    </c:when>
    <c:otherwise>
    <c:set var = "description" value = "Update Route"/>
    </c:otherwise>
</c:choose>

        <div id="page-wrapper">
            <div class="row">
                <div class="col-lg-12">
                    <h1 class="page-header"><c:out value = "${description}"/></h1>
                </div>
                <!-- /.col-lg-12 -->
            </div>
            <!-- /.row -->
            <div class="row">
                <div class="col-lg-8">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <i class="fa fa-road fa-fw"></i> Route Details
                        </div>
                        <!-- /.panel-heading -->
                        <div class="panel-body">
                            <div class="table-responsive">
                            
                            <form:form action="save-route" modelAttribute="route" method="POST">
		
							<!-- Associate data of current form with route id -->
							<form:hidden path="id" />
							
							<!-- Associate current status with current route id -->
							<form:hidden path="status" />
                            
                                <table class="table">
                                    <thead>
                                        <tr>
                                            <th>#</th>
                                            <th>Attribute</th>
                                            <th>Value</th>
                                            <th>Info</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr>
                                            <td>1</td>
                                            <td>Start Station</td>
                                            <td>
                                              <fieldset>
                                                  <div class="form-group">
                                                  
	                                                  <form:select path="startStationId" class="form-control" style="width:50%;">
													  	  <form:options items="${stationsOptions}" />
													  </form:select>
                                                  
                                                  </div>
                                              </fieldset>
                                            </td>
                                            <td><p class="text-muted">Select start station name</p></td>
                                        </tr>
                                        <tr>
                                          <td>2</td>
                                          <td>Finish Station</td>
                                          <td>
                                            <fieldset>
                                                <div class="form-group">
                                                    
													  <form:select path="finishStationId" class="form-control" style="width:50%;">
													  	  <form:options items="${stationsOptions}" />
													  </form:select>
													  
                                                </div>
                                            </fieldset>
                                          </td>
                                          <td><p class="text-muted">Select finish station name</p></td>
                                        </tr>
                                        <tr>
                                          <td>3</td>
                                          <td>Start Time</td>
                                          <td>
                                            <div class="form-group"> 
                                            	<form:input path="startTime" class="form-control" style="width:50%;" placeholder="06:00" />
                                            </div>
                                          </td>
                                          <td>
                                          	  <p class="text-muted">Start time 06:05 - 22:00</p>
	                                          <form:errors path="startTime" class="text-muted" />  
                                          </td>
                                        </tr>
                                        <tr>
                                            <td>4</td>
                                            <td>End Time</td>
                                            <td>
                                              <div class="form-group">
                                                  <form:input path="endTime" class="form-control" style="width:50%;" placeholder="06:00" />
                                              </div>
                                            </td>
                                            <td>
                                            	<p class="text-muted">End time 06:05 - 22:00</p>
                                            	<form:errors path="endTime" class="text-muted" />
                                            </td>
                                        </tr>
                                        <tr>
                                          <td>5</td>
                                          <td>Username</td>
                                          <td>
                                            <fieldset>
                                                <div class="form-group">
                                                
	                                                <form:select path="userId" class="form-control" style="width:50%;">
														<form:options items="${usersOptions}" />
													</form:select>
                                                
                                                </div>
                                            </fieldset>
                                          </td>
                                          <td><p class="text-muted">Select user for this route</p></td>
                                        </tr>
                                        <tr>
                                          <td>6</td>
                                          <td>License Plates</td>
                                          <td>
                                            <fieldset>
                                                <div class="form-group">
                                                
	                                                <form:select path="vehicleId" class="form-control" style="width:50%;">
														<form:options items="${vehiclesOptions}" />
													</form:select>
                                                
                                                </div>
                                            </fieldset>
                                          </td>
                                          <td><p class="text-muted">Select vehicle license plates</p></td>
                                        </tr>
                                        <tr>
                                              <td></td>
                                              <td></td>
                                              <td><input type="submit" value="${description}" class="btn btn-outline btn-danger" /></td>
                                              <td><p class="text-muted">Click to save the route ${errorMessage}</p></td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                            <!-- /.table-responsive -->
                            
                            </form:form>
                            
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
                              <span class="pull-left">Editing Details</span>
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
                          <i class="fa fa-chevron-circle-left fa-fw"></i> Back to Dashboard
                      </div>
                      <div class="panel-body">
                          <p></p>
                          <form action="../main" method="get" id="form1"></form>
                          <button type="submit" form="form1" class="btn btn-outline btn-danger col-md-10">Back to Dashboard</button>
                          </br></br></br>
                      </div>
                  </div>
                  <!-- /.panel -->
                </div>
                <!-- /.col-lg-2 -->
                <div class="col-lg-2">
                  <div class="panel panel-default">
                      <div class="panel-heading">
                          <i class="fa fa-arrow-circle-left fa-fw"></i> Update Details
                      </div>
                      <div class="panel-body">
                          <p>Start and finish station, start and end time, username and license plates can be updated.</p>
                      </div>
                  </div>
                  <!-- /.panel -->
                </div>
                <!-- /.col-lg-2 -->
                <div class="col-lg-2">
                  <div class="panel panel-default">
                      <div class="panel-heading">
                          <i class="fa fa-info-circle fa-fw"></i> Info
                      </div>
                      <div class="panel-body">
                          <p>The administrator can create a new route and it will be saved to the database.</p>
                      </div>
                  </div>
                  <!-- /.panel -->
                </div>
                <!-- /.col-lg-2 -->
            </div>
            <!-- /.row -->
            
        <%@include file="footer.jsp"%>