<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>


<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>MVC-Microservices Application: Home</title>
</head>
<body>
<jsp:include page="/WEB-INF/pages/common/header.jsp" flush="true" />

<form:form action="${pageContext.request.contextPath}/login" method="post" modelAttribute="user" id="loginForm">
	<p class="searchTitle">&nbsp;&nbsp;MVC-Microservices Application: Home</p>
	<p class="searchTitle">&nbsp;</p>
	<p class="searchTitle">&nbsp;&nbsp;Please click the links go to the modules.</p>
	<p class="searchTitle">
		<c:if test="${not empty error}">
		    <div class="error-msg" style="color:red;">
		        &nbsp;&nbsp;${error}
		    </div>
		</c:if>
		<c:if test="${not empty msg}">
		    <div style="color: green;"><b>&nbsp;&nbsp;${msg}</b></div>
		</c:if>
	</p>
	<!-- global errors -->
    <form:errors path="*" cssClass="error-msg" element="div"/>
	<table class="il-home-table">
		<tr>
			<td width="50%">
				<table class="il-home-table">
					<tr>
						<td width="100%" colspan="3" style="color: red;"><strong>Microservices</strong></td>
					</tr>
					<tr>
						<td width="10%" align="left">&nbsp;</td>
						<td width="55%" align="left"><b>Login Details</b>&nbsp;&nbsp;<b>(User Microservice)</b></td>
						<td width="35%" align="left">&nbsp;</td>							
					</tr>
					<tr>
						<td width="10%" align="left">&nbsp;</td>
						<td width="55%" align="left"><a href="${pageContext.request.contextPath}/manufacturer/showManufacturerDetails"><b>Manufacturer Details</b></a>&nbsp;&nbsp;<b>(Microservice)</b></td>
						<td width="35%" align="left">&nbsp;</td>		
					</tr>
					<tr>
						<td width="10%" align="left">&nbsp;</td>
						<td width="55%" align="left"><a href="${pageContext.request.contextPath}/product/showProductDetails"><b>Product Details</b></a>&nbsp;&nbsp;<b>(Microservice)</b></td>			
						<td width="35%" align="left">&nbsp;</td>		
					</tr>
					<tr>
						<td width="10%" align="left">&nbsp;</td>
						<td width="55%" align="left"><a href="${pageContext.request.contextPath}/orderqty/showOrderQtyDetails"><b>Order Details</b></a>&nbsp;&nbsp;<b>(Microservice)</b></td>			
						<td width="35%" align="left">&nbsp;</td>			
					</tr>	
					<tr>
						<td width="10%" align="left">&nbsp;</td>
						<td width="55%" align="left"><a href="${pageContext.request.contextPath}/orderDocument/showMongoDbDetails"><b>MongoDB Details</b></a><b>&nbsp;&nbsp;(Retrieved document(s) from the Mongo database)</b></td>			
						<td width="35%" align="left">&nbsp;</td>		
					</tr>	
				</table>
			</td>		
			<td width="50%">
				<table class="il-home-table">
					<tr>
						<td width="100%" align="left" style="color: red;"><strong>AI Agents</strong></td>
					</tr>
					<tr>
						<td width="100%" align="left">Designed and implemented an AI Agent system using Spring Boot microservices integrated with a Python-based AI API service. 
													The platform processes large PDF documents, performs GMP regulatory compliance analysis using Ollama models, and generates 
													automated audit reports through RESTful communication. Created regulatory compliance dashboard.
						</td>
					</tr>
					<tr>
						<td width="100%" align="left"><a href="${pageContext.request.contextPath}/aiAgent/showAiAgent"><b>Only Display Regulatory Compliance</b></a>&nbsp;&nbsp;<b>(AI Agent->Spring Boot->Microservice->Python AI API Service)</b></td>			
					</tr>
					<tr>
						<td width="100%" align="left"><a href="${pageContext.request.contextPath}/aiAgent/showAiAgentProcessBatch"><b>Save/Display Regulatory Compliance</b></a>&nbsp;&nbsp;<b>(AI Agent->Spring Boot->Microservice->Python AI API Service)</b></td>			
					</tr>
					<tr>
						<td width="100%" align="left"><a href="${pageContext.request.contextPath}/aiAgent/showAiAgentDashboard"><b>Regulatory Compliance Dashboard</b></a>&nbsp;&nbsp;<b>(Microservice)</b></td>			
					</tr>
				</table>
			</td>
		</tr>

	</table>
</form:form>
</body>
</html>


