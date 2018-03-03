<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@include file="header.jsp"%>

<c:choose>
    <c:when test="${empty param.vehicleId}" >
    <c:set var = "description" value = "Add New Vehicle"/>
    <c:set var = "readonly" value = "false"/>
    </c:when>
    <c:otherwise>
    <c:set var = "description" value = "Update Vehicle"/>
    <c:set var = "readonly" value = "true"/>
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
                            <i class="fa fa-automobile fa-fw"></i> Vehicle Details
                        </div>
                        <!-- /.panel-heading -->
                        <div class="panel-body">
                            <div class="table-responsive">
                            
                            <form:form action="save-vehicle" modelAttribute="vehicle" method="POST">
		
							<!-- Associate data of current form with vehicle id -->
							<form:hidden path="id" />
							
							<!-- Associate current station id with current vehicle id -->
							<form:hidden path="stationId" />
                            
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
                                            <td>License Plates</td>
                                            <td>
                                              <div class="form-group">
                                                  <form:input path="licensePlates" class="form-control" style="width:50%;" placeholder="License Plates" readonly="${readonly}"/>
                                              </div>
                                            </td>
                                            <td>
                                            	<p class="text-muted">Enter vehicle license plates</p>
                                                <form:errors path="licensePlates" class="text-muted" />
                                            </td>
                                        </tr>
                                        <tr>
                                          <td>2</td>
                                          <td>Model Name</td>
                                          <td>
                                            <div class="form-group">
                                                <form:input path="model" class="form-control" style="width:50%;" placeholder="Model Name"/>
                                            </div>
                                          </td>
                                          <td>
                                          	  <p class="text-muted">Enter vehicle model name</p>
                                              <form:errors path="model" class="text-muted" />
                                          </td>
                                        </tr>
                                        <tr>
                                          <td>3</td>
                                          <td>Charge %</td>
                                          <td>
                                            <div class="form-group">
                                                <form:input path="charge" class="form-control" style="width:50%;" placeholder="0.00 - 100.00%" />
                                            </div>
                                          </td>
                                          <td>
                                          	  <p class="text-muted">Enter vehicle charge level</p>
                                              <form:errors path="charge" class="text-muted" />
                                          </td>
                                        </tr>
                                        <tr>
                                          <td>4</td>
                                          <td>Availability</td>
                                          <td>
                                            <div class="form-group">
                                              <div class="checkbox">
                                                  <label>Available</label>
                                                  <form:checkbox path="available" value="1" style="margin-left: 15px;" />
                                              </div>
                                            </div>
                                          </td>
                                          <td><p class="text-muted">Check if vehicle is available</p></td>
                                        </tr>
                                        <tr>
                                              <td></td>
                                              <td></td>
                                              <td><input type="submit" value="${description}" class="btn btn-outline btn-warning" /></td>
                                              <td><p class="text-muted">Click to save the vehicle ${errorMessage}</p></td>
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
                          <i class="fa fa-chevron-circle-left fa-fw"></i> Back to Vehicles
                      </div>
                      <div class="panel-body">
                          <p></p>
                          <form action="list" method="get" id="form1"></form>
                          <button type="submit" form="form1" class="btn btn-outline btn-warning col-md-10">Back to Vehicles</button>
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
                          <p>Vehicle license plates, model, charge level and availability can be created or updated.</p>
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
                          <p>The administrator can create a new vehicle and it will be saved to the database.</p>
                      </div>
                  </div>
                  <!-- /.panel -->
                </div>
                <!-- /.col-lg-2 -->
            </div>
            <!-- /.row -->
            
        <%@include file="footer.jsp"%>