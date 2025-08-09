<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*" %>

<%
    if (session == null || session.getAttribute("user") == null) {
        response.sendRedirect(request.getContextPath() + "/auth.jsp?action=login");
        return;
    }

    String role = (String) session.getAttribute("role");
    String nama = (String) session.getAttribute("nama");

    // Prioritas: attribute dari servlet, lalu parameter dari URL
    String targetPage = (String) request.getAttribute("page");
    if (targetPage == null || targetPage.equals("")) {
        targetPage = request.getParameter("page");
    }
    if (targetPage == null || targetPage.equals("")) {
        targetPage = "dashboard.jsp";
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>D'Bakery - <%= role.toUpperCase() %></title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
</head>
<body>

<!-- Sidebar -->
<div class="sidebar">
    <div>
        <div class="sidebar-header">
            <i class="fas fa-bread-slice"></i>
            <span class="app-title">D'Bakery</span>
        </div>

        <!-- Menu utama ditengah -->
        <ul class="menu-links" style="margin-top: 80px;">
            <!-- ✅ Dashboard hanya untuk gudang dan baker, TIDAK untuk kasir -->
            <% if ("gudang".equals(role) || "baker".equals(role)) { %>
                <li><a href="<%= request.getContextPath() %>/view/layout.jsp?page=dashboard.jsp" class="<%= targetPage.equals("dashboard.jsp") ? "active" : "" %>">
                    <i class="fas fa-home"></i> <span>Dashboard</span></a></li>
            <% } %>

            <% if ("gudang".equals(role)) { %>
                <li><a href="<%= request.getContextPath() %>/view/layout.jsp?page=gudang/index.jsp" class="<%= targetPage.contains("gudang/index.jsp") ? "active" : "" %>">
                    <i class="fas fa-box"></i> <span>Bahan Baku</span></a></li>
                <li><a href="<%= request.getContextPath() %>/view/layout.jsp?page=gudang/laporanGudang.jsp" class="<%= targetPage.contains("gudang/laporanGudang.jsp") ? "active" : "" %>">
                    <i class="fas fa-clipboard-list"></i> <span>Laporan Gudang</span></a></li>
                <li><a href="<%= request.getContextPath() %>/view/layout.jsp?page=gudang/LaporanPenggunaan.jsp" class="<%= targetPage.contains("gudang/LaporanPenggunaan.jsp") ? "active" : "" %>">
                    <i class="fas fa-warehouse"></i> <span>Penggunaan Bahan</span></a></li>
            <% } else if ("baker".equals(role)) { %>
                <li><a href="<%= request.getContextPath() %>/view/layout.jsp?page=baker/Produksi.jsp" class="<%= targetPage.contains("baker/Produksi.jsp") ? "active" : "" %>">
                    <i class="fas fa-cookie-bite"></i> <span>Produksi</span></a></li>
                <li><a href="<%= request.getContextPath() %>/view/layout.jsp?page=baker/Resep.jsp" class="<%= targetPage.contains("baker/Resep.jsp") ? "active" : "" %>">
                    <i class="fas fa-utensils"></i> <span>Resep</span></a></li>
                <li><a href="<%= request.getContextPath() %>/view/layout.jsp?page=baker/LaporanProduksi.jsp" class="<%= targetPage.contains("baker/LaporanProduksi.jsp") ? "active" : "" %>">
                    <i class="fas fa-chart-line"></i> <span>Laporan Produksi</span></a></li>
            <% } else if ("kasir".equals(role)) { %>
                <li><a href="<%= request.getContextPath() %>/view/layout.jsp?page=kasir/index.jsp" class="<%= targetPage.contains("kasir/index.jsp") ? "active" : "" %>">
                    <i class="fas fa-cash-register"></i> <span>Dashboard Kasir</span></a></li>

                <!-- PERBAIKAN: Link transaksi baru harus ke servlet untuk mengambil data -->
                <li><a href="<%= request.getContextPath() %>/PenjualanServlet?action=form" class="<%= targetPage.contains("kasir/FormTransaksi.jsp") ? "active" : "" %>">
                    <i class="fas fa-shopping-cart"></i> <span>Transaksi Baru</span></a></li>

                <!-- PERBAIKAN: Link laporan harus ke servlet untuk mengambil data -->
                <li><a href="<%= request.getContextPath() %>/PenjualanServlet?action=laporan" class="<%= targetPage.contains("kasir/LaporanTransaksi.jsp") ? "active" : "" %>">
                    <i class="fas fa-receipt"></i> <span>Laporan Transaksi</span></a></li>
            <% } %>
        </ul>
    </div>

    <!-- Logout tetap di bawah -->
    <ul class="logout-link" style="margin-bottom: 30px;" >
        <li><a href="<%= request.getContextPath() %>/logout"><i class="fas fa-sign-out-alt"></i> <span>Logout</span></a></li>
    </ul>
</div>

<!-- Navbar -->
<div class="navbar">
    <div class="toggle-btn" onclick="toggleSidebar()">
        <i class="fas fa-bars"></i>
    </div>

    <!-- Jam di tengah -->
    <div class="realtime-clock" id="clock" style="position: absolute; left: 50%; transform: translateX(-50%); font-weight: bold;"></div>

    <div class="navbar-right">
        <div class="user-dropdown" onclick="toggleUserMenu()">
            <div class="user-avatar"><%= nama.substring(0, 1).toUpperCase() %></div>
            <div class="dropdown-menu">
                <a href="#"><i class="fas fa-user"></i> <%= nama %></a>
                <a href="#"><i class="fas fa-user-tag"></i> <%= role.toUpperCase() %></a>
            </div>
        </div>
        <button class="dark-btn" onclick="toggleDarkMode()"><i class="fas fa-moon"></i></button>
    </div>
</div>

<!-- Konten -->
<div class="content">
    <jsp:include page="<%= targetPage %>" />
</div>

<script>
    function toggleSidebar() {
        document.querySelector('.sidebar').classList.toggle('active');
        document.querySelector('.navbar').classList.toggle('active');
        document.querySelector('.content').classList.toggle('active');
    }

    function toggleDarkMode() {
        document.body.classList.toggle("dark-mode");
        const icon = document.querySelector('.dark-btn i');
        if (document.body.classList.contains("dark-mode")) {
            localStorage.setItem("darkMode", "enabled");
            icon.classList.remove("fa-moon");
            icon.classList.add("fa-sun");
        } else {
            localStorage.setItem("darkMode", "disabled");
            icon.classList.remove("fa-sun");
            icon.classList.add("fa-moon");
        }
    }

    // Saat pertama kali load, sesuaikan icon
    window.addEventListener("DOMContentLoaded", () => {
        const icon = document.querySelector('.dark-btn i');
        if (localStorage.getItem("darkMode") === "enabled") {
            document.body.classList.add("dark-mode");
            icon.classList.remove("fa-moon");
            icon.classList.add("fa-sun");
        }
    });

    function toggleUserMenu() {
        document.querySelector(".user-dropdown").classList.toggle("active");
    }

    function updateClock() {
        const clock = document.getElementById("clock");
        const now = new Date();
        const jam = now.getHours().toString().padStart(2, '0');
        const menit = now.getMinutes().toString().padStart(2, '0');
        const detik = now.getSeconds().toString().padStart(2, '0');
        clock.innerText = jam + '.' + menit + '.' + detik;
    }
    setInterval(updateClock, 1000);
    updateClock();
</script>

</body>
</html>