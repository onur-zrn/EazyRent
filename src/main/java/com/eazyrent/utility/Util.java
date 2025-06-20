package com.eazyrent.utility;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Pattern;

import static com.eazyrent.constants.patterns.*;


public class Util {
    //region Renkler
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    private static Scanner scanner = new Scanner(System.in);

    //endregion


    //region DegerAlma
    public static int intDegerAl(String mesaj, int min, int max) {
        int deger = 0;
        boolean gecerliDeger = false;

        while (!gecerliDeger) {
            try {
                writeMenuItemBlue(mesaj);
                deger = scanner.nextInt();
                scanner.nextLine();

                if (deger >= min && deger <= max) {
                    gecerliDeger = true;
                } else {
                    System.out.println("Lütfen " + min + " ile " + max + " arasında bir değer giriniz.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Lütfen geçerli bir sayı giriniz.");
                scanner.nextLine();
            }
        }

        return deger;
    }

    public static int intDegerAl(String message) {
        while (true) {
            try {
                System.out.print(message);
                int value = scanner.nextInt();
                scanner.nextLine();
                return value;
            } catch (Exception e) {
                System.out.println("Lütfen geçerli bir sayı giriniz.");
                scanner.nextLine();
            }
        }
    }

    public static LocalTime localTimeDegerAl(String message) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm"); // Saat formatı
        while (true) {
            try {
                System.out.print(message);
                String timeString = scanner.nextLine();
                LocalTime time = LocalTime.parse(timeString, formatter); // Formatla geçerli LocalTime'a dönüştür
                return time;
            } catch (Exception e) {
                System.out.println("Lütfen geçerli bir saat giriniz (HH:mm).");
            }
        }
    }

    public static LocalDate localDateDegerAl(String message) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Tarih formatı
        while (true) {
            try {
                System.out.print(message);
                String dateString = scanner.nextLine();
                LocalDate date = LocalDate.parse(dateString, formatter); // Formatla geçerli LocalDate'a dönüştür
                return date;
            } catch (Exception e) {
                System.out.println("Lütfen geçerli bir tarih giriniz (yyyy-MM-dd).");
            }
        }
    }

    public static double doubleDegerAl(String message) {
        while (true) {
            try {
                System.out.print(message);
                double value = scanner.nextDouble();
                scanner.nextLine();
                return value;
            } catch (Exception e) {
                System.out.println("Lütfen geçerli bir sayı giriniz.");
                scanner.nextLine();
            }
        }
    }

    // YENİ EKLENEN: Boş bırakılabilir string değer alımı
    public static String stringDegerAl(String mesaj, boolean allowEmpty) {
        writeMenuItemBlue(mesaj);
        String deger = scanner.nextLine().trim();
        if (allowEmpty || !deger.isEmpty()) {
            return deger;
        } else {
            printRed("Boş değer girilemez. Lütfen tekrar deneyin.");
            return stringDegerAl(mesaj, allowEmpty); // Tekrar sor
        }
    }

    // YENİ EKLENEN: Boş bırakılabilir double değer alımı
    public static double doubleDegerAl(String message, boolean allowEmpty) {
        while (true) {
            try {
                System.out.print(message);
                String input = scanner.nextLine().trim();
                if (allowEmpty && input.isEmpty()) {
                    return -1.0; // -1.0, kullanıcının değeri değiştirmediğini işaret eder
                }
                double value = Double.parseDouble(input);
                return value;
            } catch (NumberFormatException e) {
                printRed("Lütfen geçerli bir sayı giriniz veya boş bırakın.");
            }
        }
    }

    public static String stringDegerAl(String mesaj) {
        String deger = "";
        boolean gecerliDeger = false;

        while (!gecerliDeger) {
            writeMenuItemBlue(mesaj);
            deger = scanner.nextLine().trim();

            if (!deger.isEmpty()) {
                gecerliDeger = true;
            } else {
                System.out.println("Boş değer girilemez. Lütfen tekrar deneyin.");
            }
        }

        return deger;
    }

    public static String eMailDegerAl(String mesaj) {
        String email = "";
        boolean gecerliEmail = false;

        while (!gecerliEmail) {
            email = stringDegerAl(mesaj);
            Pattern pattern = Pattern.compile(VALID_EMAIL_ADDRESS_REGEX, Pattern.CASE_INSENSITIVE);

            if (pattern.matcher(email).matches()) {
                gecerliEmail = true;
            } else {
                System.out.println("Geçersiz e-posta adresi. Lütfen geçerli bir e-posta girin (Örnek: example@example.com).");
            }
        }

        return email; // Geçerli e-posta adresi döner
    }

    // YENİ EKLENEN: Boş bırakılabilir e-posta değeri alımı
    public static String eMailDegerAl(String mesaj, boolean allowEmpty) {
        String email = "";
        boolean gecerliEmail = false;
        while (!gecerliEmail) {
            email = stringDegerAl(mesaj, true); // Boş bırakmaya izin ver
            if (email.isEmpty() && allowEmpty) {
                return null; // Eğer boş bırakıldıysa ve izin veriliyorsa null dön
            }
            // Normal e-posta validasyonu
            Pattern pattern = Pattern.compile(VALID_EMAIL_ADDRESS_REGEX, Pattern.CASE_INSENSITIVE);
            if (pattern.matcher(email).matches()) {
                gecerliEmail = true;
            } else {
                printRed("Geçersiz e-posta adresi. Örnek: example@example.com. Boş bırakmak için Enter'a basın.");
            }
        }
        return email;
    }

    public static String telefonDegerAl(String mesaj) {
        String telefon = "";
        boolean gecerliTelefon = false;

        while (!gecerliTelefon) {
            telefon = stringDegerAl(mesaj); // Kullanıcıdan telefon numarasını al
            Pattern pattern = Pattern.compile(VALID_TELEFON_REGEX, Pattern.CASE_INSENSITIVE);

            if (pattern.matcher(telefon).matches()) {
                gecerliTelefon = true; // Geçerli telefon numarası ise döngü sonlanır
            } else {
                System.out.println("Geçersiz telefon numarası. Örnek: 5555555555 veya 3125555555.");
            }
        }

        return telefon; // Geçerli telefon numarası döner
    }


    // YENİ EKLENEN: Boş bırakılabilir telefon değeri alımı
    public static String telefonDegerAl(String mesaj, boolean allowEmpty) {
        String telefon = "";
        boolean gecerliTelefon = false;
        while (!gecerliTelefon) {
            telefon = stringDegerAl(mesaj, true); // Boş bırakmaya izin ver
            if (telefon.isEmpty() && allowEmpty) {
                return null; // Eğer boş bırakıldıysa ve izin veriliyorsa null dön
            }
            // Normal telefon validasyonu
            Pattern pattern = Pattern.compile(VALID_TELEFON_REGEX, Pattern.CASE_INSENSITIVE);
            if (pattern.matcher(telefon).matches()) {
                gecerliTelefon = true;
            } else {
                printRed("Geçersiz telefon numarası. Örnek: 5555555555 veya 3125555555. Boş bırakmak için Enter'a basın.");
            }
        }
        return telefon;
    }


    public static String tcKimlikNoDegerAl(String mesaj) {
        String tcKimlikNo = "";
        boolean gecerliTcKimlik = false;

        while (!gecerliTcKimlik) {
            tcKimlikNo = stringDegerAl(mesaj); // Kullanıcıdan TC Kimlik No'yu al
            Pattern pattern = Pattern.compile(VALID_TC_KIMLIK_REGEX);

            if (pattern.matcher(tcKimlikNo).matches()) {
                gecerliTcKimlik = true;
            } else {
                System.out.println("Geçersiz TC Kimlik No. Lütfen 11 haneli ve sadece rakamlardan oluşan bir TC Kimlik No girin.");
            }
        }
        return tcKimlikNo;
    }

    public static String tcKimlikNoDegerAl(String mesaj, boolean allowEmpty) {
        String tcKimlikNo = "";
        boolean gecerliTcKimlik = false;

        while (!gecerliTcKimlik) {
            tcKimlikNo = stringDegerAl(mesaj, true); // Boş bırakmaya izin ver

            if (tcKimlikNo.isEmpty() && allowEmpty) {
                return null; // Eğer boş bırakıldıysa ve izin veriliyorsa null dön
            }

            // TC Kimlik No doğrulaması
            Pattern pattern = Pattern.compile(VALID_TC_KIMLIK_REGEX);

            if (pattern.matcher(tcKimlikNo).matches()) {
                gecerliTcKimlik = true;
            } else {
                printRed("Geçersiz TC Kimlik No. 11 haneli ve sadece rakamlardan oluşan bir değer girin. Boş bırakmak için Enter'a basın.");
            }
        }

        return tcKimlikNo;
    }
    //endregion

    //region Renkli Yazdır
    public static void writeMenuItem(String item) {
        System.out.println(ANSI_GREEN + item + ANSI_RESET);
    }

    public static void writeMenuItemRed(String item) {
        System.out.println(ANSI_RED + item + ANSI_RESET);
    }

    public static void writeMenuItemBlue(String item) {
        System.out.print(ANSI_BLUE + item + ANSI_RESET);
    }

    public static void printInColor(String text, String color) {
        System.out.println(color + text + ANSI_RESET);
    }

    public static void printColor(String text, String color) {
        System.out.println(color + text + ANSI_RESET);
    }

    public static void printGreen(String text) {
        System.out.println(ANSI_GREEN + text + ANSI_RESET);
    }

    public static void printRed(String text) {
        System.out.println(ANSI_RED + text + ANSI_RESET);
    }

    public static void printBlue(String text) {
        System.out.println(ANSI_BLUE + text + ANSI_RESET);
    }

    public static void printYellow(String text) {
        System.out.println(ANSI_YELLOW + text + ANSI_RESET);
    }

    public static void printPurple(String text) {
        System.out.println(ANSI_PURPLE + text + ANSI_RESET);
    }

    public static void printCyan(String text) {
        System.out.println(ANSI_CYAN + text + ANSI_RESET);
    }

    public static void printBlack(String text) {
        System.out.println(ANSI_BLACK + text + ANSI_RESET);
    }

    //endregion

    public static void baslikYazdir(String baslik) {
        int satirUzunlugu = 50; // Daha uzun bir satır uzunluğu
        String yildiz = "*";

        // Başlık uzunluğu kontrolleri
        int baslikUzunlugu = baslik.length();

        // Eğer başlık satır uzunluğundan uzunsa kısalt
        if (baslikUzunlugu > satirUzunlugu - 4) { //
            baslik = baslik.substring(0, satirUzunlugu - 7) + "...";
            baslikUzunlugu = baslik.length();
        }

        String ustSatir = yildiz.repeat(satirUzunlugu);
        System.out.println(ANSI_YELLOW + ustSatir);

        int bosluklarToplami = satirUzunlugu - baslikUzunlugu - 2; // 2 tane * için

        // Negatif değer kontrolü
        if (bosluklarToplami < 0) {
            bosluklarToplami = 0;
        }

        // Tek/çift durumu kontrolü
        int solBosluk = bosluklarToplami / 2;
        int sagBosluk = bosluklarToplami - solBosluk;

        String baslikSatiri = yildiz +
                (solBosluk > 0 ? " ".repeat(solBosluk) : "") +
                baslik +
                (sagBosluk > 0 ? " ".repeat(sagBosluk) : "") +
                yildiz;

        System.out.println(baslikSatiri);

        // Alt satır
        System.out.println(ustSatir + ANSI_RESET);
    }

    public static void enteraBas() {
        System.out.println(ANSI_YELLOW + "Ana menü için [Enter] basınız." + ANSI_RESET); // Renk eklendi
        scanner.nextLine();
    }

    public static void printImage(String imagePath) {
        try {
            // Resim dosyasını okuma
            System.out.println("Geçerli çalışma dizini: " + System.getProperty("user.dir"));

            File file = new File("src\\RestoranRezervasyonSistemi\\util\\Image\\" + imagePath);
            Scanner scanner = new Scanner(file);

            // Resmi satır satır okuma ve terminale yazdırma
            while (scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());
            }

            scanner.close();
        } catch (IOException e) {
            System.out.println("Resim dosyası okunamadı: " + e.getMessage());
        }
    }
}