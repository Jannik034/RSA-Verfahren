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

    /**
     * Setzt verschiedene Werte und Einstellungen initial fest.
     */
    @FXML private void initialize() {
        int maxPrimePossible = 1000;
        int[] primes = RSAService.primeGenerator(maxPrimePossible);

        //Textformatter: Sorgt dafür, dass man nur Zahlen im encrypt-Textfeld eingeben kann.
        inputMessageEncrypt.setTextFormatter(new TextFormatter<>(change -> change.getControlNewText().matches("\\d*") ? change : null));
        inputMessageDecrypt.setTextFormatter(new TextFormatter<>(change -> change.getControlNewText().matches("\\d*") ? change : null));

        //Initial dürfen die Slider nicht auf dem gleichen Wert stehen.
        if (sliderP.getValue() == sliderQ.getValue()) {
            sliderQ.setValue(1);
        }

        //Initialer Aufruf, um die Werte bevor dem ersten Bewegen der Slider schon anzuzeigen.
        updateSliders(primes, sliderP, labelP);
        updateSliders(primes, sliderQ, labelQ);
        updateLabels(primes[(int) Math.round(sliderP.getValue())] , primes[(int) Math.round(sliderQ.getValue())]);

        //Die Anzeigelabels in den Encrypt und Decrypt Tabs sind direkt abhängig von den Werten der Anzeigelabels im Voreinstellungen Tab.
        labelDDecrypt.textProperty().bind(labelDSettings.textProperty());
        labelEEncrypt.textProperty().bind(labelESettings.textProperty());
        labelNDecrypt.textProperty().bind(labelNSettings.textProperty());
        labelNEncrypt.textProperty().bind(labelNSettings.textProperty());

        //Hinzufügen der Listener, die bei Bewegung des Sliders die Funktion onSliderChanged() aufrufen.
        sliderP.valueProperty().addListener((obs, o, n) -> onSliderChanged(primes, sliderP, sliderQ));
        sliderQ.valueProperty().addListener((obs, o, n) -> onSliderChanged(primes, sliderQ, sliderP));

        handleCryption(primes);
    }

    /**
     * Fügt den encrypt und decrypt Buttons einen EventHandler hinzu, der bei Betätigung des Knopfes den Text ver und entschlüsselt. Alle NumberFormatExeptions werden dabei durch den Try-Catch Block entsprechend behandelt.
     * @param primes Primzahlarray
     */
    @FXML private void handleCryption(int[] primes) {
        encryptBtn.setOnAction(event -> {

            String text = inputMessageEncrypt.getText();

            //Prüft ob das Textobjekt existiert und einen Inhalt besitzt.
            if (text != null && !text.isEmpty()) {

                //Errechnung aller aktuellen Werte über RSAService.
                int currentP = primes[(int) Math.round(sliderP.getValue())];
                int currentQ = primes[(int) Math.round(sliderQ.getValue())];
                int currentN = RSAService.calcN(currentP, currentQ);
                int currentE = RSAService.calcE(currentP, currentQ);

                //Führt die encryption aus und fängt mögliche Fehlermeldungen ab
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

            //Prüft ob das Textobjekt existiert und einen Inhalt besitzt.
            if (text != null && !text.isEmpty()) {

                //Errechnung aller aktuellen Werte über RSAService.
                int currentP = primes[(int) Math.round(sliderP.getValue())];
                int currentQ = primes[(int) Math.round(sliderQ.getValue())];
                int currentN = RSAService.calcN(currentP, currentQ);
                int currentE = RSAService.calcE(currentP, currentQ);
                int currentD = RSAService.calcD(currentP, currentQ, currentE);

                //Führt die decryption aus und fängt mögliche Fehlermeldungen ab.
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

        // Verhindert es, dass der bewegte Slider den gleichen Index wie der andere Slider hat und damit p = q ist.
        if (movedIdx == otherIdx) {
            isUpdating = true;
            movedIdx = (movedIdx + 1 < primes.length) ? movedIdx + 1 : movedIdx - 1;
            movedSlider.setValue(movedIdx);
            isUpdating = false;
        }

        // Aktualisierung der Slider und Label, nach dem aktuell bewegten Slider.
        updateSliders(primes, movedSlider, (movedSlider == sliderP) ? labelP : labelQ);
        updateLabels(primes[(int) Math.round(sliderP.getValue())] , primes[(int) Math.round(sliderQ.getValue())]);
    }

    /**
     * Mappt die Primzahlen des Arrays int[] primes auf den Slider und zeigt den aktuellen Wert des Sliders als Label an.
     * @param primes Primzahlarray
     * @param slider zu mappender Slider
     * @param label Anzeigelabel
     */
    @FXML private void updateSliders(int[] primes, Slider slider, Label label) {
        setupSlider(slider, primes.length);

        //Errechnung der entsprechender Primzahl aus dem primes-Array mit passendem Index zum Slider.
        int index = (int) Math.round(slider.getValue());
        int prime = primes[index];

        if(slider == sliderP) {
            label.setText("p = "+ prime);
        }else{
            label.setText("q = "+ prime);
        }
    }

    /**
     * Verpasst den Primzahl-Slidern ihre Grundeinstellungen
     * @param slider einzustellender Slider
     * @param primesLength Länge des Primzahlarrays
     */
    @FXML private void setupSlider(Slider slider, int primesLength) {
        slider.setMin(0);
        slider.setMax(primesLength -1);
        slider.setMinorTickCount(0);
        slider.setBlockIncrement(1);
        slider.setShowTickLabels(false);
        slider.setShowTickMarks(false);
    }

    /**
     * Aktualisiert die Werte N, e und d in den Anzeigeboxen.
     * @param p Primzahl
     * @param q Primzahl
     */
    @FXML private void updateLabels(int p, int q) {
        labelNSettings.setText("N = "+ RSAService.calcN(p, q));
        labelESettings.setText("e = "+ RSAService.calcE(p, q));
        labelDSettings.setText("d = "+ RSAService.calcD(p, q, RSAService.calcE(p, q)));
    }
}