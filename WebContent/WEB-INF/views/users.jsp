<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>

<%@include file="header.jsp"%>

        <div id="page-wrapper">
            <div class="row">
                <div class="col-lg-12">
                    <h1 class="page-header">Manage Users</h1>
                </div>
                <!-- /.col-lg-12 -->
            </div>
            <!-- /.row -->
            <div class="row">
                <div class="col-lg-8">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <i class="fa fa-users fa-fw"></i> User Accounts Details
                        </div>
                        <!-- /.panel-heading -->
                        <div class="panel-body">
                            <div class="table-responsive">
                                <table class="table table-hover">
                                    <thead>
                                        <tr>
                                            <th>Full Name</th>
                                            <th>Username</th>
                                            <th>Gender</th>
                                            <th>Date of Birth</th>
                                            <th>Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                    
                                    <!-- Loop over and print the users -->
									<c:forEach var="tempUser" items="${users}">
									
										<!-- Construct an update link with user id -->
										<c:url var="updateLink" value="/user/update-user" >
											<c:param name="userId" value="${tempUser.id}" />
										</c:url>
										
										<!-- Construct an delete link with user id -->
										<c:url var="deleteLink" value="/user/delete-user" >
											<c:param name="userId" value="${tempUser.id}" />
										</c:url>

                                        <tr>
                                            <td>${tempUser.name}</td>
                                            <td>${tempUser.username}</td>
                                            <td>${tempUser.gender}</td>
                                            <td>
                                            	<fmt:formatDate value="${tempUser.dob}" pattern="dd/MM/yyyy" />
											</td>
                                            <td>
                                            	<a href="${updateLink}" class="btn btn-primary btn-outline btn-warning btn-xs">Update</a>

												<!-- Button trigger modal -->
					                            <button class="btn btn-primary btn-outline btn-danger btn-xs" data-toggle="modal" data-target="#myModal-${tempUser.id}-${tempUser.used}">
					                                Delete
					                            </button>
					                            <!-- Modal -->
					                            <div class="modal fade" id="myModal-${tempUser.id}-false" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
					                                <div class="modal-dialog">
					                                    <div class="modal-content">
					                                        <div class="modal-header">
					                                            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					                                            <h4 class="modal-title" id="myModalLabel">Warning!</h4>
					                                        </div>
					                                        <div class="modal-body">
					                                            Are you sure you want to delete this user <strong>${tempUser.name}</strong>?
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
					                            <div class="modal fade" id="myModal-${tempUser.id}-true" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
					                                <div class="modal-dialog">
					                                    <div class="modal-content">
					                                        <div class="modal-header">
					                                            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					                                            <h4 class="modal-title" id="myModalLabel">Warning!</h4>
					                                        </div>
					                                        <div class="modal-body">
					                                            You can not delete this user <strong>${tempUser.name}</strong>.
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
                          <i class="fa fa-plus-circle fa-fw"></i> Add Users
                      </div>
                      <div class="panel-body">
                        <p></p>
                          <form action="add-new-user" method="get" id="form1"></form>
                          <button type="submit" form="form1" class="btn btn-outline btn-primary col-md-10">Add New User</button>
                          </br></br></br>
                      </div>
                  </div>
                  <!-- /.panel -->
                </div>
                <!-- /.col-lg-2 -->
                <div class="col-lg-2">
                  <div class="panel panel-default">
                      <div class="panel-heading">
                          <i class="fa fa-arrow-circle-left fa-fw"></i> Manage Users
                      </div>
                      <div class="panel-body">
                          <p>Users usernames, gender and dates of birth are shown and they can be created or updated.</p>
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
                          <p>This panel is protected and visible to all logged in admins and moderators.</p>
                      </div>
                  </div>
                  <!-- /.panel -->
                </div>
                <!-- /.col-lg-2 -->
            </div>
            <!-- /.row -->
            
        <%@include file="footer.jsp"%>