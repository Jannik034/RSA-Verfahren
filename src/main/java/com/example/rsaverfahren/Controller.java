package com.example.rsaverfahren;

import javafx.fxml.FXML;
import javafx.scene.control.*;


public class Controller {

    @FXML private Slider sliderP, sliderQ;
    @FXML private Label labelP, labelQ;

    @FXML private void initialize() {
        int maxPrimePossible = 1000;
        int[] primes = primeGenerator(maxPrimePossible);
        mapPrimesOnSlider(primes, sliderP, labelP);
        mapPrimesOnSlider(primes, sliderQ, labelQ);
        sliderP.valueProperty().addListener((obs, o, n) -> mapPrimesOnSlider(primes, sliderP, labelP));
        sliderQ.valueProperty().addListener((obs, o, n) -> mapPrimesOnSlider(primes, sliderQ, labelQ));
    }

    @FXML private void mapPrimesOnSlider(int[] primes, Slider slider, Label label) {

        int p = primes[(int) Math.round(slider.getValue())];

        slider.setMin(0);
        slider.setMax(primes.length -1);
        slider.setSnapToTicks(true);
        slider.setMinorTickCount(0);
        slider.setBlockIncrement(1);
        slider.setShowTickLabels(false);
        slider.setShowTickMarks(false);

        label.setText(""+p);
    }

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