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
package de.czoeller.depanalyzer.ui.animation;

import edu.uci.ics.jung.visualization.VisualizationViewer;

import java.awt.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Function;

public class NodeFillHighlightAnimation<N, E> {

    private final VisualizationViewer<N, E> vv;
    private final List<N> nodes;
    private final Paint color;
    /** static fields can't have type parameter */
    private static Function backedUpNodeFillPaintFunction;

    public NodeFillHighlightAnimation(VisualizationViewer<N, E> vv, List<N> nodes, Paint color) {
        this.vv = vv;
        this.nodes = nodes;
        this.color = color;
        if (backedUpNodeFillPaintFunction == null) {
            backedUpNodeFillPaintFunction = vv.getRenderContext().getNodeFillPaintFunction();
        }
        if(!nodes.isEmpty()) {
            java.util.Timer timer = new java.util.Timer();
            AnimationTimerTask at = new AnimationTimerTask(timer);
            timer.scheduleAtFixedRate(at, 10, 30);
        }
    }

    class AnimationTimerTask extends TimerTask {

        private final double width = 0.123; // Size of the colored line.
        private final double stepsize = 0.01;
        private final Timer timer;
        private double keyframe = 0 + width; // Between 0.0 and 1.0


        public AnimationTimerTask(Timer timer) {
            this.timer = timer;
        }

        @Override
        public void run() {
            vv.getRenderContext()
              .setNodeFillPaintFunction(n -> {
                  if (nodes.contains(n)) {
                      return color;
                  } else {
                      return (Paint) backedUpNodeFillPaintFunction.apply(n);
                  }
              });
            keyframe += stepsize;
            keyframe %= 1.0;
            if (keyframe >= 0.99) {
                timer.cancel();
                vv.getRenderContext()
                  .setNodeFillPaintFunction(backedUpNodeFillPaintFunction);
            }
            vv.repaint();
        }
    }
}
