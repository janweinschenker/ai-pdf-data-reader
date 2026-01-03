package org.weinschenker.tldrdatareader.infrastructure;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.lang3.StringUtils;

import java.awt.image.BufferedImage;
import java.util.Optional;

/**
 * Utility class for performing OCR using Tesseract.
 */
@Slf4j
public class TesseractOcr {

    private TesseractOcr() {
        // private constructor to prevent instantiation
    }

    /**
     * Extracts text content from a BufferedImage using Tesseract OCR.
     *
     * @param bufferedImage the image to extract text from
     * @param dataPath      the path to the Tesseract data files
     * @param language      the language to use for OCR, e.g., "eng" or "deu"
     * @return the extracted text content
     */
    public static String getTextContents(final BufferedImage bufferedImage, final String dataPath, final String language) {
        try {
            final Tesseract tesseract = new Tesseract();
            tesseract.setDatapath(dataPath); // path to tessdata dir
            tesseract.setLanguage(language);
            tesseract.setOcrEngineMode(3);
            tesseract.setPageSegMode(3);

            final var s = Optional.ofNullable(tesseract.doOCR(bufferedImage));
            return s.orElse(StringUtils.EMPTY);

        } catch (final TesseractException e) {
            log.error("Error reading image data", e);
            return StringUtils.EMPTY;
        }
    }
}
