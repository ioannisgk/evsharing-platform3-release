<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@include file="header.jsp"%>

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
                            <i class="fa fa-bar-chart-o fa-fw"></i> Requests Accepted vs Requests Denied over Time
                        </div>
                        <!-- /.panel-heading -->
                        <div class="panel-body">
                            <div style="height: 200px; background-color: #F5F5DC;">No data, start service or simulation first</div>
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
                            <div style="height: 200px; background-color: #F5F5DC;">No data</div>
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
                        </div>
                        <!-- /.panel-heading -->
                        <div class="panel-body">
                            <div style="height: 200px; background-color: #F5F5DC;">No data, start service or simulation first</div>
                        </div>
                        <!-- /.panel-body -->
                    </div>
                    <!-- /.panel -->
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <i class="fa fa-automobile fa-fw"></i> Vehicles Charge Levels over Time
                        </div>
                        <!-- /.panel-heading -->
                        <div class="panel-body">
                            <div style="height: 200px; background-color: #F5F5DC;">No data, start service or simulation first</div>
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
                      </div>
                      <!-- /.panel-heading -->
                      <div class="panel-body">
                          <div style="height: 400px; background-color: #F5F5DC;">No data, start service or simulation first</div>
                      </div>
                      <!-- /.panel-body -->
                  </div>
                  <!-- /.panel -->
                </div>
                <!-- /.col-lg-5 -->
            </div>
            <!-- /.row -->
            
<%@include file="footer.jsp"%>