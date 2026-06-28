package com.usedcar.behavior.dto;

import jakarta.validation.constraints.NotNull;

public record ViewRequest(@NotNull Long carId) {}
