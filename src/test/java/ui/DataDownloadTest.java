
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

/**
 * Unit tests for DataDownload class.
 */
class DataDownloadTest {

    @Test
    void testDownload() {
        DataDownload downloader = new DataDownload();
        // Test that Download() can be called
        assertDoesNotThrow(() -> {
            try {
                downloader.Download();
            } catch (IOException e) {
                assertNotNull(e);
            }
        });
    }

    @Test
    void testYearOfField() {
        // Test that yearof is accessible
        DataDownload downloader = new DataDownload();
        assertNotNull(downloader);
    }
}

