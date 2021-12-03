package org.imageconverter.util;

import java.util.function.Supplier;

import net.sourceforge.tess4j.ITesseract;

@FunctionalInterface
public interface TesseractSupplier extends Supplier<ITesseract> {

}
