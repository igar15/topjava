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
        <p><a href="meals?action=create">Add Meal</a></p>
        <c:if test="${mealTos.size() == 0}">
            <h3>You do not have meals</h3>
        </c:if>
        <c:if test="${mealTos.size() > 0}">
            <table border="" cellpadding="5">
                <thead>
                <th>DateTime</th>
                <th>Description</th>
                <th>Calories</th>
                <th>Action</th>
                </thead>
                <tbody align="center">
                <c:forEach var="mealTo" items="${mealTos}">
                    <tr ${mealTo.excess == true ? 'style="color: red"' : 'style="color: green"'}>
                        <td><javatime:format value="${mealTo.dateTime}" pattern="yyyy-MM-dd HH:mm"/></td>
                        <td>${mealTo.description}</td>
                        <td>${mealTo.calories}</td>
                        <td>
                            <a href="meals?action=update&id=${mealTo.id}">Update</a> |
                            <a href="meals?action=delete&id=${mealTo.id}">Delete</a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:if>
    </body>
</html>
