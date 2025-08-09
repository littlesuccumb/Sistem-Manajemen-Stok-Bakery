package model;

import java.util.Date;

public class Produksi {
    private int id;
    private int resepId;
    private String namaProduk;
    private int jumlahProduksi;
    private int hargaJual;
    private Date tanggalProduksi;
    private String status;
    private String gambar;
    private String namaResep; 
    
    // Constructor kosong
    public Produksi() {}
    
    // Constructor tanpa id (untuk insert)
    public Produksi(int resepId, String namaProduk, int jumlahProduksi, int hargaJual, 
                   Date tanggalProduksi, String status, String gambar) {
        this.resepId = resepId;
        this.namaProduk = namaProduk;
        this.jumlahProduksi = jumlahProduksi;
        this.hargaJual = hargaJual;
        this.tanggalProduksi = tanggalProduksi;
        this.status = status;
        this.gambar = gambar;
    }
    
    // Constructor lengkap (untuk select dengan gambar)
    public Produksi(int id, int resepId, String namaProduk, int jumlahProduksi, int hargaJual, 
                   Date tanggalProduksi, String status, String gambar) {
        this.id = id;
        this.resepId = resepId;
        this.namaProduk = namaProduk;
        this.jumlahProduksi = jumlahProduksi;
        this.hargaJual = hargaJual;
        this.tanggalProduksi = tanggalProduksi;
        this.status = status;
        this.gambar = gambar;
    }
    
    // Getters dan Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getResepId() {
        return resepId;
    }
    
    public void setResepId(int resepId) {
        this.resepId = resepId;
    }
    
    public String getNamaProduk() {
        return namaProduk;
    }
    
    public void setNamaProduk(String namaProduk) {
        this.namaProduk = namaProduk;
    }
    
    public int getJumlahProduksi() {
        return jumlahProduksi;
    }
    
    public void setJumlahProduksi(int jumlahProduksi) {
        this.jumlahProduksi = jumlahProduksi;
    }
    
    public int getHargaJual() {
        return hargaJual;
    }
    
    public void setHargaJual(int hargaJual) {
        this.hargaJual = hargaJual;
    }
    
    public Date getTanggalProduksi() {
        return tanggalProduksi;
    }
    
    public void setTanggalProduksi(Date tanggalProduksi) {
        this.tanggalProduksi = tanggalProduksi;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    // Getter dan Setter untuk gambar
    public String getGambar() {
        return gambar;
    }
    
    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getNamaResep() {
        return namaResep;
    }
    
    public void setNamaResep(String namaResep) {
        this.namaResep = namaResep;
    }
    
    @Override
    public String toString() {
        return "Produksi{" +
                "id=" + id +
                ", resepId=" + resepId +
                ", namaProduk='" + namaProduk + '\'' +
                ", jumlahProduksi=" + jumlahProduksi +
                ", hargaJual=" + hargaJual +
                ", tanggalProduksi=" + tanggalProduksi +
                ", status='" + status + '\'' +
                ", gambar='" + gambar + '\'' +
                ", namaResep='" + namaResep + '\'' +
                '}';
    }
}