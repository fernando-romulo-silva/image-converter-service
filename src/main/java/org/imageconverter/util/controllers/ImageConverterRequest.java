package org.imageconverter.util.controllers;

import static java.text.MessageFormat.format;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.imageconverter.domain.imageConvertion.ExecutionType;
import org.springframework.web.multipart.MultipartFile;

public final record ImageConverterRequest(MultipartFile data, ExecutionType executionType, int x, int y, int width, int height) {

    public ImageConverterRequest(//
		    @NotNull(message = "The data cannot be null") //
		    final MultipartFile data, //
		    //
		    @NotNull(message = "The executionType cannot be null") //
		    final ExecutionType executionType, //
		    //
		    @Min(value = 0, message = "The x point must be greater than zero") //
		    final int x, //
		    //
		    @Min(value = 0, message = "The y point must be greater than zero") //
		    final int y, //
		    //
		    @Min(value = 0, message = "The with must be greater than zero") //
		    final int width, //
		    //
		    @Min(value = 0, message = "The height must be greater than zero") //
		    final int height) {

	this.data = data;
	this.x = x;
	this.y = y;
	this.width = width;
	this.height = height;
	this.executionType = executionType;
    }

    public ImageConverterRequest( //
		    @NotNull(message = "data cannot be null") //
		    final MultipartFile data, //

		    @NotNull(message = "executionType cannot be null") //
		    final ExecutionType executionType) {

	this(data, executionType, 0, 0, 0, 0);
    }

    @Override
    public String toString() {
	return format("Image[file {0}, x {1}, x {2}, width {3}, height {4}]", data.getOriginalFilename(), x, y, width, height);
    }
}