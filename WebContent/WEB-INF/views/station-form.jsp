<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@include file="header.jsp"%>

<c:choose>
    <c:when test="${empty param.stationId}" >
    <c:set var = "description" value = "Add New Station"/>
    </c:when>
    <c:otherwise>
    <c:set var = "description" value = "Update Station"/>
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
                            <i class="fa fa-sitemap fa-fw"></i> Station Details
                        </div>
                        <!-- /.panel-heading -->
                        <div class="panel-body">
                            <div class="table-responsive">
                            
                            <form:form action="save-station" modelAttribute="station" method="POST">
		
							<!-- Associate data of current form with station id -->
							<form:hidden path="id" />
                            
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
                                            <td>Station Name</td>
                                            <td>
                                              <div class="form-group">
                                                  <form:input path="name" class="form-control" style="width:50%;" placeholder="Station Name" />
                                              </div>
                                            </td>
                                            <td>
                                            	<p class="text-muted">Enter station name</p>
                                                <form:errors path="name" class="text-muted" />
                                            </td>
                                        </tr>
                                        <tr>
                                          <td>2</td>
                                          <td>Latitude</td>
                                          <td>
                                            <div class="form-group">
                                                <form:input path="latitude" class="form-control" style="width:50%;" placeholder="Latitude" />
                                            </div>
                                          </td>
                                          <td>
                                          	  <p class="text-muted">Enter station latitude</p>
                                              <form:errors path="latitude" class="text-muted" />
                                          </td>
                                        </tr>
                                        <tr>
                                          <td>3</td>
                                          <td>Longitude</td>
                                          <td>
                                            <div class="form-group">
                                                <form:input path="longitude" class="form-control" style="width:50%;" placeholder="Longitude" />
                                            </div>
                                          </td>
                                          <td>
                                          	  <p class="text-muted">Enter station longitude</p>
                                              <form:errors path="longitude" class="text-muted" />
                                          </td>
                                        </tr>
                                        <tr>
                                          <td>4</td>
                                          <td>Traffic Level</td>
                                          <td>
                                            <fieldset>
                                                <div class="form-group">
                                                
                                                    <form:select path="trafficLevel" class="form-control" style="width:50%;">
														<form:option value="1" label="Very Low" />
														<form:option value="2" label="Low" />
														<form:option value="3" label="Medium" />
														<form:option value="4" label="High" />
														<form:option value="5" label="Very High" />
													</form:select>
													
                                                </div>
                                            </fieldset>
                                          </td>
                                          <td><p class="text-muted">Select station traffic level</p></td>
                                        </tr>
                                        <tr>
                                              <td></td>
                                              <td></td>
                                              <td><input type="submit" value="${description}" class="btn btn-outline btn-success" /></td>
                                              <td><p class="text-muted">Click to save the station ${errorMessage}</p></td>
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
                          <i class="fa fa-chevron-circle-left fa-fw"></i> Back to Stations
                      </div>
                      <div class="panel-body">
                          <p></p>
                          <form action="list" method="get" id="form1"></form>
                          <button type="submit" form="form1" class="btn btn-outline btn-success col-md-10">Back to Stations</button>
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
                          <p>Station full name, latitude, longitude and traffic level can be created or updated.</p>
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
                          <p>The administrator can create a new station and it will be saved to the database.</p>
                      </div>
                  </div>
                  <!-- /.panel -->
                </div>
                <!-- /.col-lg-2 -->
            </div>
            <!-- /.row -->
            
        <%@include file="footer.jsp"%>