<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>MVC-Microservices Application: MongoDB</title>
</head>
<body>
<jsp:include page="/WEB-INF/pages/common/header.jsp" flush="true" />

<form:form action="${pageContext.request.contextPath}/orderDocument/orderDocumentSearch" method="post" modelAttribute="orderDocumentForm.orderDocument" id="orderDocumentForm">
	<p class="searchTitle">&nbsp;&nbsp;MongoDB Interface: Home</p>
		<p class="searchTitle">
		<c:if test="${not empty error}">
		    <ul style="color: red;">
		        <c:forEach items="${error}" var="err">
		            <li>${err}</li>
		        </c:forEach>
		    </ul>
		</c:if>
		<c:if test="${not empty msg}">
		    <div style="color: green;"><b>&nbsp;&nbsp;${msg}</b></div>
		</c:if>
	</p>
	<p>&nbsp;&nbsp;
		<input type="button" value="Return Home" onclick="javascript:jsOrderDocumentSubmit('${pageContext.request.contextPath}/orderDocument/returnILHome');" />
	</p>
	
	<!-- global errors -->
    <form:errors path="*" cssClass="error-msg" element="div"/>
	<table width="100%">			
		<tr>
			<td width="1%">&nbsp;</td>			
			<td width="20%" align="right"><b>Manufacturer Name:</b></td>
			<td width="30%" align="left">
				<select name="orderDocument.manufacturerId" id="orderDocument.manufacturerId">
			        <option value="">Choose one...</option>
			        <c:forEach items="${orderDocumentForm.manufacturers}" var="manufacturer" varStatus="status">
			            <option value="${manufacturer.manufacturerId}" <c:if test="${manufacturer.manufacturerId == orderDocumentForm.orderDocument.manufacturerId}">selected</c:if>>${manufacturer.mfgName}</option>
			        </c:forEach>
	    		</select>
			</td>
			<td width="49%">&nbsp;</td>
		</tr>
		<tr>
			<td width="1%">&nbsp;</td>			
			<td width="20%" align="right"><b>Product Name:</b></td>
			<td width="30%" align="left">
				<select name="orderDocument.productId" id="orderDocument.productId">
			        <option value="">Choose one...</option>
			        <c:forEach items="${orderDocumentForm.products}" var="product" varStatus="status">
			            <option value="${product.productId}" <c:if test="${product.productId == orderDocumentForm.orderDocument.productId}">selected</c:if>>${product.productName}</option>
			        </c:forEach>
	    		</select>
			</td>
			<td width="49%">&nbsp;</td>
		</tr>
		<tr>
			<td width="100%" colspan="4">&nbsp;</td>
		</tr>		
		<tr>
			<td width="1%">&nbsp;</td>			
			<td width="50%" align="center" colspan="2">
				<input type="submit" value="Submit" />&nbsp;&nbsp;<input type="button" value="Cancel" onclick="javascript:jsOrderDocumentSubmit('${pageContext.request.contextPath}/orderDocument/returnILHome');" />
			</td>
			<td width="49%">&nbsp;</td>
		</tr>				
		<tr>
			<td width="100%" colspan="4"><hr /></td>
		</tr>	
	</table>
	<!-- Display search results -->
	<c:if test="${orderDocumentForm.showDetails}">
		<p class="searchTitle">&nbsp;&nbsp;MongoDB Order Search Results:</p>
		<table class="searchDetail">			
			<tr>
				<th>Type</th>
				<th>Order id</th>
				<th>Mfg id</th>
				<th>Mfg name</th>	
				<th>Product id</th>				
				<th>Product name</th>				
				<th>Product description</th>
				<th>Cas Number</th>
				<th>Quantity</th>
				<th>Created date</th>								
			</tr>
			<c:forEach items="${orderDocumentForm.resultOrderDocuments}" var="orderDocument" varStatus="status">		
				<tr class="detailData">		
					<td class="leftNoBold">${orderDocument.documentType}</td>	
					<td class="leftNoBold">${orderDocument.orderQtyId}</td>					
					<td class="leftNoBold">${orderDocument.manufacturerId}</td>
					<td class="leftNoBold">${orderDocument.mfgName}</td>
					<td class="leftNoBold">${orderDocument.productId}</td>
					<td class="leftNoBold">${orderDocument.productName}</td>
					<td class="leftNoBold">${orderDocument.productDescription}</td>
					<td class="leftNoBold">${orderDocument.casNumber}</td>
					<td class="leftNoBold">${orderDocument.quantity}</td>
					<td class="leftNoBold">${orderDocument.createdAt}</td>
				</tr>																
			</c:forEach>								
		</table>
		<c:if test="${orderDocumentForm.pageResponseDto.totalPages > 1}">
		    <div style="text-align:center;margin-top:20px;">
		        <!-- Previous -->
		        <c:if test="${orderDocumentForm.pageResponseDto.pageNumber > 0}">
		            <a href="?page=${orderDocumentForm.pageResponseDto.pageNumber - 1}&orderDocument.manufacturerId=${orderDocumentForm.orderDocument.manufacturerId}&orderDocument.productId=${orderDocumentForm.orderDocument.productId}">Previous</a>
		        </c:if>		
		        <!-- Page Numbers -->
		        <c:forEach begin="0" end="${orderDocumentForm.pageResponseDto.totalPages-1}" var="i">
		            <c:choose>
		                <c:when test="${i == orderDocumentForm.pageResponseDto.pageNumber}">
		                    <b>[${i+1}]</b>
		                </c:when>
		                <c:otherwise>
		                    <a href="?page=${i}&orderDocument.manufacturerId=${orderDocumentForm.orderDocument.manufacturerId}&orderDocument.productId=${orderDocumentForm.orderDocument.productId}">${i+1}</a>
		                </c:otherwise>
		            </c:choose>
		        </c:forEach>		
		        <!-- Next -->
		        <c:if test="${orderDocumentForm.pageResponseDto.pageNumber < orderDocumentForm.pageResponseDto.totalPages-1}">
		            <a href="?page=${orderDocumentForm.pageResponseDto.pageNumber + 1}&orderDocument.manufacturerId=${orderDocumentForm.orderDocument.manufacturerId}&orderDocument.productId=${orderDocumentForm.orderDocument.productId}">Next</a>
		        </c:if>	
		    </div>
		</c:if>	
	</c:if>
	<input type="hidden" name="orderId" />
</form:form>
</body>
</html>


