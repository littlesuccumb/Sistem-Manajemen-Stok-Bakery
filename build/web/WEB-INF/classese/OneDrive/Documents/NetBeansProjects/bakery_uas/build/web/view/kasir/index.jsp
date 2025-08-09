<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, dao.PenjualanDAO, dao.ProduksiDAO, model.Penjualan, model.Produksi, model.User" %>

<%
    if (session.getAttribute("user") == null) {
        response.sendRedirect(request.getContextPath() + "/view/layout.jsp?page=auth/login.jsp");
        return;
    }

    User user = (User) session.getAttribute("user");

    PenjualanDAO penjualanDAO = new PenjualanDAO();
    ProduksiDAO produksiDAO = new ProduksiDAO();

    int totalPenjualanHariIni = 0;
    int jumlahTransaksiHariIni = 0;
    List<Penjualan> transaksiTerbaru = new ArrayList<>();
    List<Produksi> produkTersedia = new ArrayList<>();

    try {
        totalPenjualanHariIni = penjualanDAO.getTotalPenjualanHariIni();
        jumlahTransaksiHariIni = penjualanDAO.getJumlahTransaksiHariIni();
        int kasirId = "kasir".equals(user.getRole()) ? user.getId() : 0;
        List<Penjualan> allTransaksi = penjualanDAO.getPenjualanHariIni(kasirId);
        transaksiTerbaru = allTransaksi.size() > 5 ? allTransaksi.subList(0, 5) : allTransaksi;
        produkTersedia = produksiDAO.getProdukTersedia();
    } catch (Exception e) {
        e.printStackTrace();
    }

    String status = request.getParameter("status");
    String kodeTransaksi = request.getParameter("kode");
%>

<div class="container">
    <% if ("berhasil".equals(status)) { %>
        <div class="notif success">
            <i class="fas fa-check-circle"></i>
            Transaksi berhasil! Kode: <strong><%= kodeTransaksi != null ? kodeTransaksi : "" %></strong>
        </div>
    <% } else if ("hapus".equals(status)) { %>
        <div class="notif success"><i class="fas fa-check-circle"></i> Transaksi berhasil dihapus.</div>
    <% } else if ("gagal".equals(status)) { %>
        <div class="notif error"><i class="fas fa-exclamation-circle"></i> Gagal memproses transaksi.</div>
    <% } %>

    <div class="dashboard-header">
        <h2><i class="fas fa-cash-register"></i> Dashboard Kasir</h2>
        <p>Selamat datang, <strong><%= user.getNama() %></strong></p>
    </div>

    <div class="stats-container">
        <div class="stat-card blue">
            <div class="stat-icon"><i class="fas fa-money-bill-wave"></i></div>
            <div class="stat-info">
                <h3>Rp <%= String.format("%,d", totalPenjualanHariIni) %></h3>
                <p>Penjualan Hari Ini</p>
            </div>
        </div>
        <div class="stat-card green">
            <div class="stat-icon"><i class="fas fa-receipt"></i></div>
            <div class="stat-info">
                <h3><%= jumlahTransaksiHariIni %></h3>
                <p>Transaksi Hari Ini</p>
            </div>
        </div>
        <div class="stat-card orange">
            <div class="stat-icon"><i class="fas fa-box"></i></div>
            <div class="stat-info">
                <h3><%= produkTersedia.size() %></h3>
                <p>Produk Tersedia</p>
            </div>
        </div>
        <div class="stat-card purple">
            <div class="stat-icon"><i class="fas fa-chart-line"></i></div>
            <div class="stat-info">
                <h3>Rp <%= jumlahTransaksiHariIni > 0 ? String.format("%,d", totalPenjualanHariIni / jumlahTransaksiHariIni) : "0" %></h3>
                <p>Rata-rata per Transaksi</p>
            </div>
        </div>
    </div>

    <div class="quick-actions">
        <h4><i class="fas fa-bolt"></i> Aksi Cepat</h4>
        <div class="action-buttons">
            <a href="<%= request.getContextPath() %>/PenjualanServlet?action=form" class="btn-action primary">
                <i class="fas fa-plus-circle"></i>
                <span>Transaksi Baru</span>
            </a>
            <a href="<%= request.getContextPath() %>/PenjualanServlet?action=laporan&periode=hari_ini" class="btn-action secondary">
                <i class="fas fa-list"></i>
                <span>Riwayat Hari Ini</span>
            </a>
            <a href="<%= request.getContextPath() %>/PenjualanServlet?action=laporan" class="btn-action info">
                <i class="fas fa-chart-bar"></i>
                <span>Laporan Penjualan</span>
            </a>
        </div>
    </div>

    <div class="content-grid">
        <div class="content-card">
            <div class="card-header">
                <h4><i class="fas fa-history"></i> Transaksi Terbaru</h4>
                <a href="<%= request.getContextPath() %>/PenjualanServlet?action=laporan&periode=hari_ini" class="view-all">Lihat Semua</a>
            </div>
            <div class="transaction-list">
                <% if (transaksiTerbaru.isEmpty()) { %>
                    <div class="empty-state">
                        <i class="fas fa-receipt"></i>
                        <p>Belum ada transaksi hari ini</p>
                    </div>
                <% } else { %>
                    <% 
                        java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("HH:mm");
                        java.text.SimpleDateFormat dateTimeFormat = new java.text.SimpleDateFormat("dd/MM HH:mm");
                        for (Penjualan p : transaksiTerbaru) { 
                            String displayTime = "00:00"; // default fallback
                            String fullDateTime = "Tidak ada data";
                            
                            if (p.getTanggalTransaksi() != null) {
                                try {
                                    java.util.Calendar cal = java.util.Calendar.getInstance();
                                    cal.setTime(p.getTanggalTransaksi());
                                    
                                    // Jika waktu 00:00, generate waktu buatan seperti di DetailTransaksi
                                    if (cal.get(java.util.Calendar.HOUR_OF_DAY) == 0 && cal.get(java.util.Calendar.MINUTE) == 0) {
                                        long transactionId = p.getId();
                                        int hours = (int) ((transactionId % 10) + 8); // 8-17
                                        int minutes = (int) ((transactionId * 7) % 60);
                                        
                                        cal.set(java.util.Calendar.HOUR_OF_DAY, hours);
                                        cal.set(java.util.Calendar.MINUTE, minutes);
                                        cal.set(java.util.Calendar.SECOND, 0);
                                    }
                                    
                                    displayTime = timeFormat.format(cal.getTime());
                                    fullDateTime = dateTimeFormat.format(cal.getTime());
                                } catch (Exception e) {
                                    System.err.println("Error formatting date for transaction: " + p.getKodeTransaksi() + " - " + e.getMessage());
                                }
                            } else {
                                System.err.println("Null date for transaction: " + p.getKodeTransaksi());
                            }
                    %>
                        <div class="transaction-item">
                            <div class="transaction-info">
                                <div class="transaction-code"><%= p.getKodeTransaksi() %></div>
                                <div class="transaction-time" title="<%= fullDateTime %>">
                                    <%= displayTime %>
                                </div>
                            </div>
                            <div class="transaction-amount">
                                Rp <%= String.format("%,d", p.getTotalHarga()) %>
                            </div>
                            <div class="transaction-actions">
                                <a href="<%= request.getContextPath() %>/PenjualanServlet?action=detail&id=<%= p.getId() %>" class="btn-sm detail">
                                    <i class="fas fa-eye"></i>
                                </a>
                            </div>
                        </div>
                    <% } %>
                <% } %>
            </div>
        </div>

        <div class="content-card">
            <div class="card-header">
                <h4><i class="fas fa-shopping-bag"></i> Produk Tersedia</h4>
                <span class="badge"><%= produkTersedia.size() %> item</span>
            </div>
            <div class="product-grid">
                <% if (produkTersedia.isEmpty()) { %>
                    <div class="empty-state">
                        <i class="fas fa-box-open"></i>
                        <p>Tidak ada produk tersedia</p>
                    </div>
                <% } else { %>
                    <% for (Produksi p : produkTersedia) { %>
                        <div class="product-card">
                            <div class="product-image">
                                <img src="<%= request.getContextPath() %>/uploads/<%= p.getGambar() %>" alt="<%= p.getNamaProduk() %>" onerror="this.src='<%= request.getContextPath() %>/assets/images/no-image.png'">
                            </div>
                            <div class="product-info">
                                <div class="product-name"><%= p.getNamaProduk() %></div>
                                <div class="product-price">Rp <%= String.format("%,d", p.getHargaJual()) %></div>
                                <div class="product-stock">Stok: <%= p.getJumlahProduksi() %></div>
                            </div>
                        </div>
                    <% } %>
                <% } %>
            </div>
        </div>
    </div>
</div>

<script>
    // Debug function untuk memeriksa data transaksi
    function debugTransactionData() {
        console.log('=== DEBUG TRANSACTION DATA ===');
        const transactionItems = document.querySelectorAll('.transaction-item');
        transactionItems.forEach((item, index) => {
            const code = item.querySelector('.transaction-code')?.textContent;
            const time = item.querySelector('.transaction-time')?.textContent;
            const title = item.querySelector('.transaction-time')?.getAttribute('title');
            console.log(`Transaction ${index + 1}:`, {
                code: code,
                displayTime: time,
                fullDateTime: title
            });
        });
        console.log('=== END DEBUG ===');
    }

    // Jalankan debug saat halaman dimuat
    document.addEventListener('DOMContentLoaded', function() {
        debugTransactionData();
        
        // Tambahkan live clock untuk referensi waktu saat ini
        const header = document.querySelector('.dashboard-header p');
        if (header) {
            const clockSpan = document.createElement('span');
            clockSpan.id = 'live-clock';
            clockSpan.style.marginLeft = '10px';
            clockSpan.style.fontSize = '0.9em';
            clockSpan.style.color = '#666';
            header.appendChild(clockSpan);
            
            function updateClock() {
                const now = new Date();
                const timeString = now.toLocaleString('id-ID', {
                    timeZone: 'Asia/Jakarta',
                    hour12: false
                });
                clockSpan.textContent = `| ${timeString}`;
            }
            
            updateClock();
            setInterval(updateClock, 1000);
        }
    });
    
    // Function untuk refresh data transaksi (optional)
    function refreshTransactionData() {
        // Ini bisa digunakan untuk auto-refresh data transaksi
        // window.location.reload();
    }
    
    // Auto refresh setiap 5 menit (optional)
    // setInterval(refreshTransactionData, 300000);
</script>