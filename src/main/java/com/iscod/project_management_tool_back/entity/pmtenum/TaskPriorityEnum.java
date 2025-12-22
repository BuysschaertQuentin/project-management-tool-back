package com.iscod.project_management_tool_back.entity.pmtenum;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum TaskPriorityEnum {
    LOW("LOW"), MEDIUM("MEDIUM"), HIGH("HIGH"), URGENT("URGENT");
    public final String priority;

    public static TaskPriorityEnum fromString(String priority) {
        return Arrays.stream(TaskPriorityEnum.values())
                .filter(taskPriorityEnum -> taskPriorityEnum.priority.equalsIgnoreCase(priority))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid priority : " + priority));
    }
}
