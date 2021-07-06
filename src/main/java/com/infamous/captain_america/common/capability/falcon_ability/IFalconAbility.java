package com.infamous.captain_america.common.capability.falcon_ability;


import com.infamous.captain_america.common.util.ITranslatable;

import java.util.Map;

public interface IFalconAbility {

    Map<Key, Value> getAbilitySelectionMap();

    boolean isHovering();

    void setHovering(boolean hovering);

    boolean isVerticallyFlying();

    void setVerticallyFlying(boolean verticallyFlying);

    enum Key implements ITranslatable {
        FLIGHT("ability.falcon.flight"),
        COMBAT("ability.falcon.combat"),
        DRONE("ability.falcon.drone"),
        HUD("ability.falcon.hud");

        private final String translationKey;

        Key(String translationKey){
            this.translationKey = translationKey;
        }

        @Override
        public String getTranslationKey() {
            return translationKey;
        }
    }

    enum Value implements ITranslatable{
        TOGGLE_FLIGHT(Key.FLIGHT, "toggleFlight"),
        BOOST(Key.FLIGHT, "boost"),
        HALT(Key.FLIGHT, "halt"),
        TOGGLE_HOVER(Key.FLIGHT, "toggleHover"),

        MISSILE(Key.COMBAT, "infrared"),

        DEPLOY(Key.DRONE, "deploy"),
        TOGGLE_PATROL(Key.DRONE, "togglePatrol"),
        TOGGLE_RECALL(Key.DRONE, "toggleRecall"),

        INFRARED(Key.HUD, "infrared");

        private final Key parent;
        private final String suffix;

        Value(Key parent, String suffix){
            this.parent = parent;
            this.suffix = suffix;
        }

        public Key getParent() {
            return this.parent;
        }

        @Override
        public String getTranslationKey() {
            return this.parent.getTranslationKey() + "." + this.suffix;
        }

        public boolean isValidForKey(Key key){
            return this.parent == key;
        }
    }
}
