package org.weinschenker.tldrdatareader.adapter.out.jpeg.reader;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.weinschenker.tldrdatareader.infrastructure.ApplicationProperties;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.List;

import javax.imageio.ImageIO;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpgOutAdapterTest {

    @InjectMocks
    private JpgOutAdapter sut;

    @Mock
    private ApplicationProperties props;

    @Test
    @DisplayName("extractText: should return empty string when image bytes cannot be read")
    void extractText_shouldReturnEmptyWhenImageInvalid() {
        // given
        final byte[] invalid = new byte[0];

        // when
        final String result = sut.extractText(invalid);

        // then
        assertEquals("", result);
    }

    @Test
    @DisplayName("getTextContents: should return OCR result when Tesseract succeeds")
    void getTextContents_shouldReturnOcrResult_whenTesseractSucceeds() throws Exception {
        // given
        when(props.getTessdataPath()).thenReturn("/tmp/tessdata");
        when(props.getTessdataLanguage()).thenReturn("eng");

        final BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);

        // when // then
        try (MockedConstruction<Tesseract> mocked = mockConstruction(Tesseract.class,
                (mock, context) -> {
                    when(mock.doOCR(any(BufferedImage.class))).thenReturn("recognized text");
                })) {
            // when
            final String result = sut.getTextContents(img);

            // then
            assertEquals("recognized text", result);
            List<Tesseract> constructed = mocked.constructed();
            assertEquals(1, constructed.size());
            Tesseract used = constructed.getFirst();
            verify(used).setDatapath("/tmp/tessdata");
            verify(used).setLanguage("eng");
            verify(used).setOcrEngineMode(3);
            verify(used).setPageSegMode(3);
        }
    }

    @Test
    @DisplayName("getTextContents: should return empty string when Tesseract throws")
    void getTextContents_shouldReturnEmptyWhenTesseractThrows() throws Exception {
        // given
        when(props.getTessdataPath()).thenReturn("/tmp/tessdata");
        when(props.getTessdataLanguage()).thenReturn("eng");

        final BufferedImage img = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);

        // when // then
        try (MockedConstruction<Tesseract> mocked = mockConstruction(Tesseract.class,
                (mock, context) -> {
                    when(mock.doOCR(any(BufferedImage.class))).thenThrow(new TesseractException("fail"));
                })) {
            // when
            final String result = sut.getTextContents(img);

            // then
            assertEquals("", result);
        }
    }

    @Test
    @DisplayName("canHandle: should return true for image/jpeg and false for other content types")
    void canHandle_shouldDetectJpeg() {
        // given

        // when / then
        assertTrue(sut.canHandle("image/jpeg"));
        assertTrue(sut.canHandle("image/jpeg; charset=utf-8"));
        assertFalse(sut.canHandle("application/pdf"));
    }

    @Test
    @DisplayName("createImageFromBytes: should create a BufferedImage from valid jpeg bytes")
    void createImageFromBytes_shouldReturnImageForValidJpegBytes() throws Exception {
        // given

        final BufferedImage source = new BufferedImage(8, 8, BufferedImage.TYPE_INT_RGB);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(source, "jpg", baos);
        final byte[] jpegBytes = baos.toByteArray();

        // when
        final BufferedImage result = sut.createImageFromBytes(jpegBytes);

        // then
        assertNotNull(result);
        assertEquals(8, result.getWidth());
        assertEquals(8, result.getHeight());
    }
}

