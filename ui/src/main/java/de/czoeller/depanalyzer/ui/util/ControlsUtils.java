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
package de.czoeller.depanalyzer.ui.util;

import de.czoeller.depanalyzer.ui.model.Layouts;
import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.util.Duration;
import org.controlsfx.control.SegmentedButton;

import java.util.function.Consumer;
import java.util.function.Function;

public class ControlsUtils {

    public static void selectFirstButton(SegmentedButton layoutSegmentedButton) {
        layoutSegmentedButton.getButtons().stream().findFirst().ifPresent(toggle -> toggle.setSelected(true));
    }

    /**
     * Intercept {@link ChangeListener} to check if the new value is not null and the new value differs the old value.
     * In addition to {@link ControlsUtils#safeChangeListener(java.util.function.Consumer)} this function
     * provides the ability to pass a mapper to convert from one type to another.
     *
     * @param action the action that is executed if the checks are passed
     * @param <T> The type of the change listener
     * @param <R> The type of the mapped value to be passed to the action
     * @return returns a new {@link ChangeListener} with the specified behavior.
     */
    public static <T, R> ChangeListener<T> safeChangeListener(Function<T, R> mapper, Consumer<R> action) {
        return (observable, oldValue, newValue) -> {
            if( newValue != null && newValue != oldValue ) {
                action.accept(mapper.apply(newValue));
            }
        };
    }

    /**
     * Intercept {@link ChangeListener} to check if the new value is not null and the new value differs the old value.
     *
     * @param action the action that is executed if the checks are passed
     * @param <T> The type of the change listener
     * @return returns a new {@link ChangeListener} with the specified behavior.
     */
    public static <T> ChangeListener<T> safeChangeListener(Consumer<T> action) {
        return safeChangeListener(Function.identity(), action);
    }

    /**
     * Intercept {@link ChangeListener} to check if the new value is not null and the new value differs the old value.
     *
     * @param consumer
     * @param <T> something that extends Toggle
     * @return
     */
    public static <T extends javafx.scene.control.Toggle> ChangeListener<T> safeToggleChangeListener(Consumer<Layouts> consumer) {
        return safeChangeListener((t) -> (Layouts) t.getUserData(), consumer::accept);
    }

    /**
     * Schedule something after a 1 second delay from the last time a {@link ChangeListener} is fired.
     * If it fires again within that 1 second window, the previous change is ignored and the something is scheduled to be done with the new value 1 second from the most recent change.
     * @see <a href="https://stackoverflow.com/questions/34784037/javafx-8-how-to-add-a-timedelay-to-a-listener">https://stackoverflow.com/questions/34784037/javafx-8-how-to-add-a-timedelay-to-a-listener</a>
     * @param listener the origin listener
     * @param <T> type parameter of the listener
     * @return returns a new {@link ChangeListener} with the specified behavior.
     */
    public static <T> ChangeListener<T> delayedListener(ChangeListener<T> listener) {
        final PauseTransition pause = new PauseTransition(Duration.seconds(1));
        return (observable, oldValue, newValue) -> {
            pause.setOnFinished(event -> listener.changed(observable, oldValue, newValue));
            pause.playFromStart();
        };
    }

}
