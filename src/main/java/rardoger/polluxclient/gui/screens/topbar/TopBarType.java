/*
 * This file is part of the Pollux Client distribution (https://github.com/PolluxDevelopment/pollux-client/).
 * Copyright (c) 2020 Pollux Development.
 */

package rardoger.polluxclient.gui.screens.topbar;

public enum TopBarType {
    Modules(TopBarModules::new),
    Config(TopBarConfig::new),
    Gui(TopBarGui::new),
    Friends(TopBarFriends::new),
    Macros(TopBarMacros::new),
    Baritone(TopBarBaritone::new),
    Waypoints(TopBarWaypoints::new);

    private interface TopBarScreenFactory {
        TopBarScreen create();
    }

    private final TopBarScreenFactory topBarScreenFactory;

    TopBarType(TopBarScreenFactory topBarScreenFactory) {
        this.topBarScreenFactory = topBarScreenFactory;
    }

    public TopBarScreen createScreen() {
        return topBarScreenFactory.create();
    }
}
