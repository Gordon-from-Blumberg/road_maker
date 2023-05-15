package com.gordonfromblumberg.games.core.common.ui;

import com.gordonfromblumberg.games.core.common.world.World;

public class ButtonConfig {
    final World gameWorld;
    final String[] disableEvents;
    final String[] enableEvents;
    final String[] showEvents;
    final String[] hideEvents;

    public ButtonConfig(World gameWorld, String[] disableEvents, String[] enableEvents, String[] showEvents, String[] hideEvents) {
        this.gameWorld = gameWorld;
        this.disableEvents = disableEvents;
        this.enableEvents = enableEvents;
        this.showEvents = showEvents;
        this.hideEvents = hideEvents;
    }

    public static ButtonConfig toggleDisable(World gameWorld, String[] disableEvents, String[] enableEvents) {
        return new ButtonConfig(gameWorld, disableEvents, enableEvents, null, null);
    }

    public static ButtonConfig toggleDisable(World gameWorld, String disableEvent, String enableEvent) {
        return new ButtonConfig(gameWorld, new String[] { disableEvent }, new String[] { enableEvent }, null, null);
    }
}
