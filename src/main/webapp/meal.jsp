<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<h2>Meals</h2>
<table border="1" cellspacing="0" cellpadding="5">
    <thead>
    <tr>
        <th>Description</th>
        <th>Date</th>
        <th>Calories</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="m" items="${meals}">
        <tr style="background-color: ${m.exceed ? "red" : "green"}">
            <td>${m.description}</td>
            <td><javatime:format value="${m.dateTime}" style="L-"/></td>
            <td>${m.calories}</td>
        </tr>
    </c:forEach>
    </tbody>
</table>
</body>
</html>
