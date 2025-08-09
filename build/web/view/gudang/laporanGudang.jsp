<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, java.text.SimpleDateFormat, model.BahanBaku, model.User" %>

<%
    List<BahanBaku> bahanList = (List<BahanBaku>) request.getAttribute("laporanData");
    String tanggalMulai = request.getAttribute("tanggalMulai") != null ? request.getAttribute("tanggalMulai").toString() : "";
    String tanggalAkhir = request.getAttribute("tanggalAkhir") != null ? request.getAttribute("tanggalAkhir").toString() : "";
    String filterWaktu = request.getAttribute("filterWaktu") != null ? request.getAttribute("filterWaktu").toString() : "all";
    String errorMessage = (String) request.getAttribute("error");
    User user = (User) session.getAttribute("user");
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
    <h1 class="laporan-title">Laporan Stok Bahan Baku</h1>
    <p class="laporan-user">Purchasing: <strong><%= user != null ? user.getNama() : "-" %></strong> | <span id="currentTime"></span></p>

    <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
        <div class="alert-error">
            <strong>Error:</strong> <%= errorMessage %>
        </div>
    <% } %>

    <!-- Filter -->
    <form method="get" action="<%= request.getContextPath() %>/gudang/laporan">
        <input type="hidden" name="page" value="gudang/laporanGudang.jsp">
        <fieldset class="filter-fieldset">
            <legend>Filter Waktu</legend>
            <label><input type="radio" name="filterWaktu" value="all" <%= "all".equals(filterWaktu) ? "checked" : "" %>> Semua</label>
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
                <a href="<%= request.getContextPath() %>/view/layout.jsp?page=gudang/laporanGudang.jsp" class="btn btn-secondary">Reset</a>
            </div>
        </fieldset>
    </form>

    <!-- Ringkasan -->
    <%
        int totalBahan = 0, bahanHabis = 0, bahanMenipis = 0;
        if (bahanList != null) {
            totalBahan = bahanList.size();
            for (BahanBaku b : bahanList) {
                int stok = b.getJumlah();
                if (stok <= 0) bahanHabis++;
                else if (stok <= 2000) bahanMenipis++;
            }
        }
    %>
    <table class="table summary-table">
        <tr><td>Total Jenis Bahan</td><td><%= totalBahan %></td></tr>
        <tr><td>Bahan Habis</td><td class="text-red"><%= bahanHabis %></td></tr>
        <tr><td>Bahan Menipis</td><td class="text-orange"><%= bahanMenipis %></td></tr>
    </table>

    <button onclick="window.print()" class="btn btn-print no-print">Cetak Laporan</button>

    <!-- AREA YANG DICETAK -->
    <div id="print-section">
        <h2 class="print-title">LAPORAN STOK BAHAN BAKU</h2>
        <div class="laporan-wrapper">
            <table class="table table-bordered laporan-table">
                <thead>
                    <tr>
                        <th>No</th>
                        <th>Nama Bahan</th>
                        <th>Stok</th>
                        <th>Satuan</th>
                        <th>Tanggal Masuk</th>
                        <th>Status</th>
                    </tr>
                </thead>
                <tbody>
                <%
                    if (bahanList != null && !bahanList.isEmpty()) {
                        int no = 1;
                        for (BahanBaku b : bahanList) {
                            int stok = b.getJumlah();
                %>
                    <tr>
                        <td><%= no++ %></td>
                        <td><%= b.getNamaBahan() %></td>
                        <td><%= stok %></td>
                        <td><%= b.getSatuan() %></td>
                        <td><%= new SimpleDateFormat("dd/MM/yyyy").format(b.getTanggalMasuk()) %></td>
                        <td>
                            <% if (stok <= 0) { %>
                                <span class="badge badge-red">Habis</span>
                            <% } else if (stok <= 2000) { %>
                                <span class="badge badge-orange">Menipis</span>
                            <% } else { %>
                                <span class="badge badge-green">Aman</span>
                            <% } %>
                        </td>
                    </tr>
                <%
                        }
                    } else {
                %>
                    <tr><td colspan="6" class="text-center">Tidak ada data untuk periode ini</td></tr>
                <% } %>
                </tbody>
            </table>
        </div>
        <div class="laporan-footer">
            <p><strong>Tanggal Cetak:</strong> <%= new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()) %></p>
            <p><strong>Dicetak oleh:</strong> <%= user != null ? user.getNama() : "-" %></p>
        </div>
    </div>
</div>

<script>
    function updateTime() {
        document.getElementById("currentTime").innerText = new Date().toLocaleString("id-ID");
    }
    updateTime();
    setInterval(updateTime, 1000);
</script>
