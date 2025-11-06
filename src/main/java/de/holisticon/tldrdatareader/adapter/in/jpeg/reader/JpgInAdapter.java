package de.holisticon.tldrdatareader.adapter.in.jpeg.reader;

import de.holisticon.tldrdatareader.adapter.in.textextractor.TextExtractor;
import de.holisticon.tldrdatareader.application.port.in.DataExtractionInPort;
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

@Service
@RequiredArgsConstructor
public class JpgInAdapter implements TextExtractor {

    private final @NonNull DataExtractionInPort extractionInPort;
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

            final var s = tesseract.doOCR(bufferedImage);
            final var parts = extractionInPort.extractDataFromDocuments(s);
            return parts.orElse("");

        } catch (TesseractException e) {
            throw new RuntimeException(e);
        }
    }
}
