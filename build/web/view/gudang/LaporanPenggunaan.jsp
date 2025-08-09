<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.*, java.text.SimpleDateFormat" %>
<%@ page import="model.User, model.LogStok" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null || !"gudang".equals(user.getRole())) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }

    List<LogStok> logList = (List<LogStok>) request.getAttribute("logList");
    if (logList == null) logList = new ArrayList<>();
%>

<div class="main-content">
    <div class="container">
        <h1 class="page-title">Laporan Penggunaan Bahan</h1>

        <form method="get" action="<%= request.getContextPath() %>/view/layout.jsp" class="filter-form">
            <input type="hidden" name="page" value="gudang/LaporanPenggunaan.jsp">          
            <div class="form-row">
                <label>Tanggal Mulai:</label>
                <input type="date" name="start" value="<%= request.getAttribute("startDate") != null ? request.getAttribute("startDate") : "" %>" required>
                <label>Tanggal Akhir:</label>
                <input type="date" name="end" value="<%= request.getAttribute("endDate") != null ? request.getAttribute("endDate") : "" %>" required>
                <button type="submit" class="btn btn-primary" style="margin-left: 10px">Filter</button>
                <a href="<%= request.getContextPath() %>/view/layout.jsp?page=gudang/LaporanPenggunaan.jsp" class="btn btn-secondary">Reset</a>
            </div>
        </form>

        <table class="laporan-table">
            <thead>
                <tr>
                    <th>No</th>
                    <th>Nama Bahan</th>
                    <th>Jumlah Dikurangi</th>
                    <th>Resep ID</th>
                    <th>Keterangan</th>
                    <th>Tanggal</th>
                </tr>
            </thead>
            <tbody>
                <%
                    int no = 1;
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    for (LogStok log : logList) {
                %>
                <tr>
                    <td><%= no++ %></td>
                    <td><%= log.getNamaBahan() %></td>
                    <td><%= log.getJumlahDikurangi() %></td>
                    <td><%= log.getResepId() %></td>
                    <td><%= log.getKeterangan() %></td>
                    <td><%= sdf.format(log.getTanggal()) %></td>
                </tr>
                <% } %>
                <% if (logList.isEmpty()) { %>
                <tr>
                    <td colspan="6" style="text-align: center;">Tidak ada data penggunaan bahan</td>
                </tr>
                <% } %>
            </tbody>
        </table>
    </div>
</div>
