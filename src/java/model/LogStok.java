package model;

import java.util.Date;

public class LogStok {
    private int id, resepId, bahanId, jumlahDikurangi;
    private String namaBahan, keterangan;
    private Date tanggal;

    // Constructor + Getter/Setter
    public LogStok(int id, int resepId, int bahanId, String namaBahan, int jumlahDikurangi, String keterangan, Date tanggal) {
        this.id = id;
        this.resepId = resepId;
        this.bahanId = bahanId;
        this.namaBahan = namaBahan;
        this.jumlahDikurangi = jumlahDikurangi;
        this.keterangan = keterangan;
        this.tanggal = tanggal;
    }

    public int getId() { return id; }
    public int getResepId() { return resepId; }
    public int getBahanId() { return bahanId; }
    public String getNamaBahan() { return namaBahan; }
    public int getJumlahDikurangi() { return jumlahDikurangi; }
    public String getKeterangan() { return keterangan; }
    public Date getTanggal() { return tanggal; }
}

