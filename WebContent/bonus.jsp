<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html>
<head>
<meta charset="ISO-8859-1">
<title>Bonus</title>
<link rel="stylesheet"
	href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css"
	integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T"
	crossorigin="anonymous">
</head>
<body>
	<nav class="navbar navbar-expand-lg navbar-light bg-light">
		<div class="navbar-nav">
			<a class="nav-item nav-link" href="index.jsp">Main Qn</a> 
			<a class="nav-item nav-link active" href="#">Bonus Qn <span class="sr-only">(current)</span></a> 
		</div>
	</nav>
	<div align="center">
		<br>
		<h1>Cheapest Time for Location</h1>
		<br> <br>

		<form action="<%=request.getContextPath()%>/query2" method="post">
			<label for="startLoc">Select Start Location: </label> <input
				type="number" id="startLoc" , name="startLoc" /> <label
				for="endLoc">Select End Location: </label> <input type="number"
				id="endLoc" , name="endLoc" /> <input type="submit" value="Submit" />
		</form>
		<br> <br>
		<h2>
			<c:if test="${requestScope.result!=null}">
	  			Cheapest Hour For travelling from Location ID ${startLoc} to ${endLoc} in NYC:		
		  	  	
		  	<c:choose>
					<c:when test="${result!=-1}">
						<div style="color: red">${result}:00HRS</div>
					</c:when>
					<c:otherwise>
						<div style="color: red">Data Not Found. Please use other
							input numbers</div>
					</c:otherwise>
				</c:choose>
			</c:if>

		</h2>

		<br>

	</div>

</body>
</html>