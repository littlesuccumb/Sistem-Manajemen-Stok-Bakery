package model;

import java.util.Date;

public class Penjualan {
    private int id;
    private String kodeTransaksi;
    private Date tanggalTransaksi;
    private int totalHarga;
    private String metodePembayaran;
    private int kasirId;
    
    // Untuk join dengan tabel users
    private String namaKasir;
    
    // Constructor kosong
    public Penjualan() {}
    
    // Constructor tanpa id
    public Penjualan(String kodeTransaksi, Date tanggalTransaksi, int totalHarga, String metodePembayaran, int kasirId) {
        this.kodeTransaksi = kodeTransaksi;
        this.tanggalTransaksi = tanggalTransaksi;
        this.totalHarga = totalHarga;
        this.metodePembayaran = metodePembayaran;
        this.kasirId = kasirId;
    }
    
    // Constructor lengkap
    public Penjualan(int id, String kodeTransaksi, Date tanggalTransaksi, int totalHarga, String metodePembayaran, int kasirId) {
        this.id = id;
        this.kodeTransaksi = kodeTransaksi;
        this.tanggalTransaksi = tanggalTransaksi;
        this.totalHarga = totalHarga;
        this.metodePembayaran = metodePembayaran;
        this.kasirId = kasirId;
    }
    
    // Constructor dengan nama kasir (untuk join)
    public Penjualan(int id, String kodeTransaksi, Date tanggalTransaksi, int totalHarga, String metodePembayaran, int kasirId, String namaKasir) {
        this.id = id;
        this.kodeTransaksi = kodeTransaksi;
        this.tanggalTransaksi = tanggalTransaksi;
        this.totalHarga = totalHarga;
        this.metodePembayaran = metodePembayaran;
        this.kasirId = kasirId;
        this.namaKasir = namaKasir;
    }
    
    // Getter dan Setter
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getKodeTransaksi() {
        return kodeTransaksi;
    }
    
    public void setKodeTransaksi(String kodeTransaksi) {
        this.kodeTransaksi = kodeTransaksi;
    }
    
    public Date getTanggalTransaksi() {
        return tanggalTransaksi;
    }
    
    public void setTanggalTransaksi(Date tanggalTransaksi) {
        this.tanggalTransaksi = tanggalTransaksi;
    }
    
    public int getTotalHarga() {
        return totalHarga;
    }
    
    public void setTotalHarga(int totalHarga) {
        this.totalHarga = totalHarga;
    }
    
    public String getMetodePembayaran() {
        return metodePembayaran;
    }
    
    public void setMetodePembayaran(String metodePembayaran) {
        this.metodePembayaran = metodePembayaran;
    }
    
    public int getKasirId() {
        return kasirId;
    }
    
    public void setKasirId(int kasirId) {
        this.kasirId = kasirId;
    }
    
    public String getNamaKasir() {
        return namaKasir;
    }
    
    public void setNamaKasir(String namaKasir) {
        this.namaKasir = namaKasir;
    }
}