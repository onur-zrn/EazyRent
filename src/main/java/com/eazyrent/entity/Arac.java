package com.eazyrent.entity;

import com.eazyrent.enums.AracDurum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data //@ToString, @EqualsAndHashCode, @Getter, @Setter, @RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_arac")
@NamedQueries({
        @NamedQuery(
                name = "Arac.findByMarkaAndModel",
                query = "SELECT a FROM Arac a WHERE LOWER(a.marka) LIKE LOWER(:marka) AND LOWER(a.model) LIKE LOWER(:model) ORDER BY a.id"
        )
})
public class Arac {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "marka", nullable = false, length = 50)
    private String marka;

    @Column(name = "model", nullable = false, length = 50)
    private String model;

    @Column(name = "plaka", unique = true, nullable = false, length = 15)
    private String plaka;

    @Column(name = "gunluk_fiyat", nullable = false)
    private double gunlukFiyat;

    @Enumerated(EnumType.STRING)
    @Column(name = "arac_durum", nullable = false)
    private AracDurum aracDurum;
}
