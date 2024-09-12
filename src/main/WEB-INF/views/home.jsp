<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Home</title>
</head>
<body>
    <h1>Welcome Home!</h1>
    <p>You have successfully logged in.</p>

    <!-- Logout link -->
    <a href="${pageContext.request.contextPath}/logout">Logout</a>
</body>
</html>
