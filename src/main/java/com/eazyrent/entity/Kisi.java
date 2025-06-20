package com.eazyrent.entity;

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
@Table(name = "tbl_kisi")
public class Kisi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ad", nullable = false, length = 50)
    private String ad;

    @Column(name = "soyad", nullable = false, length = 50)
    private String soyad;

    @Column(name = "tc_kimlik_no", unique = true, nullable = false, length = 11)
    private String tcKimlikNo;

    @Column(name = "telefon", length = 20)
    private String telefon;

    @Column(name = "email", length = 100)
    private String email;
}
