<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@include file="header.jsp"%>

<!-- If admin with role ADMIN has logged in, show manage admins page content -->
<% if (session.getAttribute("loggedInRole").equals("ADMIN")) { %>

<c:choose>
    <c:when test="${empty param.administratorId}" >
    <c:set var = "description" value = "Add New Admin"/>
    <c:set var = "readonly" value = "false"/>
    </c:when>
    <c:otherwise>
    <c:set var = "description" value = "Update Admin"/>
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
                            <i class="fa fa-user fa-fw"></i> Admin Account Details
                        </div>
                        <!-- /.panel-heading -->
                        <div class="panel-body">
                            <div class="table-responsive">
                            
                            <form:form action="save-admin" modelAttribute="administrator" method="POST">
		
							<!-- Associate data of current form with administrator id -->
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
                                            <td>Full Name</td>
                                            <td>
                                              <div class="form-group">
                                              	  <form:input path="name" class="form-control" style="width:50%;" placeholder="Full Name" />
                                              </div>
                                            </td>
                                            <td>
                                            	<p class="text-muted">Enter admin full name</p>
												<form:errors path="name" class="text-muted" />
											</td>
                                        </tr>
                                        <tr>
                                          <td>2</td>
                                          <td>Username</td>
                                          <td>
                                            <div class="form-group">
                                                  <form:input path="username" class="form-control" style="width:50%;" placeholder="Username" readonly="${readonly}"/>
                                            </div>
                                          </td>
                                          <td>
                                          		<p class="text-muted">Enter admin username</p>
												<form:errors path="username" class="text-muted" />
										  </td>
                                        </tr>
                                        <tr>
                                          <td>3</td>
                                          <td>Password</td>
                                          <td>
                                            <div class="form-group">	
                                                <form:input path="password" class="form-control" style="width:50%;" placeholder="Password" readonly="${readonly}" type="password"/> 
                                            </div>
                                          </td>
                                          <td>
                                          		<p class="text-muted">Admin password</p>
												<form:errors path="password" class="text-muted" />
										  </td>
                                        </tr>
                                        <tr>
                                          <td>4</td>
                                          <td>Role</td>
                                          <td>
                                            <fieldset>
                                                <div class="form-group">
                                                    
                                                    <form:select path="role" class="form-control" style="width:50%;">
														<form:option value="MODERATOR" label="Moderator" />
														<form:option value="ADMIN" label="Admin" />
													</form:select>      
                                                    
                                                </div>
                                            </fieldset>
                                          </td>
                                          <td><p class="text-muted">Select admin role</p></td>
                                        </tr>
                                        <tr>
                                              <td></td>
                                              <td></td>
                                              <td><input type="submit" value="${description}" class="btn btn-outline btn-danger" /></td>
                                              <td><p class="text-muted">Click to save the admin ${errorMessage}</p></td>
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
                          <i class="fa fa-chevron-circle-left fa-fw"></i> Back to Admins
                      </div>
                      <div class="panel-body">
                          <p></p>
                          <form action="list" method="get" id="form1"></form>
                          <button type="submit" form="form1" class="btn btn-outline btn-danger col-md-10">Back to Admins</button>
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
                          <p>Administrator full name, username and role can be created or updated.</p>
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
                          <p>The administrator can create a new admin and it will be saved to the database.</p>
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