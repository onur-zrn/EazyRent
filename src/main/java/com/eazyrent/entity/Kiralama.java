package com.eazyrent.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data //@ToString, @EqualsAndHashCode, @Getter, @Setter, @RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_kiralama")
public class Kiralama {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "arac_id", nullable = false)
    private Long aracId;

    @Column(name = "kisi_id", nullable = false)
    private Long kisiId;

    @Column(name = "kiralama_tarihi", nullable = false)
    private LocalDate kiralamaTarihi;

    @Column(name = "teslim_tarihi") // Teslim tarihi başlangıçta boş (null) olabilir
    private LocalDate teslimTarihi;

    @Column(name = "toplam_tutar") // Toplam tutar, araç teslim edildiğinde hesaplanacak
    private Double toplamTutar; //null olabilir
}
