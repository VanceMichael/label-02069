package com.secondhand.config;

import com.secondhand.entity.Category;
import com.secondhand.entity.Product;
import com.secondhand.entity.User;
import com.secondhand.mapper.CategoryMapper;
import com.secondhand.mapper.ProductMapper;
import com.secondhand.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public void run(String... args) {
        Long userCount = userMapper.selectCount(null);
        if (userCount > 0) {
            log.info("Data already initialized, skipping.");
            return;
        }
        log.info("Initializing data...");
        initUsers();
        initCategories();
        initProducts();
        log.info("Data initialization complete.");
    }

    private void initUsers() {
        String pwd = encoder.encode("123456");
        List.of(
            createUser("admin", pwd, "\u7cfb\u7edf\u7ba1\u7406\u5458", "13800000000", new BigDecimal("99999"), "ADMIN"),
            createUser("user1", pwd, "\u5f20\u4e09", "13800000001", new BigDecimal("5000"), "USER"),
            createUser("user2", pwd, "\u674e\u56db", "13800000002", new BigDecimal("3000"), "USER"),
            createUser("user3", pwd, "\u738b\u4e94", "13800000003", new BigDecimal("8000"), "USER"),
            createUser("user4", pwd, "\u8d75\u516d", "13800000004", new BigDecimal("2000"), "USER"),
            createUser("seller1", pwd, "\u6570\u7801\u5c0f\u738b", "13800000005", new BigDecimal("10000"), "USER"),
            createUser("seller2", pwd, "\u670d\u9970\u8fbe\u4eba", "13800000006", new BigDecimal("8000"), "USER"),
            createUser("seller3", pwd, "\u4e66\u9999\u9601\u4e3b", "13800000007", new BigDecimal("6000"), "USER")
        ).forEach(userMapper::insert);
        log.info("Users initialized: 8");
    }

    private User createUser(String username, String password, String nickname, String phone, BigDecimal balance, String role) {
        User u = new User();
        u.setUsername(username);
        u.setPassword(password);
        u.setNickname(nickname);
        u.setPhone(phone);
        u.setBalance(balance);
        u.setRole(role);
        u.setStatus(1);
        return u;
    }

    private void initCategories() {
        List.of(
            createCategory("\u6570\u7801\u7535\u5b50", "Monitor", 1),
            createCategory("\u670d\u9970\u978b\u5305", "ShoppingBag", 2),
            createCategory("\u56fe\u4e66\u6559\u6750", "Reading", 3),
            createCategory("\u751f\u6d3b\u7528\u54c1", "House", 4),
            createCategory("\u8fd0\u52a8\u6237\u5916", "Football", 5),
            createCategory("\u5176\u4ed6", "More", 6)
        ).forEach(categoryMapper::insert);
        log.info("Categories initialized: 6");
    }

    private Category createCategory(String name, String icon, int sort) {
        Category c = new Category();
        c.setName(name);
        c.setIcon(icon);
        c.setSort(sort);
        c.setStatus(1);
        return c;
    }

    private void initProducts() {
        // Category 1: Digital Electronics (seller_id=6)
        long sid1 = 6; long cat1 = 1; String cat1Name = "\u6570\u7801\u7535\u5b50";
        insertProduct(sid1, "iPhone 13 Pro 256G \u8fdc\u5cf0\u84dd",
            "\u81ea\u7528iPhone 13 Pro\uff0c256G\u8fdc\u5cf0\u84dd\u8272\uff0c\u8d2d\u4e8e2022\u5e743\u6708\uff0c\u7535\u6c60\u5065\u5eb792%\uff0c\u65e0\u78d5\u78b0\uff0c\u5c4f\u5e55\u5b8c\u7f8e\u3002",
            4999, 8999, cat1, cat1Name, "https://picsum.photos/400/400?random=101", 9, 128);
        insertProduct(sid1, "MacBook Pro 14\u5bf8 M1 Pro",
            "2021\u6b3eMacBook Pro 14\u5bf8\uff0cM1 Pro\u82af\u7247\uff0c16G+512G\uff0c\u6df1\u7a7a\u7070\uff0c\u6210\u8272\u5f88\u65b0\u3002",
            9999, 14999, cat1, cat1Name, "https://picsum.photos/400/400?random=102", 9, 256);
        insertProduct(sid1, "AirPods Pro 2\u4ee3",
            "\u82f9\u679cAirPods Pro\u7b2c\u4e8c\u4ee3\uff0c\u5e26MagSafe\u5145\u7535\u76d2\uff0c\u964d\u566a\u6548\u679c\u5f88\u597d\u3002",
            1299, 1899, cat1, cat1Name, "https://picsum.photos/400/400?random=103", 9, 89);
        insertProduct(sid1, "iPad Air 5 256G WiFi\u7248",
            "iPad Air 5\u4ee3\uff0c256G WiFi\u7248\uff0c\u661f\u5149\u8272\uff0c\u51e0\u4e4e\u5168\u65b0\u3002",
            4299, 5999, cat1, cat1Name, "https://picsum.photos/400/400?random=104", 10, 167);
        insertProduct(sid1, "\u7d22\u5c3cWH-1000XM4\u964d\u566a\u8033\u673a",
            "\u7d22\u5c3c\u65d7\u8230\u964d\u566a\u8033\u673aWH-1000XM4\uff0c\u9ed1\u8272\uff0c\u97f3\u8d28\u5f88\u68d2\u3002",
            1599, 2499, cat1, cat1Name, "https://picsum.photos/400/400?random=105", 8, 95);
        insertProduct(sid1, "\u5c0f\u7c7313 Ultra 512G",
            "\u5c0f\u7c7313 Ultra\uff0c512G\u9ed1\u8272\uff0c\u5f95\u5361\u5f71\u50cf\u65d7\u8230\u3002",
            3999, 5999, cat1, cat1Name, "https://picsum.photos/400/400?random=106", 9, 78);

        // Category 2: Clothing (seller_id=7)
        long sid2 = 7; long cat2 = 2; String cat2Name = "\u670d\u9970\u978b\u5305";
        insertProduct(sid2, "Nike Air Jordan 1 \u9ed1\u7ea2\u914d\u8272 42\u7801",
            "\u7ecf\u5178AJ1\u9ed1\u7ea2\u914d\u8272\uff0c42\u7801\uff0c\u6b63\u54c1\uff0c\u7a7f\u8fc73\u6b21\u3002",
            899, 1299, cat2, cat2Name, "https://picsum.photos/400/400?random=201", 9, 234);
        insertProduct(sid2, "Levi's 501\u7ecf\u5178\u76f4\u7b52\u725b\u4ed4\u88e4",
            "Levi's 501\u539f\u8272\u725b\u4ed4\u88e4\uff0c32\u7801\uff0c\u7ecf\u5178\u76f4\u7b52\u7248\u578b\u3002",
            299, 699, cat2, cat2Name, "https://picsum.photos/400/400?random=202", 8, 156);
        insertProduct(sid2, "Coach\u84bb\u9a70\u5973\u58eb\u6258\u7279\u5305",
            "Coach\u7ecf\u5178\u6258\u7279\u5305\uff0c\u68d5\u8272\u8001\u82b1\u6b3e\uff0c\u6210\u8272\u4e0d\u9519\u3002",
            1299, 2999, cat2, cat2Name, "https://picsum.photos/400/400?random=203", 8, 189);
        insertProduct(sid2, "\u4f18\u8863\u5e93\u7fbd\u7ed2\u670d \u7537\u6b3eL\u7801",
            "\u4f18\u8863\u5e93\u8f7b\u8584\u7fbd\u7ed2\u670d\uff0c\u7537\u6b3eL\u7801\u9ed1\u8272\uff0c\u4fdd\u6696\u6548\u679c\u597d\u3002",
            199, 499, cat2, cat2Name, "https://picsum.photos/400/400?random=204", 9, 67);
        insertProduct(sid2, "Adidas Ultraboost 22 \u8dd1\u978b 43\u7801",
            "Adidas Ultraboost 22\u8dd1\u6b65\u978b\uff0c43\u7801\u767d\u8272\uff0cboost\u4e2d\u5e95\u8d85\u8f6f\u3002",
            599, 1099, cat2, cat2Name, "https://picsum.photos/400/400?random=205", 8, 145);
        insertProduct(sid2, "Zara\u7537\u58eb\u897f\u88c5\u5916\u5957 M\u7801",
            "Zara\u4fee\u8eab\u897f\u88c5\u5916\u5957\uff0cM\u7801\u85cf\u9752\u8272\uff0c\u51e0\u4e4e\u5168\u65b0\u3002",
            299, 599, cat2, cat2Name, "https://picsum.photos/400/400?random=206", 10, 88);

        // Category 3: Books (seller_id=8)
        long sid3 = 8; long cat3 = 3; String cat3Name = "\u56fe\u4e66\u6559\u6750";
        insertProduct(sid3, "\u300a\u7b97\u6cd5\u5bfc\u8bba\u300b\u7b2c\u4e09\u7248",
            "\u7b97\u6cd5\u5bfc\u8bba\u7b2c\u4e09\u7248\uff0c\u8ba1\u7b97\u673a\u4e13\u4e1a\u5fc5\u8bfb\u7ecf\u5178\u3002",
            59, 128, cat3, cat3Name, "https://picsum.photos/400/400?random=301", 7, 312);
        insertProduct(sid3, "\u300a\u6df1\u5165\u7406\u89e3\u8ba1\u7b97\u673a\u7cfb\u7edf\u300bCSAPP",
            "CSAPP\u7b2c\u4e09\u7248\uff0c\u7a0b\u5e8f\u5458\u8fdb\u9636\u5fc5\u8bfb\u3002",
            69, 139, cat3, cat3Name, "https://picsum.photos/400/400?random=302", 8, 278);
        insertProduct(sid3, "\u8003\u7814\u82f1\u8bed\u771f\u9898\u9ec4\u76ae\u4e66",
            "\u8003\u7814\u82f1\u8bed\u4e00\u771f\u9898\u9ec4\u76ae\u4e66\uff0c2020-2024\u5e74\u771f\u9898\u3002",
            29, 68, cat3, cat3Name, "https://picsum.photos/400/400?random=303", 7, 456);
        insertProduct(sid3, "\u300aJavaScript\u9ad8\u7ea7\u7a0b\u5e8f\u8bbe\u8ba1\u300b\u7b2c4\u7248",
            "\u7ea2\u5b9d\u4e66\u7b2c\u56db\u7248\uff0c\u524d\u7aef\u5f00\u53d1\u5fc5\u8bfb\uff0c\u5168\u65b0\u672a\u62c6\u5c01\u3002",
            79, 129, cat3, cat3Name, "https://picsum.photos/400/400?random=304", 10, 198);
        insertProduct(sid3, "CPA\u6ce8\u4f1a\u6559\u6750\u5168\u5957 2024\u7248",
            "2024\u5e74CPA\u6ce8\u518c\u4f1a\u8ba1\u5e08\u8003\u8bd5\u6559\u6750\u5168\u5957\u3002",
            199, 420, cat3, cat3Name, "https://picsum.photos/400/400?random=305", 9, 167);
        insertProduct(sid3, "\u300a\u4e09\u4f53\u300b\u5168\u96c6 \u7cbe\u88c5\u7248",
            "\u4e09\u4f53\u4e09\u90e8\u66f2\u7cbe\u88c5\u7248\uff0c\u79d1\u5e7b\u795e\u4f5c\uff0c\u4e66\u51b5\u5f88\u597d\u3002",
            89, 168, cat3, cat3Name, "https://picsum.photos/400/400?random=306", 9, 234);

        // Category 4: Home & Living (seller_id=3)
        long sid4 = 3; long cat4 = 4; String cat4Name = "\u751f\u6d3b\u7528\u54c1";
        insertProduct(sid4, "\u6234\u68eeV12\u5438\u5c18\u5668",
            "\u6234\u68eeV12\u65e0\u7ebf\u5438\u5c18\u5668\uff0c\u5438\u529b\u5f3a\u52b2\uff0c\u914d\u4ef6\u9f50\u5168\u3002",
            2499, 4490, cat4, cat4Name, "https://picsum.photos/400/400?random=401", 9, 145);
        insertProduct(sid4, "\u5c0f\u7c73\u7a7a\u6c14\u51c0\u5316\u56684 Pro",
            "\u5c0f\u7c73\u7a7a\u6c14\u51c0\u5316\u56684 Pro\uff0c\u9002\u752860\u5e73\u7c73\uff0c\u6ee4\u82af\u521a\u6362\u65b0\u3002",
            699, 1499, cat4, cat4Name, "https://picsum.photos/400/400?random=402", 8, 98);
        insertProduct(sid4, "\u4e5d\u9633\u7834\u58c1\u673a Y1",
            "\u4e5d\u9633\u7834\u58c1\u673aY1\uff0c\u53ef\u505a\u8c46\u6d46\u3001\u679c\u6c41\u3001\u8f85\u98df\u3002",
            399, 899, cat4, cat4Name, "https://picsum.photos/400/400?random=403", 8, 76);
        insertProduct(sid4, "\u5b9c\u5bb6MALM\u5e8a\u67b6 1.5\u7c73",
            "\u5b9c\u5bb6MALM\u7cfb\u5217\u5e8a\u67b6\uff0c1.5\u7c73\u767d\u8272\uff0c\u7b80\u7ea6\u5317\u6b27\u98ce\u3002",
            599, 1299, cat4, cat4Name, "https://picsum.photos/400/400?random=404", 8, 234);
        insertProduct(sid4, "\u98de\u5229\u6d66\u7535\u52a8\u7259\u5237 HX6730",
            "\u98de\u5229\u6d66\u58f0\u6ce2\u7535\u52a8\u7259\u5237HX6730\uff0c\u90012\u4e2a\u5168\u65b0\u5237\u5934\u3002",
            199, 399, cat4, cat4Name, "https://picsum.photos/400/400?random=405", 9, 112);
        insertProduct(sid4, "\u7f8e\u7684\u7535\u996d\u7172 4L\u667a\u80fd\u6b3e",
            "\u7f8e\u7684\u667a\u80fd\u7535\u996d\u71724L\uff0c\u591a\u529f\u80fd\u9884\u7ea6\uff0c\u529f\u80fd\u6b63\u5e38\u3002",
            149, 399, cat4, cat4Name, "https://picsum.photos/400/400?random=406", 8, 67);

        // Category 5: Sports (seller_id=4)
        long sid5 = 4; long cat5 = 5; String cat5Name = "\u8fd0\u52a8\u6237\u5916";
        insertProduct(sid5, "\u8fea\u5361\u4fac\u516c\u8def\u81ea\u884c\u8f66 RC120",
            "\u8fea\u5361\u4facRC120\u516c\u8def\u8f66\uff0c\u94dd\u5408\u91d1\u8f66\u67b6\uff0c\u8f66\u51b5\u826f\u597d\u3002",
            1999, 2999, cat5, cat5Name, "https://picsum.photos/400/400?random=501", 8, 189);
        insertProduct(sid5, "\u674e\u5b81\u7fbd\u6bdb\u7403\u62cd \u98ce\u5203900",
            "\u674e\u5b81\u98ce\u5203900\u7fbd\u6bdb\u7403\u62cd\uff0c\u8fdb\u653b\u578b\uff0c\u9001\u62cd\u5305\u3002",
            599, 1099, cat5, cat5Name, "https://picsum.photos/400/400?random=502", 8, 156);
        insertProduct(sid5, "\u8fea\u5361\u4fac\u767b\u5c71\u5305 50L",
            "\u8fea\u5361\u4fac50L\u767b\u5c71\u5305\uff0c\u80cc\u8d1f\u7cfb\u7edf\u8212\u9002\uff0c\u9632\u6c34\u9762\u6599\u3002",
            299, 499, cat5, cat5Name, "https://picsum.photos/400/400?random=503", 9, 98);
        insertProduct(sid5, "Keep\u667a\u80fd\u52a8\u611f\u5355\u8f66 C1",
            "Keep\u52a8\u611f\u5355\u8f66C1\uff0c\u78c1\u63a7\u9759\u97f3\uff0c\u53ef\u8fde\u63a5App\u8bfe\u7a0b\u3002",
            1299, 2499, cat5, cat5Name, "https://picsum.photos/400/400?random=504", 9, 234);
        insertProduct(sid5, "\u65af\u4f2f\u4e01\u7bee\u7403 NBA\u6bd4\u8d5b\u7528\u7403",
            "\u65af\u4f2f\u4e01NBA\u6bd4\u8d5b\u7528\u7403\uff0c\u771f\u76ae\u6750\u8d28\uff0c\u624b\u611f\u6781\u4f73\u3002",
            299, 599, cat5, cat5Name, "https://picsum.photos/400/400?random=505", 8, 145);
        insertProduct(sid5, "\u7491\u4f3d\u57ab TPE\u6750\u8d28 \u52a0\u539a\u6b3e",
            "\u9ad8\u5bc6\u5ea6TPE\u7491\u4f3d\u57ab\uff0c6mm\u52a0\u539a\uff0c\u9632\u6ed1\u8010\u78e8\u3002",
            79, 168, cat5, cat5Name, "https://picsum.photos/400/400?random=506", 9, 87);

        // Category 6: Others (seller_id=5)
        long sid6 = 5; long cat6 = 6; String cat6Name = "\u5176\u4ed6";
        insertProduct(sid6, "\u4e50\u9ad8\u673a\u68b0\u7ec4 \u5e03\u52a0\u8fea\u5a01\u9f99",
            "\u4e50\u9ad8\u673a\u68b0\u7ec4\u5e03\u52a0\u8fea\u5a01\u9f9942083\uff0c\u5df2\u62fc\u88c5\u5b8c\u6210\u3002",
            1999, 3699, cat6, cat6Name, "https://picsum.photos/400/400?random=601", 9, 267);
        insertProduct(sid6, "\u4efb\u5929\u5802Switch OLED\u7248",
            "Switch OLED\u7248\uff0c\u767d\u8272\uff0c\u5e263\u4e2a\u6e38\u620f\u5361\u5e26\u3002",
            1899, 2599, cat6, cat6Name, "https://picsum.photos/400/400?random=602", 9, 345);
        insertProduct(sid6, "\u5bcc\u58eb\u62cd\u7acb\u5f97 mini11 \u7c89\u8272",
            "\u5bcc\u58eb\u62cd\u7acb\u5f97mini11\uff0c\u7c89\u8272\uff0c\u90012\u76d2\u76f8\u7eb8\u3002",
            399, 599, cat6, cat6Name, "https://picsum.photos/400/400?random=603", 9, 178);
        insertProduct(sid6, "\u661f\u5df4\u514b\u9650\u5b9a\u9a6c\u514b\u676f",
            "\u661f\u5df4\u514b\u57ce\u5e02\u7cfb\u5217\u9a6c\u514b\u676f\uff0c\u5168\u65b0\u672a\u4f7f\u7528\u3002",
            129, 199, cat6, cat6Name, "https://picsum.photos/400/400?random=604", 10, 89);
        insertProduct(sid6, "\u5c0f\u7c73\u7c73\u5bb6\u6295\u5f71\u4eea2 Pro",
            "\u5c0f\u7c73\u6295\u5f71\u4eea2 Pro\uff0c1080P\u9ad8\u6e05\uff0c\u81ea\u52a8\u5bf9\u7126\u3002",
            2299, 3999, cat6, cat6Name, "https://picsum.photos/400/400?random=605", 8, 198);
        insertProduct(sid6, "\u7d22\u5c3cPS5\u5149\u9a71\u7248 \u56fd\u884c",
            "PS5\u5149\u9a71\u7248\u56fd\u884c\uff0c\u90012\u4e2a\u6e38\u620f\u5149\u76d8\u3002",
            3299, 4299, cat6, cat6Name, "https://picsum.photos/400/400?random=606", 9, 412);

        log.info("Products initialized: 36");
    }

    private void insertProduct(long sellerId, String title, String desc,
                               int price, int originalPrice, long categoryId,
                               String categoryName, String images, int condition, int viewCount) {
        Product p = new Product();
        p.setSellerId(sellerId);
        p.setTitle(title);
        p.setDescription(desc);
        p.setPrice(new BigDecimal(price));
        p.setOriginalPrice(new BigDecimal(originalPrice));
        p.setCategoryId(categoryId);
        p.setCategory(categoryName);
        p.setImages(images);
        p.setConditionLevel(condition);
        p.setStatus(1);
        p.setViewCount(viewCount);
        productMapper.insert(p);
    }
}
