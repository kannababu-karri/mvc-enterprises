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

<form:form action="${pageContext.request.contextPath}/aiAgent/aiAgentProcessBatch" method="post"  id="aiAgentForm" enctype="multipart/form-data">
	<p class="searchTitle">&nbsp;&nbsp;AI Agent Interface: Home</p>
	<p class="searchTitle">&nbsp;&nbsp;Regulatory compliance details are saved into database and results are displayed in the page.</p>

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
    
 	<table style="width:100%; padding: 10px;">
	     <tr>
            <td width="1%">&nbsp;</td>
            <td width="20%" align="right"><b>Batch No:</b></td>
            <td width="30%" align="left">
            	<input type="text" name="batchNo" required />
            </td>
            <td width="49%">&nbsp;</td>
        </tr>
        <tr>
            <td width="1%">&nbsp;</td>
            <td width="20%" align="right"><b>Product Name:</b></td>
            <td width="30%" align="left">
                <input type="text" name="productName" required />
            </td>
            <td width="49%">&nbsp;</td>
        </tr>
        <tr>
            <td width="1%">&nbsp;</td>
            <td width="20%" align="right"><b>Upload File:</b></td>
            <td width="30%" align="left">
                <input type="file" name="file" accept="application/pdf" required />
            </td>
            <td width="49%">&nbsp;</td>
        </tr>

        <tr>
            <td colspan="4" align="center" style="padding-top:10px;">
                <input type="submit" value="Submit" />
                <input type="button" value="Return Home" onclick="javascript:jsAiAgentSubmit('${pageContext.request.contextPath}/aiAgent/returnILHome');" />
            </td>
        </tr>
    </table>
	<!-- Display search results -->
	<c:if test="${not empty aiResult}">
		<p class="searchTitle">&nbsp;&nbsp;AI regulatory GMP audit compliance report:</p>
	    &nbsp;&nbsp;
	    <div class="ai-agent-report-box">
	        ${aiResult}
	    </div>
	</c:if>
</form:form>
</body>
</html>


