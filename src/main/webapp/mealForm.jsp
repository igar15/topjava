<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
    <head>
        <title>Meal Form</title>
    </head>
    <body>
        <h3><a href="index.html">Home</a></h3>
        <hr>
        <h2>
            ${not empty meal.id ? 'Update meal' : 'Create meal'}
        </h2>
        <form method="POST" action="meals">
            <input type="hidden" name="id" value="<c:out value="${meal.id}"/>"/>
            DateTime: <input type="datetime-local" name="dateTime" value="<c:out value="${meal.dateTime}"/>"/>
            <br>
            Description: <input type="text" name="description" value="<c:out value="${meal.description}"/>"/>
            <br>
            Calories: <input type="number" name="calories" value="<c:out value="${meal.calories}"/>"/>
            <br>
            <input type="submit" value="Submit"/>
            <button onclick="location.href='meals';" type="button">Cancel</button>
        </form>
    </body>
</html>
