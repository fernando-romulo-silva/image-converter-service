package org.imageconverter.util.controllers;

import static java.text.MessageFormat.format;

import javax.validation.constraints.NotNull;

import org.imageconverter.domain.ExecutionType;
import org.springframework.web.multipart.MultipartFile;

public final record ImageConverterRequest(MultipartFile data, ExecutionType executionType, int x, int y, int width, int height) {

    public ImageConverterRequest(@NotNull final MultipartFile data, @NotNull final ExecutionType executionType, final int x, final int y, final int width, final int height) {
	this.data = data;
	this.x = x;
	this.y = y;
	this.width = width;
	this.height = height;
	this.executionType = executionType;
    }

    public ImageConverterRequest(@NotNull final MultipartFile data, @NotNull final ExecutionType executionType) {
	this(data, executionType, 0, 0, 0, 0);
    }

    @Override
    public String toString() {
	return format("Image[file {0}, x {1}, x {2}, width {3}, height {4}]", data.getOriginalFilename(), x, y, width, height);
    }
}