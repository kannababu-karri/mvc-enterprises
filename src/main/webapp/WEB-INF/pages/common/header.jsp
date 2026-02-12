<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.mvc.enterprises.entities.User" %>
<%@ page import="com.mvc.enterprises.utils.Utils" %>
<%@ page import="com.mvc.enterprises.utils.StringUtility" %>
<%		
	String loginUser = Utils.getLoginUserName(request);
%>
<link type="text/css" rel="stylesheet" href="<c:url value='/styles/main.css'/>" />
<script type="text/javascript" src="<c:url value='/scripts/il-web.js'/>"></script>
<table class="headerMain" >
	<tr>
		<td width="100%" colspan="3"><hr /></td>
	</tr>
	<tr class="headerMainTr">
		<!-- <td width="25%"><img src="<c:url value='/images/il.gif'/>" alt="innovareLabs" height="100"></td> -->
		<td width="25%" align="left" class="headerMainTdOneEm" height="30%">Kannababu Karri&nbsp;&nbsp;Version 1.0</td>
		<td width="50%" align="center" class="headerMainTd" height="30%">MVC-Microservices Application</td>
		<td width="25%" align="right" class="headerMainTdOneEm" height="30%">
			<table class="headerMain" >
				<tr>
					<td width="70%" align="center">
						<%
							if(!StringUtility.isEmpty(loginUser)) {
						%>
								<b>User:</b> <%= loginUser %>&nbsp;&nbsp;<b>Role:</b> <%= Utils.getUserRole(request) %>
						<%
							}
						%>
					</td>
					<td width="30%" align="center">
						<%
							if(!StringUtility.isEmpty(loginUser)) {
						%>
								<a href="<c:url value='/logout' />">Logout</a>
						<%
							}
						%>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td width="100%" colspan="3"><hr /></td>
	</tr>	
</table>