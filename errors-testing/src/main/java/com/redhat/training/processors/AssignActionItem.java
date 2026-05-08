package com.redhat.training.processors;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.redhat.training.model.ActionItem;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AssignActionItem {
    private List<String> engineers = Arrays.asList(
        "Maverick Moss",
        "Ryland Fletcher",
        "Quentin Townsend"
    );

    public void assignEngineer(ActionItem m) {
        // Will periodically throw ArrayIndexOutOfBounds exception.
        m.setAssignee(engineers.get((new Random()).nextInt(engineers.size() + 1)));
        // Throw a generic runtime exception once every 5 times we survive the above.
        if ((new Random()).nextInt(5) == 4) {
            throw new RuntimeException("Blurb");
        }
    }
}
