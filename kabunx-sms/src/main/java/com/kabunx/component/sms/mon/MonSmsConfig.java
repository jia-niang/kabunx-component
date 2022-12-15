package com.kabunx.component.sms.mon;

import lombok.Data;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Data
public class MonSmsConfig implements Serializable {
    private Boolean enabled;
    private String userId;
    private String pwd;
    private String key;
    private List<String> urls;

    @Nullable
    public String getMD5Pwd(String timestamp) {
        String mixPwd = userId.toUpperCase() + "00000000" + pwd + timestamp;
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(mixPwd.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException ex) {
            return null;
        }
        byte[] byteArray = messageDigest.digest();
        // 加密后的字符串
        StringBuilder msb = new StringBuilder();
        for (byte b : byteArray) {
            if (Integer.toHexString(0xFF & b).length() == 1) {
                msb.append("0").append(Integer.toHexString(0xFF & b));
            } else {
                msb.append(Integer.toHexString(0xFF & b));
            }
        }
        return msb.toString();
    }
}
