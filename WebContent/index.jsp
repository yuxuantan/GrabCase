<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html>
<head>
<meta charset="ISO-8859-1">
<title>Yellow Cab Data</title>
<link rel="stylesheet"
	href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css"
	integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T"
	crossorigin="anonymous">
</head>
<body>
	<nav class="navbar navbar-expand-lg navbar-light bg-light">
		<div class="navbar-nav">
			<a class="nav-item nav-link active" href="#"> <span class="sr-only">(current)</span> Main Qn</a> 
			<a class="nav-item nav-link" href="bonus.jsp">Bonus Qn </a> 
		</div>
	</nav>
	<div align="center">
		<br>
		<h1>Hourly Trip Count</h1>
		<br> <br>

		<form action="<%=request.getContextPath()%>/query" method="post">
			<label for="qDate">Select date: </label> <input type="date"
				id="qDate" name="qDate" value="${requestScope.date }"> <input
				type="submit" value="Submit" />
		</form>
		<br>
		<c:if test="${requestScope.date!=null}">
			<table class="table table-striped col-md-2 text-center">
				<thead>
					<tr>
						<th>Time (24hr format)</th>
						<th>Trip Count</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="c" items="${result}" varStatus="status">
						<tr>
							<td><c:out value="${status.count-1}:00 - ${status.count}:00" />
							</td>
							<td><c:out value="${c}" /></td>
						</tr>
					</c:forEach>
				</tbody>

			</table>
		</c:if>
	</div>

</body>
</html>