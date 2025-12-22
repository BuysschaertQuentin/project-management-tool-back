package com.iscod.project_management_tool_back.entity.pmtenum;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum NotificationTypeEnum {
    TASK_ASSIGNED("TASK_ASSIGNED"), TASK_UPDATED("TASK_UPDATED"), PROJECT_INVITATION("PROJECT_INVITATION");

    private final String type;

    public static NotificationTypeEnum fromString(String type) {
        return Arrays.stream(NotificationTypeEnum.values())
                .filter(notificationTypeEnum -> notificationTypeEnum.type.equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid type : " + type));
    }
}
