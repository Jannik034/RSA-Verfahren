package com.example.rsaverfahren;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Arrays;


public class Controller {

    @FXML private Slider sliderP, sliderQ;
    @FXML private Label labelP, labelQ, labelN, labelE;
    private boolean isUpdating = false;

    @FXML private void initialize() {
        int maxPrimePossible = 10000;
        int[] primes = primeGenerator(maxPrimePossible);

        if (sliderP.getValue() == sliderQ.getValue()) {
            sliderQ.setValue(1);
        }

        mapPrimesOnSlider(primes, sliderP, labelP);
        mapPrimesOnSlider(primes, sliderQ, labelQ);
        calcE(primes[(int) Math.round(sliderP.getValue())],primes[(int) Math.round(sliderQ.getValue())],labelE);
        calcN(primes[(int) Math.round(sliderP.getValue())],primes[(int) Math.round(sliderQ.getValue())],labelN);


        sliderP.valueProperty().addListener((obs, o, n) -> {
            if(isUpdating) return;

            mapPrimesOnSlider(primes, sliderP, labelP);
            calcN(primes[(int) Math.round(sliderP.getValue())],primes[(int) Math.round(sliderQ.getValue())],labelN);
            calcE(primes[(int) Math.round(sliderP.getValue())],primes[(int) Math.round(sliderQ.getValue())],labelE);});

        sliderQ.valueProperty().addListener((obs, o, n) -> {
            if(isUpdating) return;

            mapPrimesOnSlider(primes, sliderQ, labelQ);
            calcN(primes[(int) Math.round(sliderP.getValue())],primes[(int) Math.round(sliderQ.getValue())],labelN);
            calcE(primes[(int) Math.round(sliderP.getValue())],primes[(int) Math.round(sliderQ.getValue())],labelE);});
    }

    /**
     * Mappt die Primzahlen des Arrays int[] primes auf den Slider und zeigt den aktuellen Wert des Sliders als Label an.
     * @param primes Primzahlarray
     * @param slider zu mappender Slider
     * @param label Anzeigelabel
     */
    @FXML private void mapPrimesOnSlider(int[] primes, Slider slider, Label label) {
        slider.setMin(0);
        slider.setMax(primes.length -1);
        slider.setMinorTickCount(0);
        slider.setBlockIncrement(1);
        slider.setShowTickLabels(false);
        slider.setShowTickMarks(false);

        Slider otherSlider = (slider == sliderP) ? sliderQ : sliderP;

        int currentIndex = (int) Math.round(slider.getValue());
        int otherIndex = (int) Math.round(otherSlider.getValue());

        if (currentIndex == otherIndex) {
            if (currentIndex + 1 < primes.length) {
                currentIndex++;
            } else {
                currentIndex--;
            }

            isUpdating = true;
            slider.setValue(currentIndex);
            isUpdating = false;
        }

        int p = primes[(int) Math.round(slider.getValue())];
        label.setText(""+p);
    }

    /** Errechnet den Wert N aus q und p. Zeigt N dann als Label an.
     * @param p Primzahl
     * @param q Primzahl
     * @param label Ausgabelabel
     */
    @FXML private void calcN(int p, int q, Label label) {
        label.setText("N =" + (p*q));
    }

    /**
     * Errechnet das kleinste e für das ggT(e, (p-1)*(q-1)) == 1; e also zu (q-1)*(p-1) teilerfremd ist.
     * @param p Primzahl
     * @param q Primzahl
     * @param label Anzeigelabel
     */
    @FXML private void calcE(int p, int q, Label label) {
        int phi = (p-1) * (q-1);
        int e = 3;

        while (ggT(e,phi) != 1) {
            e += 2;
        }

        label.setText("e = " + e);
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