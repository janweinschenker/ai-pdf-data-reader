package org.weinschenker.tldrdatareader.adapter.out.jpeg.reader;

import org.weinschenker.tldrdatareader.application.port.in.TextExtractor;
import org.weinschenker.tldrdatareader.infrastructure.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@NullMarked
public class JpgOutAdapter implements TextExtractor {

    private final ApplicationProperties applicationProperties;

    @Override
    public String extractText(byte[] fileContent) {

        final BufferedImage bufferedImage = createImageFromBytes(fileContent);
        if (bufferedImage == null) {
            return "";
        }
        return getTextContents(bufferedImage);

    }

    @Nullable
    BufferedImage createImageFromBytes(byte[] imageData) {
        ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
        try {
            return ImageIO.read(bais);
        } catch (IOException e) {
            log.error("Error reading image data", e);
            return null;
        }
    }

    @Override
    public boolean canHandle(String contentType) {
        return contentType.startsWith("image/jpeg");
    }

    public String getTextContents(BufferedImage bufferedImage) {
        try {
            Tesseract tesseract = new Tesseract();
            tesseract.setDatapath(applicationProperties.getTessdataPath()); // path to tessdata dir
            tesseract.setLanguage(applicationProperties.getTessdataLanguage());
            tesseract.setOcrEngineMode(3);
            tesseract.setPageSegMode(3);

            final var s = Optional.ofNullable(tesseract.doOCR(bufferedImage));
            return s.orElse("");

        } catch (TesseractException e) {
            return "";
        }
    }
}
