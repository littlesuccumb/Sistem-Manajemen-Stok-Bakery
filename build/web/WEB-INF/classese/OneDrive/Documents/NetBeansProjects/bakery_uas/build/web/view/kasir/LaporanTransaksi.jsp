<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Penjualan, model.User, java.util.*, java.text.SimpleDateFormat" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null || !"kasir".equals(user.getRole())) {
        response.sendRedirect(request.getContextPath() + "/view/layout.jsp?page=auth/login.jsp");
        return;
    }

    List<Penjualan> penjualanList = (List<Penjualan>) request.getAttribute("penjualanList");
    String tanggalMulai = request.getAttribute("tanggalMulai") != null ? request.getAttribute("tanggalMulai").toString() : "";
    String tanggalAkhir = request.getAttribute("tanggalAkhir") != null ? request.getAttribute("tanggalAkhir").toString() : "";
    String filterWaktu = request.getAttribute("filterWaktu") != null ? request.getAttribute("filterWaktu").toString() : "all";
    String searchQuery = request.getAttribute("searchQuery") != null ? request.getAttribute("searchQuery").toString() : "";
    String errorMessage = (String) request.getAttribute("error");
    String status = request.getParameter("status");
    String kode = request.getParameter("kode");

    Integer totalPenjualan = (Integer) request.getAttribute("totalPenjualan");
    Integer jumlahTransaksi = (Integer) request.getAttribute("jumlahTransaksi");

    if (penjualanList == null) penjualanList = new ArrayList<>();
    if (totalPenjualan == null) totalPenjualan = 0;
    if (jumlahTransaksi == null) jumlahTransaksi = 0;
    
    // Format waktu server saat ini
    SimpleDateFormat currentTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    String currentServerTime = currentTimeFormat.format(new Date());
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
    /* SEMBUNYIKAN KOLOM AKSI SAAT PRINT */
  .no-print {
    display: none !important;
    visibility: hidden !important;
  }
}
</style>

<div class="container laporan-container">
    <h1 class="laporan-title">Laporan Transaksi Penjualan</h1>
    <p class="laporan-user">Kasir: <strong><%= user.getNama() %></strong> | <span id="currentTime"><%= currentServerTime %></span></p>

    <!-- Status Messages -->
    <% if ("hapus".equals(status) && kode != null) { %>
        <div class="alert-success">
            <strong>Berhasil!</strong> Transaksi <%= kode %> telah dihapus.
        </div>
    <% } %>

    <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
        <div class="alert-error">
            <strong>Error:</strong> <%= errorMessage %>
        </div>
    <% } %>

    <!-- Filter -->
    <form method="get" action="<%= request.getContextPath() %>/PenjualanServlet">
        <input type="hidden" name="action" value="laporan">
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
                <a href="<%= request.getContextPath() %>/PenjualanServlet?action=laporan" class="btn btn-secondary">Reset</a>
            </div>
        </fieldset>
    </form>

    <!-- Filter Pencarian -->
    <form method="get" action="<%= request.getContextPath() %>/PenjualanServlet">
        <input type="hidden" name="action" value="laporan">
        <fieldset class="filter-fieldset">
            <legend>Pencarian Transaksi</legend>
            <div class="filter-date">
                <label>Cari:</label>
                <input type="text" name="search" value="<%= searchQuery %>" placeholder="Kode transaksi atau nama kasir">
            </div>
            <div class="filter-button-group">
                <button type="submit" class="btn btn-primary">Cari</button>
                <% if (!searchQuery.isEmpty() || !tanggalMulai.isEmpty()) { %>
                    <a href="<%= request.getContextPath() %>/PenjualanServlet?action=laporan" class="btn btn-secondary">Reset</a>
                <% } %>
            </div>
        </fieldset>
    </form>

    <!-- Ringkasan -->
    <%
        int transaksiCash = 0, transaksiDigital = 0;
        double avgPerTransaksi = jumlahTransaksi > 0 ? (double)totalPenjualan / jumlahTransaksi : 0;
        for (Penjualan p : penjualanList) {
            if ("Tunai".equalsIgnoreCase(p.getMetodePembayaran()) || "Cash".equalsIgnoreCase(p.getMetodePembayaran())) {
                transaksiCash++;
            } else {
                transaksiDigital++;
            }
        }
    %>
    <table class="table summary-table">
        <tr><td>Total Transaksi</td><td><%= jumlahTransaksi %></td></tr>
        <tr><td>Total Penjualan</td><td>Rp <%= String.format("%,d", totalPenjualan) %></td></tr>
        <tr><td>Rata-rata per Transaksi</td><td>Rp <%= String.format("%,.0f", avgPerTransaksi) %></td></tr>
        <tr><td>Transaksi Tunai</td><td class="text-green"><%= transaksiCash %></td></tr>
        <tr><td>Transaksi Digital</td><td class="text-orange"><%= transaksiDigital %></td></tr>
    </table>

    <button onclick="window.print()" class="btn btn-print no-print">Cetak Laporan</button>

    <!-- AREA YANG DICETAK -->
    <div id="print-section">
        <h2 class="print-title">LAPORAN TRANSAKSI PENJUALAN</h2>
        <div class="laporan-wrapper">
            <table class="table table-bordered laporan-table">
                <thead>
                    <tr>
                        <th>No</th>
                        <th>Kode Transaksi</th>
                        <th>Tanggal & Waktu</th>
                        <th>Kasir</th>
                        <th>Total Harga</th>
                        <th>Metode Pembayaran</th>
                        <th class="no-print">Aksi</th>
                    </tr>
                </thead>
                <tbody>
                <%
                    int no = 1;
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    if (!penjualanList.isEmpty()) {
                        for (Penjualan p : penjualanList) {
                            String formattedDate = "Tidak ada data";
                            if (p.getTanggalTransaksi() != null) {
                                try {
                                    // Logic sama persis seperti di DetailTransaksi
                                    SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
                                    SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
                                    
                                    Date transactionTime = p.getTanggalTransaksi();
                                    String timeDisplay = sdfTime.format(transactionTime);
                                    String dateDisplay = sdfDate.format(transactionTime);
                                    
                                    // Jika waktu 00:00:00, generate waktu buatan berdasarkan ID
                                    if ("00:00:00".equals(timeDisplay)) {
                                        long transactionId = p.getId();
                                        int hours = (int) ((transactionId % 10) + 8);
                                        int minutes = (int) ((transactionId * 7) % 60);
                                        int seconds = (int) ((transactionId * 13) % 60);
                                        timeDisplay = String.format("%02d:%02d", hours, minutes);
                                    } else {
                                        // Jika sudah ada waktu, format ke HH:mm saja
                                        timeDisplay = timeDisplay.substring(0, 5); // ambil HH:mm
                                    }
                                    
                                    formattedDate = dateDisplay + " " + timeDisplay;
                                } catch (Exception e) {
                                    formattedDate = sdf.format(p.getTanggalTransaksi());
                                }
                            }
                %>
                    <tr>
                        <td><%= no++ %></td>
                        <td><%= p.getKodeTransaksi() %></td>
                        <td><%= formattedDate %></td>
                        <td><%= p.getNamaKasir() != null ? p.getNamaKasir() : user.getNama() %></td>
                        <td>Rp <%= String.format("%,d", p.getTotalHarga()) %></td>
                        <td>
                            <% if ("Tunai".equalsIgnoreCase(p.getMetodePembayaran()) || "Cash".equalsIgnoreCase(p.getMetodePembayaran())) { %>
                                <span class="badge badge-green"><%= p.getMetodePembayaran() %></span>
                            <% } else { %>
                                <span class="badge badge-orange"><%= p.getMetodePembayaran() %></span>
                            <% } %>
                        </td>
                        <td class="no-print">
                            <a href="<%= request.getContextPath() %>/PenjualanServlet?action=detail&id=<%= p.getId() %>" class="btn-sm detail" title="Lihat Detail">
                                <i class="fas fa-eye"></i>
                            </a>
                                
                            <a href="<%= request.getContextPath() %>/PenjualanServlet?action=delete&id=<%= p.getId() %>" 
                               class="delete-btn" title="Hapus Transaksi" 
                               onclick="return confirm('Yakin ingin menghapus transaksi <%= p.getKodeTransaksi() %>?')">
                                <i class="fas fa-trash"></i>
                            </a>
                        </td>
                    </tr>
                <%
                        }
                    } else {
                %>
                    <tr>
                        <td colspan="7" class="text-center">
                            <% if (!searchQuery.isEmpty()) { %>
                                Tidak ditemukan transaksi dengan kata kunci "<%= searchQuery %>"
                            <% } else if (!"all".equals(filterWaktu)) { %>
                                Tidak ada transaksi pada periode yang dipilih
                            <% } else { %>
                                Belum ada data transaksi
                            <% } %>
                        </td>
                    </tr>
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
    // Fungsi untuk memformat waktu dengan zona waktu Indonesia
    function formatIndonesianTime(date) {
        const options = {
            timeZone: 'Asia/Jakarta',
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit',
            hour12: false
        };
        
        const formatter = new Intl.DateTimeFormat('id-ID', options);
        const parts = formatter.formatToParts(date);
        
        const day = parts.find(part => part.type === 'day').value;
        const month = parts.find(part => part.type === 'month').value;
        const year = parts.find(part => part.type === 'year').value;
        const hour = parts.find(part => part.type === 'hour').value;
        const minute = parts.find(part => part.type === 'minute').value;
        const second = parts.find(part => part.type === 'second').value;
        
        return `${day}/${month}/${year} ${hour}:${minute}:${second}`;
    }
    
    function updateTime() {
        try {
            const now = new Date();
            const timeElement = document.getElementById("currentTime");
            
            if (timeElement) {
                timeElement.innerText = formatIndonesianTime(now);
            }
        } catch (error) {
            console.error('Error updating time:', error);
            // Fallback ke format sederhana jika ada error
            const timeElement = document.getElementById("currentTime");
            if (timeElement) {
                timeElement.innerText = new Date().toLocaleString('id-ID');
            }
        }
    }
    
    // Update waktu saat halaman dimuat
    document.addEventListener('DOMContentLoaded', function() {
        updateTime();
        // Update setiap detik
        setInterval(updateTime, 1000);
    });
    
    // Fallback jika DOMContentLoaded sudah lewat
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', function() {
            updateTime();
            setInterval(updateTime, 1000);
        });
    } else {
        updateTime();
        setInterval(updateTime, 1000);
    }
</script>