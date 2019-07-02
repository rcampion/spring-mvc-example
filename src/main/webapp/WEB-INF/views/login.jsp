<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%> <%@taglib
uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page  isELIgnored="false" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Spring Login Form</title>
</head>
<body>
<form:form method="POST" action="login" modelAttribute="user">
<div align="center">
<table>
<tr>
<td>User Name</td>
<td><form:input type="text" name="userName" path="userName" /></td>
</tr>
<tr>
<td>Password</td>
<td><form:input type="password" name="password" path="password" /></td>
</tr>
<tr>
<td></td>
<td><input type="submit" value="Submit" /></td>
</tr>
</table>
<div style="color: red">${error}</div>

</div>
</form:form>
</body>
</html>