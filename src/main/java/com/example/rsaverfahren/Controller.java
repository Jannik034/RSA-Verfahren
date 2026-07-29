package com.example.rsaverfahren;

import javafx.fxml.FXML;
import javafx.scene.control.*;


public class Controller {

    @FXML private Slider sliderP, sliderQ;
    @FXML private Label labelP, labelQ;
    @FXML private Label labelNSettings, labelNEncrypt, labelNDecrypt;
    @FXML private Label labelDSettings, labelDDecrypt;
    @FXML private Label labelESettings, labelEEncrypt;
    @FXML private TextField inputMessageEncrypt, outputMessageEncrypt;
    @FXML private Button encryptBtn;
    private boolean isUpdating = false;

    @FXML private void initialize() {
        int maxPrimePossible = 1000;
        int[] primes = primeGenerator(maxPrimePossible);

        inputMessageEncrypt.setTextFormatter(new TextFormatter<>(change -> change.getControlNewText().matches("\\d*") ? change : null));

        if (sliderP.getValue() == sliderQ.getValue()) {
            sliderQ.setValue(1);
        }

        mapPrimesOnSlider(primes, sliderP, labelP);
        mapPrimesOnSlider(primes, sliderQ, labelQ);

        int p = primes[(int) Math.round(sliderP.getValue())];
        int q = primes[(int) Math.round(sliderQ.getValue())];

        labelDDecrypt.textProperty().bind(labelDSettings.textProperty());
        labelEEncrypt.textProperty().bind(labelESettings.textProperty());
        labelNDecrypt.textProperty().bind(labelNSettings.textProperty());
        labelNEncrypt.textProperty().bind(labelNSettings.textProperty());

        labelNSettings.setText("N = "+ calcN(p, q));
        labelESettings.setText("e = "+ calcE(p, q));
        labelDSettings.setText("d = "+ calcD(p, q, calcE(p, q)));

        sliderP.valueProperty().addListener((obs, o, n) -> onSliderChanged(primes, sliderP, sliderQ));
        sliderQ.valueProperty().addListener((obs, o, n) -> onSliderChanged(primes, sliderQ, sliderP));
        encryptBtn.setOnAction(event -> {
            String text = inputMessageEncrypt.getText();
            if (text != null && !text.isEmpty()) {
                int currentP = primes[(int) Math.round(sliderP.getValue())];
                int currentQ = primes[(int) Math.round(sliderQ.getValue())];
                int currentN = calcN(currentP, currentQ);
                int currentE = calcE(currentP, currentQ);

                try {
                    int plaintext = Integer.parseInt(text);
                    if (plaintext < currentN) {
                        outputMessageEncrypt.setText("" + encrypt(plaintext, currentE, currentN));
                    } else {
                        outputMessageEncrypt.setText("Eingabe muss kleiner als N sein!");
                    }
                } catch (NumberFormatException ex) {
                    outputMessageEncrypt.setText("");
                }
            } else {
                outputMessageEncrypt.setText("");
            }
        });
    }

    /**
     * Verarbeitet die Bewegung eines Sliders und verhindert eine Überlappung der beiden Primzahl-Slider
     * @param primes Primzahlarray
     * @param movedSlider der bewegte Slider
     * @param otherSlider der jeweils andere Slider
     */
    @FXML void onSliderChanged(int[] primes, Slider movedSlider, Slider otherSlider) {
        if (isUpdating) return;

        int movedIdx = (int) Math.round(movedSlider.getValue());
        int otherIdx = (int) Math.round(otherSlider.getValue());

        if (movedIdx == otherIdx) {
            isUpdating = true;
            movedIdx = (movedIdx + 1 < primes.length) ? movedIdx + 1 : movedIdx - 1;
            movedSlider.setValue(movedIdx);
            isUpdating = false;
        }

        mapPrimesOnSlider(primes, sliderP, labelP);
        mapPrimesOnSlider(primes, sliderQ, labelQ);

        int p = primes[(int) Math.round(sliderP.getValue())];
        int q = primes[(int) Math.round(sliderQ.getValue())];
        int e = calcE(p, q);

        labelNSettings.setText("N = "+ calcN(p, q));
        labelESettings.setText("e = "+ calcE(p, q));
        labelDSettings.setText("d = "+ calcD(p, q, calcE(p, q)));
    }

    @FXML private int encrypt(int plaintext, int e, int N) {

        return (int) (Math.pow(plaintext,e) % N);
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

        int index = (int) Math.round(slider.getValue());
        int prime = primes[index];

        if(slider == sliderP) {
            label.setText("p = "+ prime);
        }else{
            label.setText("q = "+ prime);
        }
    }

    /** Errechnet den Wert N aus q und p. Zeigt N dann als Label an.
     * @param p Primzahl
     * @param q Primzahl
     * @return N, Produkt aus q und p
     */
    @FXML private int calcN(int p, int q) {
        return p*q;
    }

    /**
     * Errechnet das kleinste e für das ggT(e, (p-1)*(q-1)) == 1 gilt; e also zu (q-1)*(p-1) teilerfremd ist.
     * @param p Primzahl
     * @param q Primzahl
     * @return e
     */
    @FXML private int calcE(int p, int q) {
        int phi = (p-1) * (q-1);
        int e = 3;

        while (ggT(e,phi) != 1) {
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
    @FXML private int calcD(int p, int q, int e) {
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