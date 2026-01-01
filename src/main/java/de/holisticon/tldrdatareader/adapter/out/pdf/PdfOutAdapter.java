package de.holisticon.tldrdatareader.adapter.out.pdf;

import de.holisticon.tldrdatareader.application.port.in.TextExtractor;
import de.holisticon.tldrdatareader.infrastructure.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.content.Media;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@NullMarked
public class PdfOutAdapter implements TextExtractor {

    private final ApplicationProperties applicationProperties;

    @Override
    public boolean canHandle(String contentType) {
        return contentType.startsWith("application/pdf");
    }

    /**
     * Extracts JSON data from a PDF document.
     *
     * @param fileContent the URL of the PDF document to extract data from
     * @return a list of Part objects extracted from the PDF
     */
    @Override
    public String extractText(byte[] fileContent) {
        return getTextContent(fileContent) + extractTextFromMedia(fileContent);
    }

    private String getTextContent(final byte[] fileContent) {
        final var collect = new PagePdfDocumentReader(new ByteArrayResource(fileContent))
                .get()
                .stream().map(Document::getText)
                .collect(Collectors.joining(System.lineSeparator()));
        return StringUtils.trimToEmpty(collect);
    }

    String extractTextFromMedia(byte[] fileContent) {
        return new PagePdfDocumentReader(new ByteArrayResource(fileContent))
                .get()
                .stream()
                .map(Document::getMedia)
                .filter(Objects::nonNull)
                .filter(it -> it.getData() != null)
                .map(Media::getDataAsByteArray)
                .map(this::getTextContents)
                .collect(Collectors.joining())
                .trim();
    }

    String getTextContents(byte[] imageData) {
        try {
            Tesseract tesseract = new Tesseract();
            tesseract.setDatapath(applicationProperties.getTessdataPath()); // path to tessdata dir
            tesseract.setLanguage(applicationProperties.getTessdataLanguage());

            final var s = tesseract.doOCR(createImageFromBytes(imageData));
            final var optional = Optional.ofNullable(s);
            return optional.orElse("");

        } catch (TesseractException tesseractException) {
            log.error(tesseractException.getMessage(), tesseractException);
            return "";
        }
    }

    @Nullable
    BufferedImage createImageFromBytes(byte[] imageData) {
        ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
        try {
            return ImageIO.read(bais);
        } catch (IOException e) {
            return null;
        }
    }


}
