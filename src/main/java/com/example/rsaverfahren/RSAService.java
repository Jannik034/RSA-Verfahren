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
        BigInteger m = BigInteger.valueOf(plaintext);
        BigInteger expE = BigInteger.valueOf(e);
        BigInteger modN = BigInteger.valueOf(N);

        return m.modPow(expE, modN).intValue();
    }

    /**
     * Entschlüsselt den ciphertext nach der Formel plaintext = (ciphertext^d) % N;
     * @param ciphertext zu entschlüsselndes Wort (int)
     * @param d Teil des privaten Schlüssels
     * @param N RSA-Modul
     * @return entschlüsselter Text (int)
     */
    public static int decrypt(int ciphertext, int d, int N) {
        BigInteger c = BigInteger.valueOf(ciphertext);
        BigInteger expD = BigInteger.valueOf(d);
        BigInteger modN = BigInteger.valueOf(N);

        return c.modPow(expD, modN).intValue();
    }

    /** Errechnet den Wert N aus q und p. Zeigt N dann als Label an.
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
        int phi = (p-1) * (q-1);
        int e = 3;

        while (RSAService.ggT(e,phi) != 1) {
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
        int phi = (p-1)*(q-1);
        int k = 1;

        while ((k*phi+1) % e != 0) {
            k++;
        }

        return (k*phi+1)/e;
    }

    /** Siebt alle Primzahlen einer Zahlenreihe bis zum Parameter max heraus.
     * @param max Maximaler Wert einer möglichen Primzahl
     * @return Array aller Primzahlen bis max
     */
    static int[] primeGenerator(int max) {
        boolean[] composite = new boolean[max + 1];
        int count = 0;

        for(int i = 2; i*i <= max; i++) {
            if(!composite[i]) {
                for(int j = i*i; j <= max; j += i) {
                    composite[j] = true;
                }
            }
        }

        for (int i = 2; i <= max; i++) {
            if (!composite[i]) { count++;}
        }

        int[] primes = new int[count];
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
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }
}
