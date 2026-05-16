package org.csu.mypetstorebackend.utils;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 简单的JWT工具类 - 用于生成和验证Token
 */
public class JwtUtil {
    private static final String SECRET_KEY = "MyPetStore-Secret-Key-2025-For-JWT-Token-Generation";
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000; // 24小时

    /**
     * 生成Token
     */
    public static String generateToken(String username, int userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("userId", userId);
        claims.put("iat", System.currentTimeMillis());
        claims.put("exp", System.currentTimeMillis() + EXPIRATION_TIME);
        return createToken(claims);
    }

    /**
     * 创建Token
     */
    private static String createToken(Map<String, Object> claims) {
        // 简化的Token生成 - 实际生产环境应使用jwt库如 jjwt
        String header = Base64.getEncoder().encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes());
        String payload = Base64.getEncoder().encodeToString(claimsToJson(claims).getBytes());
        String signature = Base64.getEncoder().encodeToString(
                (header + "." + payload + "." + SECRET_KEY).getBytes()
        );
        return header + "." + payload + "." + signature;
    }

    /**
     * 验证Token
     */
    public static boolean validateToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return false;
            }

            String payload = parts[1];
            String payloadJson = new String(Base64.getDecoder().decode(payload));
            
            // 检查过期时间
            if (payloadJson.contains("\"exp\":")) {
                long expTime = extractExpirationTime(payloadJson);
                return expTime > System.currentTimeMillis();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从Token中提取username
     */
    public static String extractUsername(String token) {
        try {
            String[] parts = token.split("\\.");
            String payload = parts[1];
            String payloadJson = new String(Base64.getDecoder().decode(payload));
            
            int start = payloadJson.indexOf("\"username\":\"") + 12;
            int end = payloadJson.indexOf("\"", start);
            return payloadJson.substring(start, end);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从Token中提取userId
     */
    public static Integer extractUserId(String token) {
        try {
            String[] parts = token.split("\\.");
            String payload = parts[1];
            String payloadJson = new String(Base64.getDecoder().decode(payload));
            
            int start = payloadJson.indexOf("\"userId\":") + 9;
            int end = payloadJson.indexOf(",", start);
            if (end == -1) {
                end = payloadJson.indexOf("}", start);
            }
            return Integer.parseInt(payloadJson.substring(start, end).trim());
        } catch (Exception e) {
            return null;
        }
    }

    private static long extractExpirationTime(String payloadJson) {
        int start = payloadJson.indexOf("\"exp\":") + 6;
        int end = payloadJson.indexOf(",", start);
        if (end == -1) {
            end = payloadJson.indexOf("}", start);
        }
        return Long.parseLong(payloadJson.substring(start, end).trim());
    }

    private static String claimsToJson(Map<String, Object> claims) {
        StringBuilder json = new StringBuilder("{");
        claims.forEach((key, value) -> {
            if (json.length() > 1) {
                json.append(",");
            }
            json.append("\"").append(key).append("\":");
            if (value instanceof String) {
                json.append("\"").append(value).append("\"");
            } else {
                json.append(value);
            }
        });
        json.append("}");
        return json.toString();
    }
}

