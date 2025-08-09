<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%@ page session="true" %>
<%
    String error = (String) request.getAttribute("error");
    String success = (String) request.getAttribute("success");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Login & Register - Sistem Bakery</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/auth.css">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <script src="https://unpkg.com/boxicons@2.1.4/dist/boxicons.js"></script>
</head>
<body>
    <div class="container">
        <div class="curved-shape"></div>
        <div class="curved-shape2"></div>

        <% if (error != null) { %>
            <p class="notif error"><%= error %></p>
        <% } %>
        <% if (success != null) { %>
            <p class="notif success"><%= success %></p>
        <% } %>

        <!-- Form Login -->
        <div class="form-box Login">
            <h2 class="animation" style="--D:0; --S:21">Login</h2>
            <form action="<%= request.getContextPath() %>/login" method="post">
                <input type="hidden" name="action" value="login">

                <div class="input-box animation" style="--D:1; --S:22">
                    <input type="text" name="username" required>
                    <label>Username</label>
                    <box-icon type="solid" name="user"></box-icon>
                </div>

                <div class="input-box animation" style="--D:2; --S:23">
                    <input type="password" name="password" required>
                    <label>Password</label>
                    <box-icon name="lock-alt" type="solid"></box-icon>
                </div>

                <div class="input-box animation" style="--D:3; --S:24">
                    <button class="btn" type="submit">Login</button>
                </div>

                <div class="regi-link animation" style="--D:4; --S:25">
                    <p>Don't have an account? <a href="#" class="SignUpLink">Sign Up</a></p>
                </div>
            </form>
        </div>

        <div class="info-content Login">
            <h2 class="animation" style="--D:0; --S:20">WELCOME BACK!</h2>
            <p class="animation" style="--D:1; --S:21">  Kami senang bisa melayani Anda lagi. Siap mengelola toko bakery hari ini?
</p>
        </div>

        <!-- Form Register -->
        <div class="form-box Register">
            <h2 class="animation" style="--li:17; --S:0">Register</h2>
            <form action="<%= request.getContextPath() %>/login" method="post">
                <input type="hidden" name="action" value="register">

                <div class="input-box animation" style="--li:18; --S:1">
                    <input type="text" name="nama" required>
                    <label>Nama Lengkap</label>
                    <box-icon type="solid" name="user"></box-icon>
                </div>

                <div class="input-box animation" style="--li:19; --S:2">
                    <input type="text" name="username" required>
                    <label>Username</label>
                    <box-icon type="solid" name="user"></box-icon>
                </div>

                <div class="input-box animation" style="--li:20; --S:3">
                    <input type="password" name="password" required>
                    <label>Password</label>
                    <box-icon name="lock-alt" type="solid"></box-icon>
                </div>

                <div class="input-box animation" style="--li:21; --S:4">
                    <select name="role" required>
                        <option value="">Pilih Role</option>
                        <option value="gudang">Gudang</option>
                        <option value="baker">Baker</option>
                        <option value="kasir">Kasir</option>
                    </select>
                </div>

                <div class="input-box animation" style="--li:22; --S:5">
                    <button class="btn" type="submit">Register</button>
                </div>

                <div class="regi-link animation" style="--li:23; --S:6">
                    <p>Already have an account? <a href="#" class="SignInLink">Sign In</a></p>
                </div>
            </form>
        </div>

        <div class="info-content Register">
            <h2 class="animation" style="--li:17; --S:0">WELCOME!</h2>
            <p class="animation" style="--li:18; --S:1">  Silakan daftar untuk mulai mengelola produk, pesanan, dan transaksi bakery Anda.</p>
        </div>
    </div>
    <div class="back-to-home">
    <a href="<%= request.getContextPath() %>/index.jsp" class="btn back-btn">← Kembali ke Halaman Utama</a>
</div>

    <script src="js/auth.js"></script>
</body>
</html>
