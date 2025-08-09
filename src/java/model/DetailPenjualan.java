package model;

public class DetailPenjualan {
    private int id;
    private int penjualanId;
    private int produksiId;
    private int jumlah;
    private int hargaSatuan;
    private int subtotal;
    
    // Untuk join dengan tabel produksi
    private String namaProduk;
    private String gambar;
    
    // Constructor kosong
    public DetailPenjualan() {}
    
    // Constructor untuk insert (tanpa id)
    public DetailPenjualan(int penjualanId, int produksiId, int jumlah, int hargaSatuan, int subtotal) {
        this.penjualanId = penjualanId;
        this.produksiId = produksiId;
        this.jumlah = jumlah;
        this.hargaSatuan = hargaSatuan;
        this.subtotal = subtotal;
    }
    
    // Constructor lengkap
    public DetailPenjualan(int id, int penjualanId, int produksiId, int jumlah, int hargaSatuan, int subtotal) {
        this.id = id;
        this.penjualanId = penjualanId;
        this.produksiId = produksiId;
        this.jumlah = jumlah;
        this.hargaSatuan = hargaSatuan;
        this.subtotal = subtotal;
    }
    
    // Constructor dengan nama produk (untuk join)
    public DetailPenjualan(int id, int penjualanId, int produksiId, int jumlah, int hargaSatuan, 
                          int subtotal, String namaProduk, String gambar) {
        this.id = id;
        this.penjualanId = penjualanId;
        this.produksiId = produksiId;
        this.jumlah = jumlah;
        this.hargaSatuan = hargaSatuan;
        this.subtotal = subtotal;
        this.namaProduk = namaProduk;
        this.gambar = gambar;
    }
    
    // Getters dan Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getPenjualanId() {
        return penjualanId;
    }
    
    public void setPenjualanId(int penjualanId) {
        this.penjualanId = penjualanId;
    }
    
    public int getProduksiId() {
        return produksiId;
    }
    
    public void setProduksiId(int produksiId) {
        this.produksiId = produksiId;
    }
    
    public int getJumlah() {
        return jumlah;
    }
    
    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }
    
    public int getHargaSatuan() {
        return hargaSatuan;
    }
    
    public void setHargaSatuan(int hargaSatuan) {
        this.hargaSatuan = hargaSatuan;
    }
    
    public int getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(int subtotal) {
        this.subtotal = subtotal;
    }
    
    public String getNamaProduk() {
        return namaProduk;
    }
    
    public void setNamaProduk(String namaProduk) {
        this.namaProduk = namaProduk;
    }
    
    public String getGambar() {
        return gambar;
    }
    
    public void setGambar(String gambar) {
        this.gambar = gambar;
    }
    
    @Override
    public String toString() {
        return "DetailPenjualan{" +
                "id=" + id +
                ", penjualanId=" + penjualanId +
                ", produksiId=" + produksiId +
                ", jumlah=" + jumlah +
                ", hargaSatuan=" + hargaSatuan +
                ", subtotal=" + subtotal +
                ", namaProduk='" + namaProduk + '\'' +
                ", gambar='" + gambar + '\'' +
                '}';
    }
}