package com.rjproj.memberapp.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ImageType {
    PROFILE_IMAGE("profile-image"),
    BACKGROUND_IMAGE("background-image"),
    LOGO_IMAGE("logo-image");

    private final String value;


    ImageType(String value) {
        this.value = value;
    }

    @JsonValue  // Serialize Enum to its custom value
    public String getValue() {
        return value;
    }

    @JsonCreator  // Deserialize JSON String to Enum
    public static ImageType fromValue(String value) {
        for (ImageType type : ImageType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown ImageType: " + value);
    }
}
