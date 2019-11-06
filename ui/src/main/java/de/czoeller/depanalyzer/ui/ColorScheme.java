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
package de.czoeller.depanalyzer.ui;

import java.awt.*;

import static de.czoeller.depanalyzer.ui.util.GradientUtil.createMultiGradient;

/** Defines constants for used colors in GUI. */
public class ColorScheme {

    /** Background-Color for graph viewer */
    public static final Color BG_COLOR = Color.decode("#f4f4f4");

    /** Node Colors */
    public static class NODE {
        /** Color for nodes */
        public static final Color COLOR = Color.lightGray;
        /** Color for picked nodes contour */
        public static final Color CONTOUR = Color.black;
        /** Highlight-Color for picked nodes contour */
        public static final Color PICKED_CONTOUR_HL_COLOR = Color.cyan;
        /** Color for picked project nodes contour */
        public static final Color PROJECT_CONTOUR = Color.blue;
        /** Highlight-Color for picked project nodes contour */
        public static final Color PROJECT_PICKED_CONTOUR_HL_COLOR = Color.cyan;

        /** Color-step for minimal node heat */
        private static final Color STEP_HEAT_COLOR_MIN = BG_COLOR;
        /** Color-step for medium node heat */
        private static final Color STEP_HEAT_COLOR_1 = new Color(105, 0, 0);
        /** Color-step for higher node heat */
        private static final Color STEP_HEAT_COLOR_2 = new Color(255, 150, 38);
        /** Color-step for maximal node heat */
        private static final Color STEP_HEAT_COLOR_MAX = Color.red;
        /** Color-Gradient for node heat */
        public static final Color[] HEAT_COLOR_GRADIENT = createMultiGradient(new Color[]{STEP_HEAT_COLOR_MIN, STEP_HEAT_COLOR_1, STEP_HEAT_COLOR_2, STEP_HEAT_COLOR_MAX}, 100);
    }

    /** Edge Colors */
    public static class EDGE {
        /** Color for edges */
        public static final Color COLOR = Color.lightGray;
        /** Highlight-Color for edges that are included in search result */
        public static final Color SEARCH_HL_COLOR = Color.yellow;
        /** Highlight-Color for edges that are included in the trace path */
        public static final Color TRACE_HL_COLOR = Color.orange;

        /** Color for edge animation */
        public static final Color ANIMATION_COLOR = EDGE.COLOR;
        /** Highlight-Color for edge animation */
        public static final Color ANIMATION_HL_COLOR = Color.red;

        /** Color for edge arrow */
        public static final Color ARROW_COLOR = EDGE.COLOR;
        /** Color for edge arrow contour */
        public static final Color ARROW_CONTOUR_COLOR = EDGE.COLOR;
    }
}
