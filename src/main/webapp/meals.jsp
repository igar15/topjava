<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="javatime" uri="http://sargue.net/jsptags/time" %>

<html>
    <head>
        <title>Meals</title>
    </head>
    <body>
        <h3><a href="index.html">Home</a></h3>
        <hr>
        <h2>Meals</h2>
        <table border="" cellpadding="5">
            <thead>
                <th>DateTime</th>
                <th>Description</th>
                <th>Calories</th>
            </thead>
            <tbody align="center">
                <c:forEach var="mealTo" items="${mealTos}">
                    <c:if test="${mealTo.excess == true}">
                        <tr style="color: red">
                            <td><javatime:format value="${mealTo.dateTime}" pattern="yyyy-MM-dd HH:mm"/></td>
                            <td>${mealTo.description}</td>
                            <td>${mealTo.calories}</td>
                        </tr>
                    </c:if>
                    <c:if test="${mealTo.excess == false}">
                        <tr style="color: green">
                            <td><javatime:format value="${mealTo.dateTime}" pattern="yyyy-MM-dd HH:mm"/></td>
                            <td>${mealTo.description}</td>
                            <td>${mealTo.calories}</td>
                        </tr>
                    </c:if>
                </c:forEach>
            </tbody>
        </table>
    </body>
</html>
