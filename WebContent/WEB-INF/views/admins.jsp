<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@include file="header.jsp"%>

<!-- If admin with role ADMIN has logged in, show manage admins page content -->
<% if (session.getAttribute("loggedInRole").equals("ADMIN")) { %>

        <div id="page-wrapper">
            <div class="row">
                <div class="col-lg-12">
                    <h1 class="page-header">Manage Admins</h1>
                </div>
                <!-- /.col-lg-12 -->
            </div>
            <!-- /.row -->
            <div class="row">
                <div class="col-lg-8">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <i class="fa fa-user fa-fw"></i> Admin Accounts Details
                        </div>
                        <!-- /.panel-heading -->
                        <div class="panel-body">
                            <div class="table-responsive">
                                <table class="table table-hover">
                                    <thead>
                                        <tr>
                                            <th>Full Name</th>
                                            <th>Username</th>
                                            <th>Password</th>
                                            <th>Role</th>
                                            <th>Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                    
                                    <!-- Loop over and print the administrators -->
									<c:forEach var="tempAdministrator" items="${administrators}">
									
										<!-- Construct an update link with administrator id -->
										<c:url var="updateLink" value="/admin/update-admin" >
											<c:param name="administratorId" value="${tempAdministrator.id}" />
										</c:url>
										
										<!-- Construct an delete link with administrator id -->
										<c:url var="deleteLink" value="/admin/delete-admin" >
											<c:param name="administratorId" value="${tempAdministrator.id}" />
										</c:url>
                                    
                                        <tr>
                                            <td>${tempAdministrator.name}</td>
                                            <td>${tempAdministrator.username}</td>
                                            <td>************</td>
                                            <td>${tempAdministrator.role}</td>
                                            <td>
                                            	<a href="${updateLink}" class="btn btn-primary btn-outline btn-warning btn-xs">Update</a>

												<!-- Button trigger modal -->
					                            <button class="btn btn-primary btn-outline btn-danger btn-xs" data-toggle="modal" data-target="#myModal-${tempAdministrator.id}">
					                                Delete
					                            </button>
					                            <!-- Modal -->
					                            <div class="modal fade" id="myModal-${tempAdministrator.id}" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
					                                <div class="modal-dialog">
					                                    <div class="modal-content">
					                                        <div class="modal-header">
					                                            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					                                            <h4 class="modal-title" id="myModalLabel">Warning!</h4>
					                                        </div>
					                                        <div class="modal-body">
					                                            Are you sure you want to delete this admin <strong>${tempAdministrator.name}</strong>?
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
                                  <i class="fa fa-user fa-5x"></i>
                              </div>
                              <div class="col-xs-9 text-right">
                                  <div class="huge">${countAdmins}</div>
                                  <div>Total Admins!</div>
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
                          <i class="fa fa-plus-circle fa-fw"></i> Add Admins
                      </div>
                      <div class="panel-body">
                          <p></p>
                          <form action="add-new-admin" method="get" id="form1"></form>
                          <button type="submit" form="form1" class="btn btn-outline btn-danger col-md-10">Add New Admin</button>
                          </br></br></br>
                      </div>
                  </div>
                  <!-- /.panel -->
                </div>
                <!-- /.col-lg-2 -->
                <div class="col-lg-2">
                  <div class="panel panel-default">
                      <div class="panel-heading">
                          <i class="fa fa-arrow-circle-left fa-fw"></i> Manage Admins
                      </div>
                      <div class="panel-body">
                          <p>Admins names, usernames and roles are shown and they can be created or updated.</p>
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
                          <p>This panel is protected and only visible to logged in admins, not moderators.</p>
                      </div>
                  </div>
                  <!-- /.panel -->
                </div>
                <!-- /.col-lg-2 -->
            </div>
            <!-- /.row -->
            
<!-- If admin with role ADMIN has not logged in, redirect to dashboard -->
<% } else { %>

<% response.sendRedirect("../home/main"); %>
	
<% } %>
            
        <%@include file="footer.jsp"%>