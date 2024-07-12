package com.gordonfromblumberg.games.core.common.world;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.function.Consumer;

public class AlgorithmParam {
    private final String name;
    private final Class<?> type;
    private final ComponentCreator componentCreator;
    private Object value;

    public AlgorithmParam(String name, Class<?> type, Object value, ComponentCreator componentCreator) {
        this.name = name;
        this.type = type;
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

    public void save(String prefix, Preferences prefs) {
        prefs.putString(prefix + name, String.valueOf(value));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void load(String prefix, Preferences prefs) {
        if (type.equals(String.class)) value = prefs.getString(prefix + name, (String) value);
        else if (type.equals(Integer.class)) value = prefs.getInteger(prefix + name, (int) value);
        else if (type.equals(Float.class)) value = prefs.getFloat(prefix + name, (float) value);
        else if (type.equals(Boolean.class)) value = prefs.getBoolean(prefix + name, (boolean) value);
        else if (type.isAssignableFrom(Enum.class)) value = Enum.valueOf((Class<Enum>) type, prefs.getString(prefix + name));
    }

    @FunctionalInterface
    public interface ComponentCreator {
        Actor create(Skin skin, Object value, Consumer<Object> valueConsumer);
    }
}
