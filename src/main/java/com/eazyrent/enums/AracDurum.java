package com.eazyrent.enums;

public enum AracDurum {
    KIRADA("Kirada"), // Araç şu anda kiralanmış durumda
    MUSAIT("Müsait"), // Araç kiralamaya hazır ve boşta
    BAKIMDA("Bakımda"); // Araç bakımda olduğu için kiralanamaz

    private final String durum;

    AracDurum(String durum) {
        this.durum = durum;
    }

    // Durum adını döndüren metot
    public String getDurum() {
        return durum;
    }
}