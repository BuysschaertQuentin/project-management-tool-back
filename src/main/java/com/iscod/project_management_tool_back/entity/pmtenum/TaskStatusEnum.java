package com.iscod.project_management_tool_back.entity.pmtenum;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum TaskStatusEnum {
    TODO("TODO"), IN_PROGRESS("IN_PROGRESS"), DONE("DONE");

    private final String status;

    public static TaskStatusEnum fromString(String status) {
        return Arrays.stream(TaskStatusEnum.values())
                .filter(taskStatusEnum -> taskStatusEnum.status.equalsIgnoreCase(status))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid status : " + status));
    }
}
