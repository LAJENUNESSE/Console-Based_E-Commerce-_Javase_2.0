/**
 * @file UserService.java
 * @brief 用户服务类，提供用户相关的操作
 * @package main.com.service
 */
package main.com.service;

import main.com.model.Good;
import main.com.model.User;
import main.com.util.TxtUtil;
import main.com.util.UserUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @class UserService
 * @brief 处理用户操作的服务类
 * @details 提供用户注册、登录、购物车、结算等功能
 */
public class UserService {

    /** @brief 系统输入扫描器 */
    private Scanner scanner = new Scanner(System.in);
    /** @brief 当前登录用户的用户名 */
    private String loggedInUsername;
    /** @brief 用户购物车 */
    private List<CartItem> cart;

    /**
     * @brief 构造函数，初始化购物车
     */
    public UserService() {
        cart = new ArrayList<>(); // 初始化购物车
    }

    /**
     * @brief 根据商品ID从购物车获取商品对象
     * @param id 购物车商品ID
     * @return 匹配的购物车商品对象，如果未找到则返回 null
     */
    public CartItem getGoodCtId(String id) {
        for (CartItem cartItem : cart) {
            if (cartItem.getGood().getId().equals(id)) {
                return cartItem;
            }
        }
        return null;
    }

    /**
     * @brief 用户注册方法
     * @details 提示用户输入注册信息并保存到文件
     */
    public void register() {
        System.out.print("请输入用户名: ");
        String username = scanner.nextLine();
        username = User.encryptToMD5(username);
        System.out.print("请输入密码: ");
        String password = scanner.nextLine();
        password = User.encryptToMD5(password);
        System.out.print("请输入电子邮件: ");
        String email = scanner.nextLine();
        email = User.encryptToMD5(email);
        System.out.print("请输入电话号码: ");
        String phone = scanner.nextLine();
        phone = User.encryptToMD5(phone);

        User user = new User(username, password, email, phone);
        UserUtil.addUserToTxt(user);
        System.out.println("注册成功！");
    }

    /**
     * @brief 用户登录方法
     * @return 登录是否成功
     * @details 验证用户名和密码，成功则保存当前登录用户
     */
    public boolean login() {
        System.out.print("请输入用户名: ");
        String username = scanner.nextLine();
        System.out.print("请输入密码: ");
        String password = scanner.nextLine();

        if (UserUtil.validateUser(username, password)) {
            loggedInUsername = username; // 保存登录的用户名
            return true; // 登录成功
        }
        return false; // 登录失败
    }

    /**
     * @brief 获取当前登录用户名
     * @return 当前登录的用户名
     */
    public String getLoggedInUsername() {
        return loggedInUsername; // 获取当前登录的用户名
    }

    /**
     * @brief 查看用户已购买的商品
     * @param username 用户名
     * @details 从文件中读取并显示用户购买的商品
     */
    public static void viewPurchasedGoods(String username) {
        List<String> purchasedGoods = TxtUtil.getPurchasedGoodsByUser(username);
        if (purchasedGoods.isEmpty()) {
            System.out.println("您尚未购买任何商品。");
        } else {
            System.out.println("您购买的商品列表：");
            for (String goodInfo : purchasedGoods) {
                System.out.println(goodInfo);
            }
        }
    }

    /**
     * @brief 将商品添加到购物车
     * @param goodId   商品ID
     * @param quantity 购买数量
     * @details 检查库存并将商品添加到购物车
     */
    public void addGoodToCart(String goodId, int quantity) {
        Good good = TxtUtil.getGoodById(goodId);

        if (good != null) {
            if (quantity > 0) { // 检查数量有效性
                int availableStock = good.getStock(); // 获取可用库存
                if (availableStock >= quantity) { // 检查库存是否足够
                    cart.add(new CartItem(good, quantity)); // 添加商品到购物车
                    good.reduceStock(quantity); // 减少库存数量并更新文件
                    System.out.println("成功将 " + quantity + " 件 " + good.getName() + " 添加到购物车。");
                } else {
                    System.out.println("库存不足，当前可用数量: " + availableStock);
                }
            } else {
                System.out.println("购买数量必须大于0。");
            }
        } else {
            System.out.println("未找到该商品！请检查商品ID。");
        }
    }

    /**
     * @brief 修改购物车中的商品
     * @param goodId   商品ID
     * @param quantity 要修改的数量
     * @details 检查库存并将商品返回到库存
     */
    public void modifyGoodOfCart(String goodId, int quantity) {
        CartItem goodCart = getGoodCtId(goodId);

        if (goodCart != null) {
            if (quantity >= 0) { // 检查数量有效性
                int cartQuantity = 0;
                for (CartItem item : cart) {
                    if (item.getGood().getId().equals(goodId)) {
                        cartQuantity = item.quantity;
                    }
                }
                if (quantity == 0) { // 数量为零则将商品从购物车中删除
                    cart.remove(goodCart); // 将商品从购物车中删除
                    System.out.println("成功将 " + goodCart.getGood().getName() + " 从购物车中删除。");
                } else if (quantity < cartQuantity) {
                    goodCart.reduceStock(cartQuantity - quantity); // 减少所选商品数量并更新购物车
                    goodCart.getGood().addStock(cartQuantity - quantity); // 增加库存数量并更新文件
                } else if (quantity > cartQuantity) {
                    goodCart.addStock(quantity - cartQuantity); // 增加所选商品数量并更新购物车
                    goodCart.getGood().reduceStock(quantity - cartQuantity); // 减少库存并更新文件
                } else {
                    System.out.println("商品数量与原定数量相同！");
                }
            } else {
                System.out.println("修改数量必须大于等于0。");
            }
        } else {
            System.out.println("请先添加商品至购物车！");
        }
    }

    /**
     * @brief 查看购物车中的商品
     * @details 显示购物车中所有商品及其数量
     */
    public void viewCart() {
        if (cart.isEmpty()) {
            System.out.println("购物车为空。");
        } else {
            System.out.println("购物车中的商品：");
            for (CartItem item : cart) {
                System.out.println(item); // "Id:'" + item.getId() + "' - " + item.getName() + " - 数量: " +
                                          // item.getQuantity()
            }
        }
    }

    /**
     * @brief 购物车结算方法
     * @details 计算总价、应用折扣、确认购买并记录购买信息
     */
    public void checkout() {
        if (cart.isEmpty()) {
            System.out.println("购物车为空，无法结算。");
            return;
        }
        System.out.println("购物车中的商品：");
        double totalPrice = 0; // 初始化总价格
        for (CartItem item : cart) {
            double itemTotalPrice = item.getGood().getPrice() * item.getQuantity(); // 每个商品的总价格
            totalPrice += itemTotalPrice; // 累加到总价格
            System.out.println(
                    item.getGood().getName() + " - 数量: " + item.getQuantity() + ", 小计: " + itemTotalPrice + "元");
        }
        System.out.println("总价格: " + totalPrice + "元"); // 显示总价格
        double discountedPrice;
        if (totalPrice < 1000) {
            double discountRate = 0.7 + (Math.random() * 0.2); // 随机生成 0.7 到 0.9 的折扣率
            discountedPrice = totalPrice * discountRate;
            System.out.printf("您享受的随机折扣率为: %.2f%% \n", discountRate * 100);
        } else {
            int discountCount = (int) (totalPrice / 1000);
            double discountRate = 1 - (0.02 * discountCount); // 每超过1000元减2%
            discountedPrice = totalPrice * discountRate;
            System.out.printf("您享受的折扣率为: %.2f%% \n", discountRate * 100);
        }
        System.out.printf("折后总价格: %.2f元\n", discountedPrice); // 显示折扣后的总价格
        System.out.print("您确认要结算吗？(y/n): ");
        String confirm = scanner.nextLine();
        if (confirm.equalsIgnoreCase("y")) {
            for (CartItem item : cart) {
                TxtUtil.addPurchasedGoodForUser(loggedInUsername, item.getGood(), item.getQuantity());
            }
            System.out.println("结算成功！感谢您的购买。");
            cart.clear(); // 结算后清空购物车
        } else {
            System.out.println("结算已取消。");
        }
    }

    /**
     * @brief 找回密码方法
     * @details 通过验证用户信息来重置密码
     */
    public void forgotPassword() {
        System.out.print("请输入用户名: ");
        String username = scanner.nextLine();
        // 获取用户的信息
        User user = UserUtil.getUserByUsername(username);
        if (user == null) {
            System.out.println("未找到该用户。");
            return;
        }
        System.out.println("请确认您的身份：");
        System.out.print("输入注册时使用的电子邮件: ");
        String email = scanner.nextLine();
        System.out.print("输入注册时使用的电话号码: ");
        String phone = scanner.nextLine();
        // 验证用户输入的电子邮箱和手机号码
        if (email.equals(user.getEmail()) && phone.equals(user.getPhone())) {
            System.out.println("身份验证成功，您可以重置您的密码。");
            System.out.print("请输入您的新密码: ");
            String newPassword = scanner.nextLine();
            user.setPassword(newPassword);
            UserUtil.updateUserPassword(user);
            System.out.println("您的密码已成功重置！");
        } else {
            System.out.println("身份验证失败，请检查您输入的信息。");
        }
    }

    /**
     * @class CartItem
     * @brief 购物车内部类，用于存储购物车中的商品和数量
     * @details 封装商品对象和购买数量
     */
    private class CartItem {
        /** @brief 购物车中的商品 */
        private Good good;
        /** @brief 商品数量 */
        private int quantity;

        /**
         * @brief 购物车项目构造函数
         * @param good     商品对象
         * @param quantity 购买数量
         */
        public CartItem(Good good, int quantity) {
            this.good = good;
            this.quantity = quantity;
        }

        /**
         * @brief 获取商品对象
         * @return 商品对象
         */
        public Good getGood() {
            return good;
        }

        /**
         * @brief 获取商品数量
         * @return 商品数量
         */
        public int getQuantity() {
            return quantity;
        }

        /**
         * @brief 获取商品ID
         * @return 商品ID
         */
        public String getId() {
            return good.getId();
        }

        /**
         * @brief 获取商品名称
         * @return 商品名称
         */
        public String getName() {
            return good.getName();
        }

        /**
         * @brief 从购物车中减少商品数量
         * @param quantity 要减少的商品数量
         * @details 检查购物车商品数量是否充足，如果充足则减少商品数量
         */
        public void reduceStock(int quantity) {
            if (quantity <= this.quantity) {
                this.quantity -= quantity; // 减少购物车中商品数量
            } else {
                System.out.println("购物车中剩余商品数量不足，无法减少该数量。");
            }
        }

        /**
         * @brief 从购物车中增加商品数量
         * @param quantity 要增加的商品数量
         * @details 增加商品数量
         */
        public void addStock(int quantity) {
            if (quantity >= this.quantity) {
                this.quantity += quantity; // 增加购物车中商品数量
            }
        }

        /**
         * @brief 重写toString方法，提供商品信息的字符串表示
         * @return 商品信息的字符串描述
         */
        @Override
        public String toString() {
            return "商品{" +
                    "id='" + getId() + '\'' +
                    ", 名称='" + getName() + '\'' +
                    ", 数量='" + getQuantity() + '\'' +
                    '}';
        }
    }
}