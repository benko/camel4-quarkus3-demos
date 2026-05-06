package com.redhat.training.processors;

import com.redhat.training.service.SomeApplicationClass;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@ApplicationScoped
@Named("pojoBeanProcessor")
public class Pojo {
    @Inject
    SomeApplicationClass x;

    public String asdfgh(String param) {
        x.doSomething();
        return "Processed with string input: " + param;
    }
}
