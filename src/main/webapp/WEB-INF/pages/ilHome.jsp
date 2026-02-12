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
			<td width="100%" colspan="8">&nbsp;</td>
		</tr>
		<tr>
			<td width="20%" align="left">&nbsp;</td>
			<td width="45%" align="left"><b>Login Details</b>&nbsp;&nbsp;<b>(User Microservice)</b></td>
			<td width="35%" align="left">&nbsp;</td>		
				
		</tr>
		<tr>
			<td width="20%" align="left">&nbsp;</td>
			<td width="45%" align="left"><a href="${pageContext.request.contextPath}/manufacturer/showManufacturerDetails"><b>Manufacturer Details</b></a>&nbsp;&nbsp;<b>(Microservice)</b></td>
			<td width="35%" align="left">&nbsp;</td>		
				
		</tr>
		<tr>
			<td width="20%" align="left">&nbsp;</td>
			<td width="45%" align="left"><a href="${pageContext.request.contextPath}/product/showProductDetails"><b>Product Details</b></a>&nbsp;&nbsp;<b>(Microservice)</b></td>			
			<td width="35%" align="left">&nbsp;</td>		
		</tr>
		<tr>
			<td width="20%" align="left">&nbsp;</td>
			<td width="45%" align="left"><a href="${pageContext.request.contextPath}/orderqty/showOrderQtyDetails"><b>Order Details</b></a>&nbsp;&nbsp;<b>(Microservice)</b></td>			
			<td width="35%" align="left">&nbsp;</td>			
		</tr>	
		<tr>
			<td width="20%" align="left">&nbsp;</td>
			<td width="45%" align="left"><a href="${pageContext.request.contextPath}/orderDocument/showMongoDbDetails"><b>MongoDB Details</b></a><b>&nbsp;&nbsp;(Retrieved document(s) from the Mongo database)</b></td>			
			<td width="35%" align="left">&nbsp;</td>		
		</tr>			
		<tr>
			<td width="100%" colspan="3">&nbsp;</td>
		</tr>
	</table>
</form:form>
</body>
</html>


