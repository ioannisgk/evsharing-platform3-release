<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>Electric Vehicle Sharing Platform</title>

    <!-- Bootstrap Core CSS -->
    <link href="${pageContext.request.contextPath}/resources/vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">

    <!-- MetisMenu CSS -->
    <link href="${pageContext.request.contextPath}/resources/vendor/metisMenu/metisMenu.min.css" rel="stylesheet">

    <!-- Custom CSS -->
    <link href="${pageContext.request.contextPath}/resources/dist/css/sb-admin-2.css" rel="stylesheet">

    <!-- Custom Fonts -->
    <link href="${pageContext.request.contextPath}/resources/vendor/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->

</head>

<body>

<!-- If admin has not logged in, show login form -->
<% if (session.getAttribute("loggedInUsername") == null) { %>

    <div class="container">
        <div class="row">
            <div class="col-md-4 col-md-offset-4">
                <div class="login-panel panel panel-primary">
                    <div class="panel-heading">
                        <h3 class="panel-title">Please Sign In</h3>
                    </div>
                    <div class="panel-body">
                        <div align="center">
                          <a href="#">
                            <img border="0" alt="EV Sharing Platform v1.0" src="${pageContext.request.contextPath}/resources/images/logo.png" width="152" height="152">
                          </a>
                        </div>
                        </br>
                        
                        <!-- Spring Security requires action="login" -->
						<form:form action="login-page" modelAttribute="administrator" method="POST" role="form">
						
						<!-- Associate data of current form with administrator id -->
						<form:hidden path="id" />
						
                            <fieldset>
                                <div class="form-group">
                                	<form:input path="username" class="form-control" placeholder="E-mail" name="email" type="email" autofocus="true" />
                                    <form:errors path="username" cssClass="error" />
                                </div>
                                <div class="form-group">
                                	<form:input path="password" class="form-control" placeholder="Password" name="password" type="password" value="" />
                                    <form:errors path="password" cssClass="error" />
                                </div>
                                <input type="submit" value="Secure Login" class="btn btn-outline btn-primary btn-block btn-lg" />
                            </fieldset>
                            
                        </form:form>
                        
                        <!-- Show error messages if login details are invalid -->
						</br>
						<p><c:out value="${param['fail']}"></c:out><p>
						
						<p>${logoutMessage}<p>
						
                    </div>
                </div>
            </div>
        </div>
    </div>
    
<!-- If admin has already logged in, redirect to dashboard -->
<% } else { %>

<% response.sendRedirect("main"); %>
	
<% } %>

    <!-- jQuery -->
    <script src="${pageContext.request.contextPath}/resources/vendor/jquery/jquery.min.js"></script>

    <!-- Bootstrap Core JavaScript -->
    <script src="${pageContext.request.contextPath}/resources/vendor/bootstrap/js/bootstrap.min.js"></script>

    <!-- Metis Menu Plugin JavaScript -->
    <script src="${pageContext.request.contextPath}/resources/vendor/metisMenu/metisMenu.min.js"></script>

    <!-- Custom Theme JavaScript -->
    <script src="${pageContext.request.contextPath}/resources/dist/js/sb-admin-2.js"></script>

</body>

</html>
