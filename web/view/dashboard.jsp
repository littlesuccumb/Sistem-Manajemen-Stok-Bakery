<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<%
    String nama = (String) session.getAttribute("nama");
    String role = (String) session.getAttribute("role");

    if (nama == null || role == null) {
        response.sendRedirect(request.getContextPath() + "/auth.jsp?action=login");
        return;
    }

    String roleDisplay = "";
    switch (role) {
        case "gudang": roleDisplay = "Petugas Gudang"; break;
        case "baker": roleDisplay = "Baker"; break;
        case "kasir": roleDisplay = "Kasir"; break;
        default: roleDisplay = "Pengguna";
    }
%>

<div class="container mt-5 welcome-container">
    <h1>Selamat Datang, <%= roleDisplay %>!</h1>
    <p>Halo <strong><%= nama %></strong>, selamat bekerja dan semoga harimu menyenangkan 🍞</p>
</div>
