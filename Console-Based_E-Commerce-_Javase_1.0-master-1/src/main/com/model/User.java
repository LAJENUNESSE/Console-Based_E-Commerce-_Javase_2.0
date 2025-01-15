/**
 * @file User.java
 * @brief 用户实体类，定义用户的基本属性和操作
 * @package main.com.model
 */
package main.com.model;

import java.security.MessageDigest; /**
 * @class User
 * @brief 用户实体类，表示电子商城系统中的用户信息
 * @details 包含用户的基本属性、获取器、设置器和登录验证方法
 */
public class User {
    /** @brief 用户名 */
    private String username; // 用户名
    /** @brief 密码 */
    private String password; // 密码
    /** @brief 电子邮件 */
    private String email;    // 电子邮件
    /** @brief 电话号码 */
    private String phone;    // 电话号码

    /**
     * @brief 构造函数
     * @param username 用户名
     * @param password 密码
     * @param email 电子邮件
     * @param phone 电话号码
     * @details 创建一个新的用户对象，初始化用户的基本信息
     */
    public User(String username, String password, String email, String phone) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
    }

    /**
     * @brief 获取用户名
     * @return 用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * @brief 设置用户名
     * @param username 新的用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @brief 获取密码
     * @return 用户密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * @brief 设置密码
     * @param password 新的密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @brief 获取电子邮件
     * @return 用户电子邮件
     */
    public String getEmail() {
        return email;
    }

    /**
     * @brief 设置电子邮件
     * @param email 新的电子邮件
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @brief 获取电话号码
     * @return 用户电话号码
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @brief 设置电话号码
     * @param phone 新的电话号码
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @brief 用户登录验证方法
     * @param inputPassword 输入的密码
     * @return 登录是否成功
     * @details 比较输入的密码是否与用户保存的密码相同
     */
    public boolean login(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    /**
     * @brief 重写toString方法，提供用户信息的字符串表示
     * @return 用户信息的字符串描述
     * @details 返回包含用户名、电子邮件和电话号码的字符串
     */
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }

    /**
     * @brief 对用户密码进行MD5加密
     * @param str 待加密字符串
     * @return 16进制加密字符串
     */
    public static String encryptToMD5(String str) {
        String hexStr = ""; // 加密后的16进制字符串
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5"); // 此 MessageDigest 类为应用程序提供信息摘要算法的功能
            byte[] digest = md5.digest(str.getBytes("utf-8")); // 转换为MD5码
            hexStr = bytesToHex(digest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hexStr;
    }

    /**
     * @brief byte[] to hex string
     * @param bytes 带转换原byte数据
     * @return 16进制String字符串
     */
    public static String bytesToHex(byte[] bytes) {
        final char[] HEX_CHAR = "0123456789abcdef".toCharArray(); // 十六进制字符集
        char[] buf = new char[bytes.length * 2]; // 一个byte为8位，可用两个十六进制位标识
        int a = 0;
        int index = 0;
        for(byte b : bytes) { // 使用除与取余进行转换
            if(b < 0) {
                a = 256 + b;
            } else {
                a = b;
            }
            buf[index++] = HEX_CHAR[a / 16];
            buf[index++] = HEX_CHAR[a % 16];
        }
        return new String(buf);
    }
}