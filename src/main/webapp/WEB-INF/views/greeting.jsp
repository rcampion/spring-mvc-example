<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page  isELIgnored="false" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
    
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
<meta charset="UTF-8">
<title>Greeting</title>
</head>
<body>
    <!-- 
    <p th:text="'Hello, ' + ${name} + '!'" /> 
    -->
    <c:out value="Hello, ${name}"/>
</body>
</html>