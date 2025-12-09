package com.example.todo.model;

import androidx.room.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity(tableName = "BadanieTable")
public class Badanie implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Id")
    private int id;

    @ColumnInfo(name = "Nazwa")
    private String nazwa;

    @ColumnInfo(name = "DataOstatniegoBadania")
    private Date dataOstatniegoBadania;

    @ColumnInfo(name = "OkresWaznosciDni")
    private int okresWaznosciDni;

    @ColumnInfo(name = "Notatki")
    private String notatki;

    @ColumnInfo(name = "CreateTime")
    private Date createTime;

    @ColumnInfo(name = "ModifyTime")
    private Date modifyTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    public Date getDataOstatniegoBadania() {
        return dataOstatniegoBadania;
    }

    public void setDataOstatniegoBadania(Date dataOstatniegoBadania) {
        this.dataOstatniegoBadania = dataOstatniegoBadania;
    }

    public int getOkresWaznosciDni() {
        return okresWaznosciDni;
    }

    public void setOkresWaznosciDni(int okresWaznosciDni) {
        this.okresWaznosciDni = okresWaznosciDni;
    }

    public String getNotatki() {
        return notatki;
    }

    public void setNotatki(String notatki) {
        this.notatki = notatki;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    // Oblicza datę wygaśnięcia ważności
    public Date getDataWygasniecia() {
        if (dataOstatniegoBadania == null) {
            return null;
        }
        long timeInMillis = dataOstatniegoBadania.getTime() + (okresWaznosciDni * 24L * 60 * 60 * 1000);
        return new Date(timeInMillis);
    }

    // Sprawdza czy badanie jest jeszcze ważne
    public boolean isWazne() {
        Date dataWygasniecia = getDataWygasniecia();
        if (dataWygasniecia == null) {
            return false;
        }
        return dataWygasniecia.after(new Date());
    }

    // Oblicza liczbę dni do wygaśnięcia (ujemna jeśli po terminie)
    public long getDniDoWygasniecia() {
        Date dataWygasniecia = getDataWygasniecia();
        if (dataWygasniecia == null) {
            return 0;
        }
        long diff = dataWygasniecia.getTime() - new Date().getTime();
        return diff / (24 * 60 * 60 * 1000);
    }

    // Sprawdza czy badanie jest "wkrótce" (w ciągu 30 dni)
    public boolean isWkrotce() {
        long dni = getDniDoWygasniecia();
        return dni > 0 && dni <= 30;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Badanie badanie = (Badanie) obj;
        return id == badanie.id &&
                okresWaznosciDni == badanie.okresWaznosciDni &&
                Objects.equals(nazwa, badanie.nazwa) &&
                Objects.equals(dataOstatniegoBadania, badanie.dataOstatniegoBadania) &&
                Objects.equals(notatki, badanie.notatki) &&
                Objects.equals(createTime, badanie.createTime) &&
                Objects.equals(modifyTime, badanie.modifyTime);
    }
}

