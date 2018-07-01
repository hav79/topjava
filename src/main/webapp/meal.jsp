<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Meal</title>
</head>
<body>
    <form method="post" action="meals?action=list">
        <label for="mealId">MealId:</label>
        <input id="mealId" name="mealId" type="text" value="${meal.id}" readonly>

        <label for="description">Description:</label>
        <input id="description" name="description" type="text" value="${meal.description}">

        <label for="dateTime">MealId:</label>
        <input id="dateTime" name="dateTime" type="datetime-local" value="${meal.dateTime}">

        <label for="calories">MealId:</label>
        <input id="calories" name="calories" type="text" value="${meal.calories}">

        <input type="submit" value="Submit">
    </form>
</body>
</html>
