package com.example.rsaverfahren;
import javafx.fxml.FXML;
import java.math.BigInteger;

public class RSAService {

    /**
     * Verschlüsselt den plaintext nach der Formel ciphertext = (plaintext^e) % N.
     * @param plaintext zu verschlüsselndes Wort (int)
     * @param e Teil des öffentlichen Schlüssels
     * @param N RSA-Modul
     * @return verschlüsselter text (int)
     */
    public static int encrypt(int plaintext, int e, int N) {

        //Konvertierung der gegebenen Parameter in BigInteger Objekte
        BigInteger BIPlaintext = BigInteger.valueOf(plaintext);
        BigInteger BIE = BigInteger.valueOf(e);
        BigInteger BIN = BigInteger.valueOf(N);

        /*
        1. Berechnet plaintext^e % N der BigInteger Objekte mit der Methode modPow.
        2. Wandelt das BigInteger Ergebnis in int zurück
        3. Gibt den ciphertext als int aus.
         */
        return BIPlaintext.modPow(BIE, BIN).intValue();
    }

    /**
     * Entschlüsselt den ciphertext nach der Formel plaintext = (ciphertext^d) % N;
     * @param ciphertext zu entschlüsselndes Wort (int)
     * @param d Teil des privaten Schlüssels
     * @param N RSA-Modul
     * @return entschlüsselter Text (int)
     */
    public static int decrypt(int ciphertext, int d, int N) {

        //Konvertierung der gegebenen Parameter in BigInteger Objekte
        BigInteger BICiphertext = BigInteger.valueOf(ciphertext);
        BigInteger BID = BigInteger.valueOf(d);
        BigInteger BIN = BigInteger.valueOf(N);

        /*
        1. Berechnet ciphertext^d % N der BigInteger Objekte mit der Methode modPow.
        2. Wandelt das BigInteger Ergebnis in int zurück
        3. Gibt den plaintext als int aus.
         */
        return BICiphertext.modPow(BID, BIN).intValue();
    }

    /** Errechnet den Wert N aus q und p.
     * @param p Primzahl
     * @param q Primzahl
     * @return N, Produkt aus q und p
     */
    static int calcN(int p, int q) {
        return p*q;
    }

    /**
     * Errechnet das kleinste e für das ggT(e, (p-1)*(q-1)) == 1 gilt; e also zu (q-1)*(p-1) teilerfremd ist.
     * @param p Primzahl
     * @param q Primzahl
     * @return e
     */
    static int calcE(int p, int q) {
        //Startwerte festlegen
        int phi = (p-1) * (q-1);
        int e = 3;
        //Kleinstes e finden, für dass ggT(e,phi) == 1 gilt.
        while (ggT(e,phi) != 1) {
            //ungerade Zahlen für e testen.
            e += 2;
        }
        return e;
    }

    /**
     * Berechnet den privaten Exponenten d für das RSA-Verfahren aus den Primzahlen p und q sowie e
     * @param p Primzahl
     * @param q Primzahl
     * @param e kleinstes e für das ggT(e, (p-1)*(q-1)) == 1 gilt.
     * @return d
     */
    static int calcD(int p, int q, int e) {
        /*
            Es muss gelten: (e * d) % phi = 1
            Daraus wird: e * d = k * phi + 1           Denn e * d muss genau 1 größer als ein Vielfaches von phi sein.
            Umgestellt: d = (k * phi + 1) / e
         */

        int phi = (p-1)*(q-1);
        int k = 1;

        //Solange der rechte Teil der umgestellten Gleichung nicht gleich 0 ist, wird k erhöht. Denn d = 0 kann die Gleichung nicht erfüllen.
        while ((k*phi+1) % e != 0) {
            k++;
        }

        //d zurückgeben
        return (k*phi+1)/e;
    }

    /** Siebt alle Primzahlen einer Zahlenreihe bis zum Parameter max heraus.
     * @param max Maximaler Wert einer möglichen Primzahl
     * @return Array aller Primzahlen bis max
     */
    static int[] primeGenerator(int max) {

        //Array für alle Vielfache einer Primzahl von 0 bis max
        boolean[] composite = new boolean[max + 1];
        //Zähler für Anzahl der gefundenen Primzahlen
        int count = 0;

        /*
        Schleife:
        Beginn bei i = 2 (kleinste Primzahl)
        Bedingung i*i <= max, weil jede Zahl < max mindestens einen Faktor hat der kleiner oder gleich der Wurzel von der Zahl ist. Somit ist jede Zahl bis max abgedeckt.
         */
        for(int i = 2; i*i <= max; i++) {
            // Wenn composite[i] = false ist, dann ist i eine Primzahl.
            if(!composite[i]) {

                //Alle Vielfache von i als composite markieren.
                for(int j = i*i; j <= max; j += i) {
                    composite[j] = true;
                }
            }
        }

        //Alle gefundenen Primzahlen mit composite[i] = false zählen.
        for (int i = 2; i <= max; i++) {
            if (!composite[i]) { count++;}
        }

        //Neues Array mit der Länge count (Anzahl der Primzahlen) erstellen
        int[] primes = new int[count];

        //Jede Zahl i im composite-Array die auf false steht an der Stelle k ins primes-Array übertragen.
        for (int i = 2, k = 0; i <= max; i++) {
            if (!composite[i]) primes[k++] = i;
        }

        return primes;
    }

    /**
     * Errechnet den ggT der Integer a und b nach dem Euklidischen Algorithmus
     * @param a natürliche Zahl
     * @param b natürliche Zahl
     * @return ggT von a und b
     */
    static int ggT(int a, int b) {
        //Solange b ungleich 0 ist, also noch kein Teiler für b ohne Rest gefunden wurde:
        while (b != 0) {
            //aktueller Wert von b, also der Rest der Division a / b aus dem letzten Durchgang, wird zwischengespeichert.
            int temp = b;
            //b wird dem Rest der Division a / b gesetzt.
            b = a % b;
            //a übernimmt den temp-Wert, also den ehemaligen b Wert.
            a = temp;
        }
        // Somit wird immer von dem jeweils größeren Wert a oder b der andere so oft abgezogen bis kein Rest mehr bleibt, die Schleifenbedingung b != 0 nicht mehr erfüllt ist und die Schleife abbricht.
        return a;
    }
}
