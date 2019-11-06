/*
 * Copyright (C) 2019 czoeller
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.czoeller.depanalyzer.ui.components.help;

import de.czoeller.depanalyzer.ui.ColorScheme;
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
        final java.awt.Color[] multiGradient = ColorScheme.NODE.HEAT_COLOR_GRADIENT;
        final List<Stop> collect = IntStream.range(0, multiGradient.length)
                                            .filter(i -> i % 25 == 0 || i == multiGradient.length - 1)
                                            .mapToObj(i -> new Stop(i/100f, GradientUtil.fromAWTColor(multiGradient[i])))
                                            .collect(Collectors.toList());
        legendColorRect.setFill(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, collect));

    }
}
