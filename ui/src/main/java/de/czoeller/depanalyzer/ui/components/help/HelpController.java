package de.czoeller.depanalyzer.ui.components.help;

import de.czoeller.depanalyzer.ui.scorer.ScoreToHeatTransformer;
import de.czoeller.depanalyzer.ui.util.GradientUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HelpController implements Initializable {

    @FXML
    public Rectangle legendColorRect;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        final java.awt.Color[] multiGradient = ScoreToHeatTransformer.multiGradient;
        final List<Stop> collect = IntStream.range(0, multiGradient.length)
                                            .filter(i -> i % 25 == 0 || i == multiGradient.length - 1)
                                            .mapToObj(i -> new Stop(i/100f, GradientUtil.fromAWTColor(multiGradient[i])))
                                            .collect(Collectors.toList());
        legendColorRect.setFill(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, collect));

    }
}
