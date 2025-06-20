package com.eazyrent.constants;

public class patterns {
    // E-posta formatı için regex deseni
    public static final String VALID_EMAIL_ADDRESS_REGEX = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
    // Telefon Numarası için regex deseni
    public static final String VALID_TELEFON_REGEX = "^\\d{10}$";
    // TC Kimlik Numarası için regex deseni (tam 11 rakam)
    public static final String VALID_TC_KIMLIK_REGEX = "^\\d{11}$";
    //En az 1 büyük harf, 1 küçük harf, 1 rakam, 1 özel karakter olmalı ve en az 8 karakter olmalıdır.
    public static final String VALID_PASSWORD_REGEX =
            "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
}
