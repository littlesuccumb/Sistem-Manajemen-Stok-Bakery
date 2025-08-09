<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, java.text.SimpleDateFormat, model.Produksi, model.User" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null || !"baker".equals(user.getRole())) {
        response.sendRedirect(request.getContextPath() + "/auth.jsp");
        return;
    }

    List<Produksi> laporanProduksi = (List<Produksi>) request.getAttribute("laporanProduksi");
    String tanggalMulai = request.getAttribute("tanggalMulai") != null ? request.getAttribute("tanggalMulai").toString() : "";
    String tanggalAkhir = request.getAttribute("tanggalAkhir") != null ? request.getAttribute("tanggalAkhir").toString() : "";
    String filterWaktu = request.getAttribute("filterWaktu") != null ? request.getAttribute("filterWaktu").toString() : "all";
    String errorMessage = (String) request.getAttribute("error");
%>

<style>
@media print {
    body {
        background: white;
        margin: 0;
        padding: 0;
        font-size: 12px;
        color: #000;
    }

    /* SEMBUNYIKAN SEMUA KECUALI DIV CETAK */
    body * {
        visibility: hidden;
    }

    #print-section, #print-section * {
        visibility: visible;
    }

    #print-section {
        position: absolute;
        left: 0;
        top: 0;
        width: 100%;
    }

    .laporan-table {
        width: 100%;
        border-collapse: collapse;
        font-size: 12px;
    }

    .laporan-table th, .laporan-table td {
        border: 1px solid #333;
        padding: 8px;
        text-align: center;
    }

    .laporan-table thead {
        background-color: #ccc !important;
        -webkit-print-color-adjust: exact;
    }

    h1, h2 {
        text-align: center;
        margin: 0 0 10px 0;
    }
}
</style>

<div class="container laporan-container">
    <h1 class="laporan-title">Laporan Produksi</h1>
    <p class="laporan-user">Baker: <strong><%= user.getNama() %></strong> | <span id="currentTime"></span></p>

    <!-- Tampilkan pesan error jika ada -->
    <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
        <div class="alert-error">
            <strong>Error:</strong> <%= errorMessage %>
        </div>
    <% } %>

    <!-- Filter Waktu - UPDATE: Form action mengarah ke servlet -->
    <form method="get" action="<%= request.getContextPath() %>/baker/laporan">
        <fieldset class="filter-fieldset">
            <legend>Filter Waktu</legend>
            <label><input type="radio" name="filterWaktu" value="all" <%= "all".equals(filterWaktu) ? "checked" : "" %>> Semua Periode</label>
            <label><input type="radio" name="filterWaktu" value="mingguan" <%= "mingguan".equals(filterWaktu) ? "checked" : "" %>> Minggu Ini</label>
            <label><input type="radio" name="filterWaktu" value="bulanan" <%= "bulanan".equals(filterWaktu) ? "checked" : "" %>> Bulan Ini</label>
            <label><input type="radio" name="filterWaktu" value="kustom" <%= "kustom".equals(filterWaktu) ? "checked" : "" %>> Kustom</label>
            <div class="filter-date">
                <label>Tanggal Mulai:</label>
                <input type="date" name="tanggalMulai" value="<%= tanggalMulai %>">
                <label>Tanggal Akhir:</label>
                <input type="date" name="tanggalAkhir" value="<%= tanggalAkhir %>">
            </div>
            <div class="filter-button-group">
                <button type="submit" class="btn btn-primary">Terapkan</button>
                <a href="<%= request.getContextPath() %>/baker/laporan" class="btn btn-secondary">Reset</a>
            </div>
        </fieldset>
    </form>

    <!-- Ringkasan -->
    <%
        int totalProduksi = 0, totalRoti = 0, produksiTersedia = 0;
        double totalPendapatan = 0;
        if (laporanProduksi != null) {
            totalProduksi = laporanProduksi.size();
            for (Produksi p : laporanProduksi) {
                totalRoti += p.getJumlahProduksi();
                totalPendapatan += (p.getJumlahProduksi() * p.getHargaJual());
                if ("tersedia".equals(p.getStatus())) produksiTersedia++;
            }
        }
    %>
    <table class="table summary-table">
        <tr><td>Total Jenis Produksi</td><td><%= totalProduksi %></td></tr>
        <tr><td>Total Roti Diproduksi</td><td><%= totalRoti %> pcs</td></tr>
        <tr><td>Produksi Tersedia</td><td class="text-green"><%= produksiTersedia %></td></tr>
        <tr><td>Estimasi Pendapatan</td><td>Rp <%= String.format("%,.0f", totalPendapatan) %></td></tr>
    </table>

    <button onclick="window.print()" class="btn btn-print no-print">Cetak Laporan</button>

    <!-- AREA YANG DICETAK -->
    <div id="print-section">
        <h2 class="print-title">LAPORAN PRODUKSI</h2>
        <div class="laporan-wrapper">
            <table class="table table-bordered laporan-table">
                <thead>
                    <tr>
                        <th>No</th>
                        <th>Nama Produk</th>
                        <th>Jumlah Produksi</th>
                        <th>Harga Jual</th>
                        <th>Tanggal Produksi</th>
                        <th>Status</th>
                        <th>Estimasi Nilai</th>
                    </tr>
                </thead>
                <tbody>
                <%
                    if (laporanProduksi != null && !laporanProduksi.isEmpty()) {
                        int no = 1;
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        for (Produksi p : laporanProduksi) {
                            double nilaiProduksi = p.getJumlahProduksi() * p.getHargaJual();
                %>
                    <tr>
                        <td><%= no++ %></td>
                        <td><%= p.getNamaProduk() %></td>
                        <td><%= p.getJumlahProduksi() %> pcs</td>
                        <td>Rp <%= String.format("%,d", p.getHargaJual()) %></td>
                        <td><%= sdf.format(p.getTanggalProduksi()) %></td>
                        <td>
                            <% if ("tersedia".equals(p.getStatus())) { %>
                                <span class="badge badge-green">Tersedia</span>
                            <% } else if ("habis".equals(p.getStatus())) { %>
                                <span class="badge badge-red">Habis</span>
                            <% } else { %>
                                <span class="badge badge-orange"><%= p.getStatus() %></span>
                            <% } %>
                        </td>
                        <td>Rp <%= String.format("%,.0f", nilaiProduksi) %></td>
                    </tr>
                <% 
                        }
                    } else { 
                %>
                    <tr><td colspan="7" class="text-center">Tidak ada data untuk periode ini</td></tr>
                <% } %>
                </tbody>
            </table>
        </div>
        <div class="laporan-footer">
            <p><strong>Tanggal Cetak:</strong> <%= new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()) %></p>
            <p><strong>Dicetak oleh:</strong> <%= user.getNama() %></p>
        </div>
    </div>
</div>

<script>
    function updateTime() {
        document.getElementById("currentTime").innerText = new Date().toLocaleString("id-ID");
    }
    updateTime();
    setInterval(updateTime, 1000);

    setTimeout(() => window.location.reload(), 300000); // Auto-refresh tiap 5 menit
</script>