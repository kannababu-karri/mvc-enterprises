<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>


<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>MVC-Microservices Application: Login</title>
</head>
<body>
<jsp:include page="/WEB-INF/pages/common/header.jsp" flush="true" />

<form:form action="/mvc/login" method="post" modelAttribute="user" id="loginForm">
	<p class="searchTitle">&nbsp;&nbsp;MVC-Microservices Application: Login</p>
	<p class="searchTitle">&nbsp;&nbsp;Please enter login credentials.</p>
	<p class="searchTitle">
		<c:if test="${not empty error}">
		    <div class="error-msg" style="color:red;">
		        &nbsp;&nbsp;${error}
		    </div>
		</c:if>
	</p>
	
	<!-- global errors -->
    <form:errors path="*" cssClass="error-msg" element="div"/>
		<table class="login-table">			
			<tr>			
				<td width="25%" align="right"><b>User Id:</b>&nbsp;&nbsp;</td>
				<td width="75%" align="left"><form:input path="userName" /></td>
			</tr>
			<tr>
				<td width="100%" colspan="2">&nbsp;</td>
			</tr>		
			<tr>
				<td width="25%" align="right"><b>Password:</b>&nbsp;&nbsp;</td>
				<td width="75%" align="left"><form:password path="password" /></td>			
			</tr>
			<tr>
				<td width="100%" colspan="2">&nbsp;</td>
			</tr>
			<tr>					
				<td width="25%" align="right">&nbsp;&nbsp;</td>
				<td width="75%" align="left"><input type="submit" value="Submit" />&nbsp;&nbsp;<input type="button" value="Reset" onclick="javascript:jsResetHome('${pageContext.request.contextPath}/login/reset');" /></td>
			</tr>		
		</table>		
</form:form>
		<ul style="color: black;">
	        <li><b>Project: MVC Enterprise Microservices Application</b></li>
	        &nbsp;
	        <li><b>This is a personal project developed to demonstrate MVC (Model–View–Controller) architecture integrated with microservices-based design.</b></li>
		      	<ul>
					<li>The application communicates with four independent microservices:</li>
					<li>User Service – Manages user accounts and authentication.</li>			
					<li>Product Service – Handles product information and catalog.</li>			
					<li>Manufacturer Service – Maintains manufacturer details.</li>			
					<li>Order Quantity Service – Manages orders and quantities.</li>
					&nbsp;			
				</ul>
			<li><b>Technologies:</b></li>
				<ul>
					<li>Java / Spring MVC, Spring Boot, Junit, Mokito unit tests, SonarQube, Jacoco, Actuator, Grafana, jMeter, VisualVM</li>
					<li>AWS EC2, ECR, ECS, EKS, Load balancer, Lambda.</li>			
					<li>MySQL, MongoDB, Redis Caching</li>	
					<li>MySQL, MongoDB</li>			
					<li>Apache Kafka not deployed due to budget concern. Need to pay more money for EC2 instance. Project was implement and moved into github repository as enterprise-products.git.</li>			
					<li>Implemented CI/CD pipe lines using Maven, Jenkins, Docker, HelmCharts, ArgoCd, K8S (Installed minikube).</li>			
					<li>Use the Eclipse and VS Code IDE. Integrated GitHub Copilot.</li>
					<li>Implemented JWT token for authentication and authorization. JWT token to communicate between the microservices. Like MVC to microservice and microservice to microservice.</li>
					<li>Implemented server-side pagination for the manufacturer and product search feature. Integrated query parameters (page, size, sort) with microservice REST API calls.</li>
					&nbsp;
				</ul>
			<li><b>User Roles:</b><br />The system supports three main roles:</li>
				<ul>
					<li>Admin – Full access to manage users, products, manufacturers and orders.</li>
					<li>User – Can browse products, manufacturers and place orders.</li>			
					<li>View – Read-only access for viewing data.</li>	
				</ul>
	    </ul>

</body>
</html>


