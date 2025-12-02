package hahaha.service;

import net.minidev.json.JSONObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.JSONObject;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class MomoService {

    private static final String PARTNER_CODE = "MOMO";
    private static final String ACCESS_KEY = "F8BBA842ECF85";
    private static final String SECRET_KEY = "K951B6PE1waDMi640xX08PD3vg6EkVlz";

    private static final String REDIRECT_URL = "http://localhost:8081/api/momo/return";
    private static final String IPN_URL = "http://localhost:8081/api/momo/return";

    private static final String REQUEST_TYPE = "payWithMethod";

    public String createPaymentRequest(String amount) {
        try {
            String requestId = PARTNER_CODE + new Date().getTime();
            String orderId = requestId;
            String orderInfo = "Thanh toán hóa đơn #" + requestId;
            String extraData = "";

            // RAW signature
            String rawSignature = String.format(
                    "accessKey=%s&amount=%s&extraData=%s&ipnUrl=%s&orderId=%s&orderInfo=%s"
                            + "&partnerCode=%s&redirectUrl=%s&requestId=%s&requestType=%s",
                    ACCESS_KEY, amount, extraData, IPN_URL, orderId, orderInfo,
                    PARTNER_CODE, REDIRECT_URL, requestId, REQUEST_TYPE);

            String signature = signHmacSHA256(rawSignature, SECRET_KEY);

            // JSON body
            JSONObject requestBody = new JSONObject();
            requestBody.put("partnerCode", PARTNER_CODE);
            requestBody.put("accessKey", ACCESS_KEY);
            requestBody.put("requestId", requestId);
            requestBody.put("amount", amount);
            requestBody.put("orderId", orderId);
            requestBody.put("orderInfo", orderInfo);
            requestBody.put("redirectUrl", REDIRECT_URL);
            requestBody.put("ipnUrl", IPN_URL);
            requestBody.put("extraData", extraData);
            requestBody.put("requestType", REQUEST_TYPE);
            requestBody.put("signature", signature);
            requestBody.put("lang", "vi");

            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost post = new HttpPost("https://test-payment.momo.vn/v2/gateway/api/create");
            post.setHeader("Content-Type", "application/json");
            post.setEntity(new StringEntity(requestBody.toString(), StandardCharsets.UTF_8));

            CloseableHttpResponse response = client.execute(post);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8));

            StringBuilder result = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            // Phân tích JSON response để lấy payUrl
            JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
            JSONObject jsonResponse = (JSONObject) parser.parse(result.toString());
            if (jsonResponse.containsKey("payUrl")) {
                return jsonResponse.getAsString("payUrl");
            } else {
                throw new Exception("Không tìm thấy URL thanh toán trong response từ MoMo");
            }

        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }


    private static String signHmacSHA256(String data, String key) throws Exception {
        Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmacSHA256.init(secretKey);
        byte[] hash = hmacSHA256.doFinal(data.getBytes(StandardCharsets.UTF_8));

        StringBuilder hex = new StringBuilder();
        for (byte b : hash) {
            String h = Integer.toHexString(0xff & b);
            if (h.length() == 1) hex.append('0');
            hex.append(h);
        }
        return hex.toString();
    }


    public String checkPaymentStatus(String orderId) {
        try {
            String requestId = PARTNER_CODE + new Date().getTime();

            String rawSignature = String.format(
                    "accessKey=%s&orderId=%s&partnerCode=%s&requestId=%s",
                    ACCESS_KEY, orderId, PARTNER_CODE, requestId);

            String signature = signHmacSHA256(rawSignature, SECRET_KEY);

            JSONObject body = new JSONObject();
            body.put("partnerCode", PARTNER_CODE);
            body.put("accessKey", ACCESS_KEY);
            body.put("requestId", requestId);
            body.put("orderId", orderId);
            body.put("signature", signature);
            body.put("lang", "vi");

            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost post = new HttpPost("https://test-payment.momo.vn/v2/gateway/api/query");
            post.setHeader("Content-Type", "application/json");
            post.setEntity(new StringEntity(body.toString(), StandardCharsets.UTF_8));

            CloseableHttpResponse response = client.execute(post);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8));

            StringBuilder result = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) result.append(line);

            return result.toString();
        } catch (Exception e) {
            return "{\"error\":\"" + e.getMessage() + "\"}";
        }
    }
}

