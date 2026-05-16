package org.csu.mypetstorebackend.controller.captcha;

import org.csu.mypetstorebackend.common.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/captcha")
public class CaptchaController {

    private final Map<String, String> captchaCache = new HashMap<>();

    @GetMapping
    public ApiResponse<Object> getCaptcha() {
        String captchaId = UUID.randomUUID().toString();
        String captchaCode = String.format("%04d", new Random().nextInt(10000));
        String sessionId = UUID.randomUUID().toString();

        // Store captcha code for verification (in production, use Redis or database)
        captchaCache.put(captchaId, captchaCode);

        // Generate a simple base64 encoded image (in production, generate actual image)
        String captchaImage = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==";

        Map<String, String> response = new HashMap<>();
        response.put("captchaId", captchaId);
        response.put("captchaImage", captchaImage);
        response.put("sessionId", sessionId);

        return ApiResponse.success(response);
    }

    @PostMapping("/verify")
    public ApiResponse<Object> verifyCaptcha(@RequestBody Map<String, String> request) {
        String captchaId = request.get("captchaId");
        String captchaCode = request.get("captchaCode");

        if (captchaId == null || captchaCode == null) {
            return ApiResponse.badRequest("Missing captcha ID or code");
        }

        String storedCode = captchaCache.get(captchaId);
        if (storedCode == null) {
            return ApiResponse.badRequest("Invalid captcha ID");
        }

        if (!storedCode.equals(captchaCode)) {
            return ApiResponse.badRequest("Incorrect captcha code");
        }

        // Remove used captcha
        captchaCache.remove(captchaId);

        return ApiResponse.success("Captcha verified successfully", null);
    }
}
