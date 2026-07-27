package com.example.rsaverfahren;

import javafx.fxml.FXML;
import javafx.scene.control.*;


public class Controller {

    @FXML private Slider sliderP, sliderQ;
    @FXML private Label labelP, labelQ, labelN;


    @FXML private void initialize() {
        int maxPrimePossible = 1000;
        int[] primes = primeGenerator(maxPrimePossible);

        if (sliderP.getValue() == sliderQ.getValue()) {
            sliderQ.setValue(1);
        }

        mapPrimesOnSlider(primes, sliderP, labelP);
        mapPrimesOnSlider(primes, sliderQ, labelQ);
        calcN(primes[(int) Math.round(sliderP.getValue())],primes[(int) Math.round(sliderQ.getValue())],labelN);
        sliderP.valueProperty().addListener((obs, o, n) -> {mapPrimesOnSlider(primes, sliderP, labelP); calcN(primes[(int) Math.round(sliderP.getValue())],primes[(int) Math.round(sliderQ.getValue())],labelN);});
        sliderQ.valueProperty().addListener((obs, o, n) -> {mapPrimesOnSlider(primes, sliderQ, labelQ); calcN(primes[(int) Math.round(sliderP.getValue())],primes[(int) Math.round(sliderQ.getValue())],labelN);});
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
            slider.setValue(currentIndex);
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
}