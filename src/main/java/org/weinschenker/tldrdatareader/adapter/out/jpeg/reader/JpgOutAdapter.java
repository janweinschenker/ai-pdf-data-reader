package org.weinschenker.tldrdatareader.adapter.out.jpeg.reader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.weinschenker.tldrdatareader.application.port.in.TextExtractor;
import org.weinschenker.tldrdatareader.infrastructure.ApplicationProperties;
import org.weinschenker.tldrdatareader.infrastructure.TesseractOcr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

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
            return StringUtils.EMPTY;
        }
        return getTextContents(bufferedImage);
    }

    @Nullable
    BufferedImage createImageFromBytes(final byte[] imageData) {
        final ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
        try {
            return ImageIO.read(bais);
        } catch (IOException e) {
            log.error("Error reading image data", e);
            return null;
        }
    }

    @Override
    public boolean canHandle(final String contentType) {
        return contentType.startsWith(MediaType.IMAGE_JPEG_VALUE);
    }

    String getTextContents(BufferedImage bufferedImage) {
        return TesseractOcr.getTextContents(bufferedImage, applicationProperties.getTessdataPath(), applicationProperties.getTessdataLanguage());
    }
}
