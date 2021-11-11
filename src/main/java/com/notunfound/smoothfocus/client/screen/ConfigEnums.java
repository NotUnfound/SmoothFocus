package com.notunfound.smoothfocus.client.screen;

@SuppressWarnings("unchecked")
public class ConfigEnums {

    public interface IModConfigEnum {

        <T extends IModConfigEnum> T next();

    }

    public enum MouseSensitivityModifier implements IModConfigEnum {

        NONE, SCALED, SET;

        public <T extends IModConfigEnum> T next() {

            return (T) values()[(ordinal() + 1) % 3];
        }


    }

    public enum SmoothType implements IModConfigEnum {

        NONE, SCROLL, TOGGLE, BOTH;

        public boolean scroll() {
            return equals(SCROLL) || equals(BOTH);
        }

        public boolean toggle() {
            return equals(TOGGLE) || equals(BOTH);
        }

        public <T extends IModConfigEnum> T next() {
            return (T) values()[(ordinal() + 1) % 4];
        }

    }

    public enum ToggleType implements IModConfigEnum {

        DOUBLE_TAP_ON, DOUBLE_TAP_OFF, DOUBLE_TAP_ON_OFF, TAP_ON_OFF, DISABLE;

        public boolean turnOn() {
            return equals(DOUBLE_TAP_ON_OFF) || equals(DOUBLE_TAP_ON);
        }

        public boolean turnOff() {
            return equals(DOUBLE_TAP_ON_OFF) || equals(DOUBLE_TAP_OFF);
        }

        @Override
        public <T extends IModConfigEnum> T next() {
            return (T) values()[(ordinal() + 1) % 5];
        }

    }
}
