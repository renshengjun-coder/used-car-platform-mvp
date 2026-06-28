package com.usedcar.behavior.dto;

import java.util.Map;

public record SearchEventRequest(String keyword, Map<String, Object> filters) {}
