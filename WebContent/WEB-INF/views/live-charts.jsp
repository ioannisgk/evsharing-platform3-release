<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@include file="header.jsp"%>

<c:set var = "systemStatus" value = "${systemStatus}"/>

<!-- If the service or the simulation is running, show the live charts -->
<% if ((boolean)pageContext.getAttribute("systemStatus") == true) { %>

        <div id="page-wrapper">
            <div class="row">
                <div class="col-lg-12">
                    <h1 class="page-header">Live Charts</h1>
                </div>
                <!-- /.col-lg-12 -->
            </div>
            <!-- /.row -->
            <div class="row">
                <div class="col-lg-10">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <i class="fa fa-bar-chart-o fa-fw"></i> Requests Accepted & Denied over Time
                        </div>
                        <!-- /.panel-heading -->
                        <div class="panel-body">
                            <div id="morris-area-chart"></div>
                        </div>
                        <!-- /.panel-body -->
                    </div>
                    <!-- /.panel -->
                </div>
                <!-- /.col-lg-10 -->
                <div class="col-lg-2">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <i class="fa fa-signal fa-fw"></i> Efficiency Rate
                        </div>
                        <!-- /.panel-heading -->
                        <div class="panel-body">
                            <div id="morris-donut-chart"></div>
                        </div>
                        <!-- /.panel-body -->
                    </div>
                    <!-- /.panel -->
                </div>
                <!-- /.col-lg-2 -->
                <div class="col-lg-7">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <i class="fa fa-sitemap fa-fw"></i> Vehicles Allocations in Stations
                            <div class="pull-right">
                                <div class="btn-group">
                                    <button type="button" class="btn btn-default btn-xs dropdown-toggle" data-toggle="dropdown">
                                        <span id="selected-station">Select station</span>
                                        <span class="caret"></span>
                                    </button>
                                    <ul id="select-station-chart" class="dropdown-menu pull-right" role="menu">
                                    
                                        <!-- Loop over and print the stations -->
                                        <c:forEach items="${stationsOptions}" var="entry">
                                            <li id="${entry.key}"><a href="#">${entry.value}</a></li>
                                        </c:forEach>
                                                                              
                                    </ul>
                                </div>
                            </div>
                        </div>
                        <!-- /.panel-heading -->
                        <div class="panel-body">
                            <div id="morris-bar-chart1"></div>
                        </div>
                        <!-- /.panel-body -->
                    </div>
                    <!-- /.panel -->
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <i class="fa fa-automobile fa-fw"></i> Vehicles Charge Levels over Time
                            <div class="pull-right">
                                <div class="btn-group">
                                    <button type="button" class="btn btn-default btn-xs dropdown-toggle" data-toggle="dropdown">
                                        <span id="selected-vehicle">Select vehicle</span>
                                        <span class="caret"></span>
                                    </button>
                                    <ul id="select-vehicle-chart"class="dropdown-menu pull-right" role="menu">
                                    
                                        <!-- Loop over and print the vehicles -->
                                        <c:forEach items="${vehiclesOptions}" var="entry">
                                            <li id="${entry.key}"><a href="#">${entry.value}</a></li>
                                        </c:forEach>
                                        
                                    </ul>
                                </div>
                            </div>
                        </div>
                        <!-- /.panel-heading -->
                        <div class="panel-body">
                            <div id="morris-bar-chart2"></div>
                        </div>
                        <!-- /.panel-body -->
                    </div>
                    <!-- /.panel -->
                </div>
                <!-- /.col-lg-7 -->
                <div class="col-lg-5">
                  <div class="panel panel-default">
                      <div class="panel-heading">
                          <i class="fa fa-map-marker fa-fw"></i> Vehicles Current Positions
                          <div class="pull-right">
                                <div class="btn-group">
                                    <button type="button" class="btn btn-default btn-xs dropdown-toggle" data-toggle="dropdown">
                                        <span id="selected-time">Select time</span>
                                        <span class="caret"></span>
                                    </button>
                                    <ul id="select-time-table"class="dropdown-menu pull-right" role="menu">
                                    
                                        <!-- Loop over and print the vehicles -->
                                        <c:forEach items="${reducedTimeOptions}" var="entry">
                                            <li id="${entry.key}"><a href="#">${entry.value}</a></li>
                                        </c:forEach>
                                        
                                    </ul>
                                </div>
                            </div>
                      </div>
                      <!-- /.panel-heading -->
                      <div class="panel-body">
                          <div class="table-responsive">
                              <table id="vehicles-table" class="table table-hover">
                                  <thead>
                                      <tr>
                                          <th>#</th>
                                          <th>Vehicle</th>
                                          <th>Station</th>
                                      </tr>
                                  </thead>
                                  <tbody>
                                  </tbody>
                              </table>
                          </div>
                          <!-- /.table-responsive -->
                      </div>
                      <!-- /.panel-body -->
                  </div>
                  <!-- /.panel -->
                </div>
                <!-- /.col-lg-5 -->
            </div>
            <!-- /.row -->
            
<!-- If the service and the simulation are not running, redirect to dashboard -->
<% } else { %>

<% response.sendRedirect("nodata"); %>
	
<% } %>
            
<%@include file="footer.jsp"%>