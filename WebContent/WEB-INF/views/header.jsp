<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="Electric Vehicle Sharing Platform">
    <meta name="author" content="Ioannis Gkourtzounis">

    <title>Electric Vehicle Sharing Platform</title>

    <!-- Bootstrap Core CSS -->
    <link href="${pageContext.request.contextPath}/resources/vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">

    <!-- MetisMenu CSS -->
    <link href="${pageContext.request.contextPath}/resources/vendor/metisMenu/metisMenu.min.css" rel="stylesheet">

    <!-- Custom CSS -->
    <link href="${pageContext.request.contextPath}/resources/dist/css/sb-admin-2.css" rel="stylesheet">

    <!-- Morris Charts CSS -->
    <link href="${pageContext.request.contextPath}/resources/vendor/morrisjs/morris.css" rel="stylesheet">

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

    <div id="wrapper">

        <!-- Navigation -->
        <nav class="navbar navbar-default navbar-static-top" role="navigation" style="margin-bottom: 0">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="dashboard.html">EV Sharing Platform v1.0</a>
            </div>

            <p class="pull-right text-muted" style="margin-top: 15px; margin-right: 30px;"><em>Welcome back, ${loggedInUsername}, ${loggedInRole}</em></p>

            <!-- /.navbar-top-links -->
            <div class="navbar-default sidebar" role="navigation">
              <div class="sidebar-nav navbar-collapse">
                  <ul class="nav" id="side-menu">
                      <li>
                        <div align="center">
                        <a href="#">
                          <img border="0" alt="EV Sharing Platform v1.0" src="${pageContext.request.contextPath}/resources/images/logo.png" width="152" height="152">
                        </a>
                        </div>
                      </li>
                      <li>
                          <a href="${pageContext.request.contextPath}/home/main"><i class="fa fa-dashboard fa-fw"></i> Dashboard</a>
                      </li>
                      <li>
                          <a href="${pageContext.request.contextPath}/charts/list"><i class="fa fa-bar-chart-o fa-fw"></i> Live Charts</span></a>
                      </li>
                      
                      <!-- If admin with role ADMIN has logged in, show manage admins page navigation link -->
					  <% if (session.getAttribute("loggedInRole").equals("ADMIN")) { %>

	                      <li>
	                          <a href="${pageContext.request.contextPath}/admin/list"><i class="fa fa-user fa-fw"></i> Admins</a>
	                      </li>
                      
                      <% } %>
                      
                      <li>
                          <a href="${pageContext.request.contextPath}/user/list"><i class="fa fa-users fa-fw"></i> Users</a>
                      </li>
                      <li>
                          <a href="${pageContext.request.contextPath}/station/list"><i class="fa fa-sitemap fa-fw"></i> Stations</a>
                      </li>
                      <li>
                          <a href="${pageContext.request.contextPath}/vehicle/list"><i class="fa fa-automobile fa-fw"></i> Vehicles</a>
                      </li>
                      <li>
                          <a href="${pageContext.request.contextPath}/simulation/list"><i class="fa fa-road fa-fw"></i> Simulation</a>
                      </li>
                      <li>
                          <a href="${pageContext.request.contextPath}/home/logout"><i class="fa fa-sign-out fa-fw"></i> Logout</a>
                      </li>

                  </ul>
              </div>
                <!-- /.sidebar-collapse -->
            </div>
            <!-- /.navbar-static-side -->
        </nav>

