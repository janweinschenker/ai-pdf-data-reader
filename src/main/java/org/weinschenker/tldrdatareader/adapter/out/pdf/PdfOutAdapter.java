package org.weinschenker.tldrdatareader.adapter.out.pdf;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.jspecify.annotations.NullMarked;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.weinschenker.tldrdatareader.application.port.in.TextExtractor;
import org.weinschenker.tldrdatareader.infrastructure.ApplicationProperties;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
        return contentType.startsWith(MediaType.APPLICATION_PDF_VALUE);
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
        return extractImagesFromPdf(fileContent)
                .stream()
                .filter(it -> it.getData() != null)
                .map(this::getTextContents)
                .collect(Collectors.joining())
                .trim();
    }

    List<BufferedImage> extractImagesFromPdf(byte[] fileContent) {
        final List<BufferedImage> images = new ArrayList<>();
        try (PDDocument document = Loader.loadPDF(fileContent)) {
            for (PDPage page : document.getPages()) {
                PDResources resources = page.getResources();
                for (COSName xObjectName : resources.getXObjectNames()) {
                    var xObject = resources.getXObject(xObjectName);
                    if (xObject instanceof PDImageXObject imageXObject) {
                        BufferedImage image = imageXObject.getImage();
                        images.add(image);
                    }
                }
            }
        } catch (IOException e) {
            log.error("Error extracting images from PDF", e);
        }

        return images;
    }

    public String getTextContents(BufferedImage bufferedImage) {
        try {
            final Tesseract tesseract = new Tesseract();
            tesseract.setDatapath(applicationProperties.getTessdataPath()); // path to tessdata dir
            tesseract.setLanguage(applicationProperties.getTessdataLanguage());
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
