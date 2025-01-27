package com.rjproj.memberapp.email;

import lombok.Getter;

public enum EmailTemplates {

    YES("event-confirmation.html", "Event confirmation confirmed"),
    NO("event-denied.html", "Event confirmation denied"),
    MAYBE("event-maybe.html", "Event Status maybe")

            ;

    @Getter
    private final String template;
    @Getter
    private final String subject;


    EmailTemplates(String template, String subject) {
        this.template = template;
        this.subject = subject;
    }
}