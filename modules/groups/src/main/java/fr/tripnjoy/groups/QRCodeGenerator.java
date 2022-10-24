package fr.tripnjoy.groups;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Component
public class QRCodeGenerator {
    private static int QR_CODE_DIMENSION = 200;
    private static String CHARSET = "UTF-8";

    public String generateQRCode(String data)
            throws WriterException, IOException {
        BitMatrix matrix = new MultiFormatWriter().encode(
                new String(data.getBytes(CHARSET), CHARSET),
                BarcodeFormat.QR_CODE, QR_CODE_DIMENSION, QR_CODE_DIMENSION);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "PNG", out);
        return Base64.getEncoder().encodeToString(out.toByteArray());
    }
}
