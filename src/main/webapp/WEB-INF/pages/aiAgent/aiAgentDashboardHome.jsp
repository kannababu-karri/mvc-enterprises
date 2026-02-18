<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page import="com.mvc.enterprises.utils.Utils" %>

<%		
	String userRole = Utils.getUserRole(request);
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>MVC-Microservices Application: AI Agent</title> 
</head>
<body>
<jsp:include page="/WEB-INF/pages/common/header.jsp" flush="true" />

<form:form action="${pageContext.request.contextPath}/aiAgent/showAiAgentProcessBatch" method="post"  id="aiAgentForm" enctype="multipart/form-data">
	<p class="searchTitle">&nbsp;&nbsp;AI Agent Interface: Home</p>
	<p class="searchTitle">&nbsp;&nbsp;AI Regulatory compliance dashboard.</p>

	<!-- Display errors -->
    <c:if test="${not empty error}">
        <ul style="color: red;">
            <c:forEach items="${error}" var="err">
                <li>${err}</li>
            </c:forEach>
        </ul>
    </c:if>

    <!-- Display success message -->
    <c:if test="${not empty msg}">
        <div style="color: green;"><b>&nbsp;&nbsp;${msg}</b></div>
    </c:if>
	<p class="searchTitle">&nbsp;&nbsp;<input type="button" value="Return Home" onclick="javascript:jsAiAgentSubmit('${pageContext.request.contextPath}/aiAgent/returnILHome');" /></p>
	<table class="searchDetail">			
		<tr>
			<th>Batch No</th>
            <th>Product</th>
            <th>Uploaded By</th>
            <th>Status</th>
            <th>Score</th>
            <th>Risk</th>
            <th>Findings</th>
            <th>Reviewed At</th>						
		</tr>
		<c:choose>
			<c:when test="${not empty batches}">
				<c:forEach var="batch" items="${batches}">
					<c:set var="compliance" value="${complianceMap[batch.id]}" />
		       		<tr class="detailData">
				        <td class="leftNoBold">${batch.batchNo}</td>
				        <td class="leftNoBold">${batch.productName}</td>
				        <td class="leftNoBold">${batch.uploadedBy}</td>
				        <td class="leftNoBold">${batch.status}</td>
						<c:choose>
							<c:when test="${not empty compliance}">
								<td class="center">${compliance.score}</td>
								<td class="center">
									${compliance.riskLevel}
								</td>
								<td class="leftNoBold">
							    	<pre style="white-space: pre-wrap;">
										${compliance.findings}
							     	</pre>
								</td>
								<td  class="leftNoBold">${compliance.reviewedAt}</td>
							</c:when>
							<c:otherwise>
								<td class="center" colspan="4">
						    		Not Processed
								</td>
							</c:otherwise>
						</c:choose>
		        	</tr>
		       	</c:forEach>
		 	</c:when>
		 	<c:otherwise>
        		<tr class="detailData">
        			<td class="center" colspan="8">No records are existing in the system.</td>
        		</tr>
    		</c:otherwise>
	     </c:choose>
       </table>
 </form:form>
</body>
</html>