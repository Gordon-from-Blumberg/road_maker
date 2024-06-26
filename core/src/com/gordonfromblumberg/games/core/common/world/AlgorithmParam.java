package com.gordonfromblumberg.games.core.common.world;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public class AlgorithmParam {
    private final String name;
    private final BiFunction<Skin, Consumer<Object>, Actor> componentSupplier;
    private Object value;

    public AlgorithmParam(String name, Object value, BiFunction<Skin, Consumer<Object>, Actor> componentSupplier) {
        this.name = name;
        this.value = value;
        this.componentSupplier = componentSupplier;
    }

    public String getName() {
        return name;
    }

    public Actor createComponent(Skin skin) {
        return componentSupplier.apply(skin, this::setValue);
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
