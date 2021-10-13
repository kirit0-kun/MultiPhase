package com.flowapp.MultiPhase.Controllers;

import com.flowapp.MultiPhase.Models.MultiPhaseResult;
import com.flowapp.MultiPhase.MultiPhase;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class MainWindowController implements Initializable {

    @FXML
    private TextField iDTextField;

    @FXML
    private TextField liquidQTextField;

    @FXML
    private TextField liquidVisTextField;

    @FXML
    private TextField liquidDensityTextField;

    @FXML
    private TextField avgTTextField;

    @FXML
    private TextField lengthTextField;

    @FXML
    private TextField gasQTextField;

    @FXML
    private TextField gasVisTextField;

    @FXML
    private TextField gasDensityTextField;

    @FXML
    private TextField maxPressureTextField;

    @FXML
    private TextField endPressureTextField;

    @FXML
    private TextField roughnessTextField;

    @FXML
    private TextField liquidSpGrTextField;

    @FXML
    private TextField gasSpGrTextField;

    @FXML
    private TextArea answerArea;

    @FXML
    private Button calculateBtn;

    @FXML
    private ImageView facebookIcon;

    @FXML
    private ImageView linkedInIcon;

    @FXML
    private ImageView emailIcon;

    private final Application application;

    public MainWindowController(Application application) {
        this.application = application;
    }

    Stage getStage() {
        return (Stage) answerArea.getScene().getWindow();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        final TextField[] textFields = {
                iDTextField,liquidQTextField,liquidVisTextField,liquidDensityTextField,
                avgTTextField,lengthTextField,gasQTextField,gasVisTextField,
                gasDensityTextField,maxPressureTextField,roughnessTextField,
                liquidSpGrTextField, gasSpGrTextField,endPressureTextField,
        };
        for (var field: textFields) {
            field.setTextFormatter(createDecimalFormatter());
        }
        var packagePath = getClass().getPackageName().split("\\.");
        packagePath[packagePath.length-1] = "Fonts";
        String fontPath = Arrays.stream(packagePath).reduce("", (s, s2) -> s + "/" + s2);
        Font font = Font.loadFont(getClass().getResourceAsStream(fontPath + "/FiraCode-Retina.ttf"), answerArea.getFont().getSize());
        answerArea.setFont(font);
        calculateBtn.setOnAction(e -> {
            try {
                calculate();
            } catch (Exception ex) {
                ex.printStackTrace();
                final var errorDialog = createErrorDialog(getStage(), ex);
                errorDialog.show();
            }
        });
        setUpIcons();
    }

    private void setUpIcons() {
        var packagePath = getClass().getPackageName().split("\\.");
        packagePath[packagePath.length-1] = "Images";
        String fontPath = Arrays.stream(packagePath).reduce("", (s, s2) -> s + "/" + s2);
        final var facebookImage = getClass().getResource(fontPath + "/facebook.png");
        final var linkedInImage = getClass().getResource(fontPath + "/linkedin.png");
        final var emailImage = getClass().getResource(fontPath + "/email.png");
        facebookIcon.setImage(new Image(Objects.requireNonNull(facebookImage).toString()));
        linkedInIcon.setImage(new Image(Objects.requireNonNull(linkedInImage).toString()));
        emailIcon.setImage(new Image(Objects.requireNonNull(emailImage).toString()));
        facebookIcon.setPickOnBounds(true);
        linkedInIcon.setPickOnBounds(true);
        emailIcon.setPickOnBounds(true);
        facebookIcon.setOnMouseClicked(e -> {
            openBrowser("https://www.facebook.com/Moustafa.essam.hpp");
        });
        linkedInIcon.setOnMouseClicked(e -> {
            openBrowser("https://www.linkedin.com/in/moustafa-essam-726262174");
        });
        emailIcon.setOnMouseClicked(e -> {
            final var email = "mailto:essam.moustafa15@gmail.com";
            openBrowser(email);
            copyToClipboard(email);
        });
    }

    void openBrowser(String url) {
        application.getHostServices().showDocument(url);
    }

    private void copyToClipboard(String answer) {
        Clipboard.getSystemClipboard().setContent(Map.of(DataFormat.PLAIN_TEXT, answer));
    }

    private final Pattern numbersExpr = Pattern.compile("[-]?[\\d]*[.]?[\\d]*");
    TextFormatter<?> createDecimalFormatter() {
        final var pattern = numbersExpr.pattern();
        return new TextFormatter<>(c -> {
            if (c.getControlNewText().isEmpty()) { return c; }
            final var isGood = c.getControlNewText().matches(pattern);
            if (isGood) { return c; }
            else { return null; }
        });
    }

    void calculate() {
        final float iDIN = getFloat(iDTextField.getText());
        final float liquidFlowRateSCFDay = getFloat(liquidQTextField.getText());
        final float liquidViscosityCP = getFloat(liquidVisTextField.getText());
        final Float liquidDensityLBCF = getFloat(liquidDensityTextField.getText());
        final Float liquidSpGr = getFloat(liquidSpGrTextField.getText());
        final float avgTC = getFloat(avgTTextField.getText());
        final float roughness = getFloat(roughnessTextField.getText());
        final float gasFlowRateSCFDay = getFloat(gasQTextField.getText());
        final float gasViscosityCP = getFloat(gasVisTextField.getText());
        final Float gasDensityLBCF = getFloat(gasDensityTextField.getText());
        final Float gasSpGr = getFloat(gasSpGrTextField.getText());
        final Float maxPressurePSIA = getFloat(maxPressureTextField.getText());
        final Float endPressurePSIA = getFloat(endPressureTextField.getText());
        final float lengthFt = getFloat(lengthTextField.getText());

        final var task = new Service<MultiPhaseResult>() {
            @Override
            protected Task<MultiPhaseResult> createTask() {
                return new Task<>() {
                    @Override
                    protected MultiPhaseResult call() {
                        final var multiPhase = new MultiPhase();
                        return multiPhase.multiPhase(liquidFlowRateSCFDay
                                , liquidViscosityCP, liquidDensityLBCF, liquidSpGr,
                                iDIN, avgTC, roughness, gasFlowRateSCFDay,
                                gasViscosityCP, gasDensityLBCF, gasSpGr, maxPressurePSIA, endPressurePSIA, lengthFt);
                    }
                };
            }
        };
        final var loadingDialog = createProgressAlert((Stage) iDTextField.getScene().getWindow(), task);
        task.setOnRunning(e -> {
            loadingDialog.show();
        });
        task.setOnSucceeded(e -> {
            final var result = task.getValue();
            setAnswer(result.getSteps());
            loadingDialog.close();
        });
        task.setOnFailed(e -> {
            final var error = e.getSource().getException();
            final var errorDialog = createErrorDialog(getStage(), error);
            errorDialog.show();
            setAnswer(error.getMessage());
            loadingDialog.close();
        });
        task.setOnCancelled(e -> {
            loadingDialog.close();
        });
        task.restart();
    }

    Float getFloat(String value) {
        try {
            return Float.valueOf(value);
        } catch (Exception e) {
            return null;
        }
    }

    Integer getInteger(String value) {
        try {
            return Integer.valueOf(value);
        } catch (Exception e) {
            return null;
        }
    }

    void setAnswer(String answer) {
        answerArea.setText(answer);
    }

    Alert createErrorDialog(Stage owner, Throwable e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(owner);
        alert.setTitle("Error");
        alert.setContentText(e.getMessage());
        return alert;
    }

    Alert createProgressAlert(Stage owner, Service<?> task) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.initOwner(owner);
        alert.titleProperty().bind(task.titleProperty());
        alert.contentTextProperty().bind(task.messageProperty());

        ProgressIndicator pIndicator = new ProgressIndicator();
        pIndicator.progressProperty().bind(task.progressProperty());
        alert.setGraphic(pIndicator);
        alert.setHeaderText("Loading...");

        alert.getDialogPane().getButtonTypes().add(ButtonType.OK);
        alert.getDialogPane().lookupButton(ButtonType.OK)
                .disableProperty().bind(task.runningProperty());

        alert.getDialogPane().cursorProperty().bind(
                Bindings.when(task.runningProperty())
                        .then(Cursor.WAIT)
                        .otherwise(Cursor.DEFAULT)
        );
        return alert;
    }
}
