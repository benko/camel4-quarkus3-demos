package com.redhat.training.processors;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.redhat.training.model.ActionItem;

public class AssignActionItem {
    private List<String> engineers = Arrays.asList(
        "Maverick Moss",
        "Ryland Fletcher",
        "Quentin Townsend"
    );

    public void assignEngineer(ActionItem m) {
        m.setAssignee(engineers.get((new Random()).nextInt(engineers.size())));
    }
}
