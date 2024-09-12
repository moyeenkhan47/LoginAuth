<!DOCTYPE html>
<html>
<head>
    <title>User Registration</title>
</head>
<body>
    <h1>Register</h1>

    <form action="${pageContext.request.contextPath}/api/register" method="post">
        <div>
            <label for="username">Username:</label>
            <input type="text" id="username" name="username" required />
        </div>
        <div>
            <label for="password">Password:</label>
            <input type="password" id="password" name="password" required />
        </div>
        <div>
            <label for="confirmPassword">Confirm Password:</label>
            <input type="password" id="confirmPassword" name="confirmPassword" required />
        </div>
        <div>
            <button type="submit">Register</button>
        </div>

        <!-- Display error or success messages -->
        <div>
            <c:if test="${not empty error}">
                <p style="color: red;">${error}</p>
            </c:if>
            <c:if test="${not empty success}">
                <p style="color: green;">${success}</p>
            </c:if>
        </div>
    </form>
</body>
</html>
