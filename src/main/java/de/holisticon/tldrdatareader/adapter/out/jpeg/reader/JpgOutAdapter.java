package de.holisticon.tldrdatareader.adapter.out.jpeg.reader;

import de.holisticon.tldrdatareader.application.port.in.TextExtractor;
import de.holisticon.tldrdatareader.infrastructure.ApplicationProperties;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JpgOutAdapter implements TextExtractor {

    private final @NonNull ApplicationProperties applicationProperties;

    @Override
    public String extractText(byte[] fileContent) {

        BufferedImage bufferedImage = createImageFromBytes(fileContent);
        return getTextContents(bufferedImage);

    }

    private BufferedImage createImageFromBytes(byte[] imageData) {
        ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
        try {
            return ImageIO.read(bais);
        } catch (IOException e) {
            throw new RuntimeException(e);
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
            throw new RuntimeException(e);
        }
    }
}
