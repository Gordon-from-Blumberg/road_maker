package com.gordonfromblumberg.games.core.common.world;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.function.Consumer;

public class AlgorithmParam {
    private final String name;
    private final ComponentCreator componentCreator;
    private Object value;

    public AlgorithmParam(String name, Object value, ComponentCreator componentCreator) {
        this.name = name;
        this.value = value;
        this.componentCreator = componentCreator;
    }

    public String getName() {
        return name;
    }

    public Actor createComponent(Skin skin) {
        return componentCreator.create(skin, value, this::setValue);
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @FunctionalInterface
    public interface ComponentCreator {
        Actor create(Skin skin, Object value, Consumer<Object> valueConsumer);
    }
}
