package com.example.rsaverfahren;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class Controller {

    @FXML private Slider sliderP, sliderQ;
    @FXML private Label labelP, labelQ;
    @FXML private Label labelNSettings, labelNEncrypt, labelNDecrypt;
    @FXML private Label labelDSettings, labelDDecrypt;
    @FXML private Label labelESettings, labelEEncrypt;
    @FXML private TextField inputMessageEncrypt, outputMessageEncrypt, inputMessageDecrypt, outputMessageDecrypt;
    @FXML private Button encryptBtn, decryptBtn;
    private boolean isUpdating = false;

    @FXML private void initialize() {
        int maxPrimePossible = 1000;
        int[] primes = RSAService.primeGenerator(maxPrimePossible);

        inputMessageEncrypt.setTextFormatter(new TextFormatter<>(change -> change.getControlNewText().matches("\\d*") ? change : null));

        if (sliderP.getValue() == sliderQ.getValue()) {
            sliderQ.setValue(1);
        }

        updateSliders(primes, sliderP, labelP);
        updateSliders(primes, sliderQ, labelQ);

        labelDDecrypt.textProperty().bind(labelDSettings.textProperty());
        labelEEncrypt.textProperty().bind(labelESettings.textProperty());
        labelNDecrypt.textProperty().bind(labelNSettings.textProperty());
        labelNEncrypt.textProperty().bind(labelNSettings.textProperty());

        updateLabels(primes[(int) Math.round(sliderP.getValue())],primes[(int) Math.round(sliderQ.getValue())]);
        sliderP.valueProperty().addListener((obs, o, n) -> onSliderChanged(primes, sliderP, sliderQ));
        sliderQ.valueProperty().addListener((obs, o, n) -> onSliderChanged(primes, sliderQ, sliderP));

        handleCryption(primes);
    }

    @FXML private void handleCryption(int[] primes) {
        encryptBtn.setOnAction(event -> {
            String text = inputMessageEncrypt.getText();
            if (text != null && !text.isEmpty()) {
                int currentP = primes[(int) Math.round(sliderP.getValue())];
                int currentQ = primes[(int) Math.round(sliderQ.getValue())];
                int currentN = RSAService.calcN(currentP, currentQ);
                int currentE = RSAService.calcE(currentP, currentQ);

                try {
                    int plaintext = Integer.parseInt(text);
                    if (plaintext < currentN) {
                        outputMessageEncrypt.setText("" + RSAService.encrypt(plaintext, currentE, currentN));
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
        decryptBtn.setOnAction(event -> {
            String text = inputMessageDecrypt.getText();
            if (text != null && !text.isEmpty()) {
                int currentP = primes[(int) Math.round(sliderP.getValue())];
                int currentQ = primes[(int) Math.round(sliderQ.getValue())];
                int currentN = RSAService.calcN(currentP, currentQ);
                int currentE = RSAService.calcE(currentP, currentQ);
                int currentD = RSAService.calcD(currentP, currentQ, currentE);

                try {
                    int ciphertext = Integer.parseInt(text);
                    if (ciphertext < currentN) {
                        outputMessageDecrypt.setText("" + RSAService.decrypt(ciphertext, currentD, currentN));
                    } else {
                        outputMessageDecrypt.setText("Eingabe muss kleiner als N sein!");
                    }
                } catch (NumberFormatException ex) {
                    outputMessageDecrypt.setText("");
                }
            } else {
                outputMessageDecrypt.setText("");
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


        updateSliders(primes, sliderP, labelP);
        updateSliders(primes, sliderQ, labelQ);
        updateLabels(primes[(int) Math.round(sliderP.getValue())],primes[(int) Math.round(sliderQ.getValue())]);
    }


    /**
     * Mappt die Primzahlen des Arrays int[] primes auf den Slider und zeigt den aktuellen Wert des Sliders als Label an.
     * @param primes Primzahlarray
     * @param slider zu mappender Slider
     * @param label Anzeigelabel
     */
    @FXML private void updateSliders(int[] primes, Slider slider, Label label) {
        setupSlider(slider, primes);

        int index = (int) Math.round(slider.getValue());
        int prime = primes[index];

        if(slider == sliderP) {
            label.setText("p = "+ prime);
        }else{
            label.setText("q = "+ prime);
        }
    }

    @FXML private void setupSlider(Slider slider, int[] primes) {
        slider.setMin(0);
        slider.setMax(primes.length -1);
        slider.setMinorTickCount(0);
        slider.setBlockIncrement(1);
        slider.setShowTickLabels(false);
        slider.setShowTickMarks(false);
    }

    @FXML private void updateLabels(int p, int q) {
        labelNSettings.setText("N = "+ RSAService.calcN(p, q));
        labelESettings.setText("e = "+ RSAService.calcE(p, q));
        labelDSettings.setText("d = "+ RSAService.calcD(p, q, RSAService.calcE(p, q)));
    }
}