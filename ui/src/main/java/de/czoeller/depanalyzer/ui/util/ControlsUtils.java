package de.czoeller.depanalyzer.ui.util;

import de.czoeller.depanalyzer.ui.model.Layouts;
import javafx.beans.value.ChangeListener;
import org.controlsfx.control.SegmentedButton;

import java.util.function.Consumer;
import java.util.function.Function;

public class ControlsUtils {

    public static void selectFirstToggle(SegmentedButton layoutSegmentedButton) {
        layoutSegmentedButton.getToggleGroup().getToggles().stream().findFirst().ifPresent(toggle -> layoutSegmentedButton.getToggleGroup().selectToggle(toggle));
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
     * @param <T> a Toggle
     * @return
     */
    public static <T extends javafx.scene.control.Toggle> ChangeListener<T> safeToggleChangeListener(Consumer<Layouts> consumer) {
        return safeChangeListener((t) -> (Layouts) t.getUserData(), consumer::accept);
    }

}
