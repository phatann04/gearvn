package gearvn.ui;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import java.io.*;
import java.util.Properties;

public class GearVNApp extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainContentPanel;

    public GearVNApp() {
        setTitle("GearVN - App Mua Sắm (All in One)");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- 1. HEADER CHUNG ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(227, 28, 37));
        headerPanel.setPreferredSize(new Dimension(1200, 60));
        headerPanel.setBorder(new EmptyBorder(10, 30, 10, 30));

        JLabel logoLabel = new JLabel("GEARVN");
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setFont(new Font("Arial", Font.BOLD, 26));
        logoLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { cardLayout.show(mainContentPanel, "HOME"); }
        });
        
        // Cụm nút bên phải (Đăng nhập + Giỏ hàng)
        JPanel rightHeaderPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightHeaderPanel.setOpaque(false);

        JButton accountBtn = new JButton("Đăng nhập / Đăng ký");
        accountBtn.setBackground(Color.WHITE);
        accountBtn.setForeground(new Color(227, 28, 37));
        accountBtn.setFocusPainted(false);
        accountBtn.setFont(new Font("Arial", Font.BOLD, 14));
        accountBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        accountBtn.addActionListener(e -> cardLayout.show(mainContentPanel, "LOGIN"));

        JButton cartBtn = new JButton("Giỏ hàng");
        cartBtn.setBackground(Color.WHITE);
        cartBtn.setForeground(new Color(227, 28, 37));
        cartBtn.setFocusPainted(false);
        cartBtn.setFont(new Font("Arial", Font.BOLD, 14));
        cartBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cartBtn.addActionListener(e -> cardLayout.show(mainContentPanel, "CART"));

        rightHeaderPanel.add(accountBtn);
        rightHeaderPanel.add(cartBtn);

        headerPanel.add(logoLabel, BorderLayout.WEST);
        headerPanel.add(rightHeaderPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // --- 2. MAIN CONTENT (CardLayout quản lý các trang) ---
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(new Color(244, 244, 244));

        // Nạp các trang cơ bản
        mainContentPanel.add(createHomePanel(), "HOME"); 
        mainContentPanel.add(createLoginPanel(), "LOGIN");
        mainContentPanel.add(createRegisterPanel(), "REGISTER");
        mainContentPanel.add(createForgotPasswordPanel(), "FORGOT_PASS");
        
        // Trang giỏ hàng mặc định (Trống)
        JPanel emptyCart = new JPanel(new BorderLayout());
        emptyCart.add(new JLabel("Giỏ hàng đang trống", SwingConstants.CENTER));
        emptyCart.setName("CART");
        mainContentPanel.add(emptyCart, "CART");

        // Nạp các trang Danh sách sản phẩm
        mainContentPanel.add(createProductListPanel("Laptop Gaming", 
            new String[]{"Dưới 30 triệu", "30 - 50 triệu", "Trên 50 triệu"}, 
            new String[]{"Acer", "Asus", "MSI", "Lenovo"}, 
            "Laptop ASUS ROG Strix", "31.790.000đ"), "LAPTOP_LIST");

        mainContentPanel.add(createProductListPanel("Chuột Gaming", 
            new String[]{"Dưới 1 triệu", "1 - 3 triệu", "Trên 3 triệu"}, 
            new String[]{"Razer", "Logitech", "Corsair"}, 
            "Chuột Razer DeathAdder", "1.590.000đ"), "CHUOT_LIST");

        mainContentPanel.add(createProductListPanel("Bàn Phím Gaming", 
            new String[]{"Dưới 1 triệu", "1 - 3 triệu", "Trên 3 triệu"}, 
            new String[]{"AULA", "Corsair", "Akko", "Logitech"}, 
            "Bàn phím cơ AULA F75", "650.000đ"), "BAN_PHIM_LIST");

        mainContentPanel.add(createProductListPanel("Tai Nghe Gaming", 
            new String[]{"Dưới 1 triệu", "1 - 3 triệu", "Trên 3 triệu"}, 
            new String[]{"Razer", "Corsair", "HyperX"}, 
            "Tai nghe Razer Barracuda", "2.890.000đ"), "TAI_NGHE_LIST");

        add(mainContentPanel, BorderLayout.CENTER);
        
        // Mặc định hiện Home đầu tiên
        cardLayout.show(mainContentPanel, "HOME"); 
    }

    // =========================================================================
    // HÀM XỬ LÝ: CHI TIẾT SẢN PHẨM & THÊM VÀO GIỎ HÀNG
    // =========================================================================
    private void showProductDetail(String productName, String productPrice) {
        for (Component c : mainContentPanel.getComponents()) {
            if ("PRODUCT_DETAIL".equals(c.getName())) {
                mainContentPanel.remove(c);
                break;
            }
        }
        JPanel detailPanel = createProductDetailPanel(productName, productPrice);
        detailPanel.setName("PRODUCT_DETAIL");
        mainContentPanel.add(detailPanel, "PRODUCT_DETAIL");
        cardLayout.show(mainContentPanel, "PRODUCT_DETAIL");
    }

    private void addToCartAndShow(String productName, String productPrice) {
        // Xóa giỏ hàng cũ để cập nhật mới
        new Thread(() -> {
            String json = "{ \"productName\":\"" + productName + "\", \"price\":\"" + productPrice + "\" }";
            ApiClient.post("/api/cart/add", json);
        }).start();
        for (Component c : mainContentPanel.getComponents()) {
            if ("CART".equals(c.getName())) {
                mainContentPanel.remove(c);
                break;
            }
        }
        
        // Tạo giỏ hàng có sản phẩm vừa chọn
        JPanel cartPanel = createCartPanel(productName, productPrice);
        cartPanel.setName("CART");
        mainContentPanel.add(cartPanel, "CART");
        cardLayout.show(mainContentPanel, "CART");
    }

    // =========================================================================
    // MÀN HÌNH GIỎ HÀNG
    // =========================================================================
    private JPanel createCartPanel(String productName, String productPrice) {
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(new Color(244, 244, 244));
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(244, 244, 244));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Nút quay lại
        JLabel backLabel = new JLabel("<html><span style='font-size:14px; color:#555;'>&larr; <u>Mua thêm sản phẩm khác</u></span></html>");
        backLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { cardLayout.show(mainContentPanel, "HOME"); }
        });
        backLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backPanel.setBackground(new Color(244, 244, 244));
        backPanel.setMaximumSize(new Dimension(800, 40));
        backPanel.add(backLabel);
        contentPanel.add(backPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // --- Hộp giỏ hàng (Cart Box) ---
        JPanel cartBox = new JPanel();
        cartBox.setLayout(new BoxLayout(cartBox, BoxLayout.Y_AXIS));
        cartBox.setBackground(Color.WHITE);
        cartBox.setMaximumSize(new Dimension(800, 400));
        cartBox.setBorder(new LineBorder(new Color(220, 220, 220), 1, true));

        // Header tổng tiền
        JPanel totalHeader = new JPanel(new BorderLayout());
        totalHeader.setBackground(new Color(255, 240, 242)); // Màu hồng nhạt
        totalHeader.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel totalText = new JLabel("Tổng tiền:");
        totalText.setFont(new Font("Arial", Font.BOLD, 18));
        JLabel totalPriceLabel = new JLabel(productPrice);
        totalPriceLabel.setFont(new Font("Arial", Font.BOLD, 22));
        totalPriceLabel.setForeground(new Color(227, 28, 37));
        
        totalHeader.add(totalText, BorderLayout.WEST);
        totalHeader.add(totalPriceLabel, BorderLayout.EAST);
        cartBox.add(totalHeader);

        // Sản phẩm (Item Row)
        JPanel itemRow = new JPanel(new GridBagLayout());
        itemRow.setBackground(Color.WHITE);
        itemRow.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 0, 10);
        
        // Cột 1: Ảnh
        JPanel imgMock = new JPanel(new BorderLayout());
        imgMock.setPreferredSize(new Dimension(100, 80));
        imgMock.setBackground(new Color(230, 230, 230));
        imgMock.add(new JLabel("Image", SwingConstants.CENTER), BorderLayout.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.0;
        itemRow.add(imgMock, gbc);

        // Cột 2: Tên sản phẩm
        JLabel nameLabel = new JLabel("<html><div style='width: 200px; font-weight:bold;'>" + productName + "</div></html>");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1; gbc.weightx = 0.5;
        itemRow.add(nameLabel, gbc);

        // Cột 3: Giá
        JPanel pricePanel = new JPanel();
        pricePanel.setLayout(new BoxLayout(pricePanel, BoxLayout.Y_AXIS));
        pricePanel.setBackground(Color.WHITE);
        JLabel pLbl = new JLabel(productPrice);
        pLbl.setFont(new Font("Arial", Font.BOLD, 16));
        pLbl.setForeground(new Color(227, 28, 37));
        JLabel oldPLbl = new JLabel("<html><strike>33.090.000đ</strike></html>");
        oldPLbl.setForeground(Color.GRAY);
        pricePanel.add(pLbl);
        pricePanel.add(oldPLbl);
        gbc.gridx = 2; gbc.weightx = 0.2;
        itemRow.add(pricePanel, gbc);

        // Cột 4: Số lượng & Nút Xóa
        JPanel actionPanel = new JPanel(new BorderLayout());
        actionPanel.setBackground(Color.WHITE);
        JLabel deleteBtn = new JLabel("<html><b>X</b></html>");
        deleteBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        deleteBtn.setHorizontalAlignment(SwingConstants.RIGHT);
        
        JPanel qtyPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        qtyPanel.setBackground(Color.WHITE);
        qtyPanel.add(new JButton("-"));
        qtyPanel.add(new JLabel(" 1 "));
        qtyPanel.add(new JButton("+"));
        
        actionPanel.add(deleteBtn, BorderLayout.NORTH);
        actionPanel.add(qtyPanel, BorderLayout.SOUTH);
        gbc.gridx = 3; gbc.weightx = 0.1;
        itemRow.add(actionPanel, gbc);

        cartBox.add(itemRow);

        // Nút Đặt hàng ngay
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(new EmptyBorder(20, 0, 30, 0));
        
        JButton checkoutBtn = new JButton("ĐẶT HÀNG NGAY");
        checkoutBtn.setBackground(new Color(227, 28, 37));
        checkoutBtn.setForeground(Color.WHITE);
        checkoutBtn.setFont(new Font("Arial", Font.BOLD, 18));
        checkoutBtn.setPreferredSize(new Dimension(350, 50));
        checkoutBtn.setFocusPainted(false);
        checkoutBtn.setBorder(new RoundedBorder(8, new Color(227, 28, 37)));
        
        footerPanel.add(checkoutBtn);
        cartBox.add(footerPanel);

        contentPanel.add(cartBox);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        wrapperPanel.add(scrollPane, BorderLayout.CENTER);

        return wrapperPanel;
    }


    // =========================================================================
    // 1. MÀN HÌNH TRANG CHỦ
    // =========================================================================
    private JPanel createHomePanel() {
        // [Nội dung giữ nguyên như code ban đầu của bạn]
        JPanel homePanel = new JPanel(new BorderLayout(15, 0));
        homePanel.setBackground(new Color(244, 244, 244));
        homePanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Color.WHITE);
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(new LineBorder(new Color(220, 220, 220)));

        String[] categories = {"Laptop Gaming", "Chuột Gaming", "Bàn Phím Gaming", "Tai Nghe Gaming", "Màn Hình", "Linh Kiện PC"};
        String[] linkKeys = {"LAPTOP_LIST", "CHUOT_LIST", "BAN_PHIM_LIST", "TAI_NGHE_LIST", "HOME", "HOME"};

        for (int i = 0; i < categories.length; i++) {
            String cat = categories[i];
            String key = linkKeys[i];
            JLabel catLabel = new JLabel(cat);
            catLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            catLabel.setBorder(new EmptyBorder(15, 15, 15, 15));
            catLabel.setMaximumSize(new Dimension(200, 50));
            catLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            catLabel.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) { cardLayout.show(mainContentPanel, key); }
                public void mouseEntered(MouseEvent e) { catLabel.setForeground(Color.RED); }
                public void mouseExited(MouseEvent e) { catLabel.setForeground(Color.BLACK); }
            });
            sidebar.add(catLabel);
            sidebar.add(new JSeparator());
        }
        homePanel.add(sidebar, BorderLayout.WEST);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(244, 244, 244));

        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(Color.DARK_GRAY);
        banner.setPreferredSize(new Dimension(800, 250));
        banner.setMaximumSize(new Dimension(2000, 250));
        JLabel bannerText = new JLabel("BANNER QUẢNG CÁO GVN", SwingConstants.CENTER);
        bannerText.setForeground(Color.WHITE);
        bannerText.setFont(new Font("Arial", Font.BOLD, 24));
        banner.add(bannerText, BorderLayout.CENTER);
        contentPanel.add(banner);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        contentPanel.add(createProductSection("Laptop Gaming Bán Chạy", "Laptop ASUS ROG Strix", "31.790.000đ"));
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(createProductSection("Chuột Gaming Bán Chạy", "Chuột Razer DeathAdder", "1.590.000đ"));
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(createProductSection("Bàn Phím Gaming Bán Chạy", "Bàn phím AKKO 3098", "1.290.000đ"));

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        homePanel.add(scrollPane, BorderLayout.CENTER);

        return homePanel;
    }

    // =========================================================================
    // 2. MÀN HÌNH DANH SÁCH SẢN PHẨM
    // =========================================================================
    private JPanel createProductListPanel(String categoryName, String[] priceRanges, String[] brands, String mockName, String mockPrice) {
        // [Nội dung giữ nguyên như code ban đầu của bạn]
        JPanel listPanel = new JPanel(new BorderLayout(15, 10));
        listPanel.setBackground(new Color(244, 244, 244));
        listPanel.setBorder(new EmptyBorder(10, 15, 15, 15));

        JLabel breadcrumb = new JLabel("<html><a href=''>Trang chủ</a> > <b>" + categoryName + "</b></html>");
        breadcrumb.setFont(new Font("Arial", Font.PLAIN, 14));
        breadcrumb.setCursor(new Cursor(Cursor.HAND_CURSOR));
        breadcrumb.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { cardLayout.show(mainContentPanel, "HOME"); }
        });
        listPanel.add(breadcrumb, BorderLayout.NORTH);

        JPanel filterSidebar = new JPanel();
        filterSidebar.setLayout(new BoxLayout(filterSidebar, BoxLayout.Y_AXIS));
        filterSidebar.setBackground(Color.WHITE);
        filterSidebar.setPreferredSize(new Dimension(220, 0));
        filterSidebar.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220)),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel priceLabel = new JLabel("Khoảng giá");
        priceLabel.setFont(new Font("Arial", Font.BOLD, 15));
        filterSidebar.add(priceLabel);
        filterSidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        for (String price : priceRanges) {
            JCheckBox cb = new JCheckBox(price);
            cb.setBackground(Color.WHITE);
            cb.setFont(new Font("Arial", Font.PLAIN, 13));
            filterSidebar.add(cb);
            filterSidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        filterSidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel brandLabel = new JLabel("Thương hiệu");
        brandLabel.setFont(new Font("Arial", Font.BOLD, 15));
        filterSidebar.add(brandLabel);
        filterSidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        for (String brand : brands) {
            JCheckBox cb = new JCheckBox(brand);
            cb.setBackground(Color.WHITE);
            cb.setFont(new Font("Arial", Font.PLAIN, 13));
            filterSidebar.add(cb);
            filterSidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        listPanel.add(filterSidebar, BorderLayout.WEST);

        JPanel rightContent = new JPanel();
        rightContent.setLayout(new BoxLayout(rightContent, BoxLayout.Y_AXIS));
        rightContent.setBackground(new Color(244, 244, 244));

        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(new Color(0, 51, 204));
        banner.setPreferredSize(new Dimension(800, 200));
        banner.setMaximumSize(new Dimension(2000, 200));
        JLabel bannerText = new JLabel("GEAR ARENA - " + categoryName.toUpperCase(), SwingConstants.CENTER);
        bannerText.setForeground(Color.YELLOW);
        bannerText.setFont(new Font("Arial", Font.BOLD, 28));
        banner.add(bannerText, BorderLayout.CENTER);
        rightContent.add(banner);
        rightContent.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel grid = new JPanel(new GridLayout(0, 3, 15, 15));
        grid.setBackground(new Color(244, 244, 244));
        for (int i = 1; i <= 6; i++) { 
            grid.add(createProductCard(mockName + " Phiên bản " + i, mockPrice));
        }
        rightContent.add(grid);

        JScrollPane scrollPane = new JScrollPane(rightContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        listPanel.add(scrollPane, BorderLayout.CENTER);

        return listPanel;
    }

    // =========================================================================
    // 3. MÀN HÌNH CHI TIẾT SẢN PHẨM
    // =========================================================================
    private JPanel createProductDetailPanel(String productName, String productPrice) {
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(Color.WHITE);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(15, 30, 30, 30));

        JLabel breadcrumb = new JLabel("<html><a href=''>Trang chủ</a> &nbsp; > &nbsp; Laptop gaming &nbsp; > &nbsp; <b>" + productName + "</b></html>");
        breadcrumb.setFont(new Font("Arial", Font.PLAIN, 14));
        breadcrumb.setCursor(new Cursor(Cursor.HAND_CURSOR));
        breadcrumb.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { cardLayout.show(mainContentPanel, "HOME"); }
        });
        breadcrumb.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(breadcrumb);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel topSection = new JPanel(new GridBagLayout());
        topSection.setBackground(Color.WHITE);
        topSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weighty = 1.0;

        JPanel imageSection = new JPanel();
        imageSection.setLayout(new BoxLayout(imageSection, BoxLayout.Y_AXIS));
        imageSection.setBackground(Color.WHITE);
        
        // ===== ẢNH CHÍNH =====
        JPanel mainImagePanel = new JPanel(new BorderLayout());
        mainImagePanel.setBackground(new Color(230, 230, 230));
        mainImagePanel.setPreferredSize(new Dimension(450, 350));
        mainImagePanel.setMaximumSize(new Dimension(450, 350));

        JLabel mainImgLabel = new JLabel("Đang tải ảnh...", SwingConstants.CENTER);
        mainImgLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        mainImgLabel.setForeground(Color.GRAY);
        mainImagePanel.add(mainImgLabel, BorderLayout.CENTER);

        // Nút 📷 để đổi ảnh ngay trong trang chi tiết
        JButton camBtnDetail = new JButton("📷 Đổi ảnh");
        camBtnDetail.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
        camBtnDetail.setFocusPainted(false);
        camBtnDetail.setBackground(Color.WHITE);
        camBtnDetail.setBorder(new LineBorder(new Color(200,200,200)));
        camBtnDetail.setCursor(new Cursor(Cursor.HAND_CURSOR));
        camBtnDetail.addActionListener(ev -> showSetImageDialog(productName, mainImgLabel));
        JPanel camTopRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 4));
        camTopRight.setOpaque(false);
        camTopRight.add(camBtnDetail);
        mainImagePanel.add(camTopRight, BorderLayout.NORTH);

        // Load ảnh lên ảnh chính
        String detailImgUrl = getImageUrl(productName);
        if (!detailImgUrl.isEmpty()) {
            loadImageAsync450(mainImgLabel, detailImgUrl);
        }

        imageSection.add(mainImagePanel);
        imageSection.add(Box.createRigidArea(new Dimension(0, 10)));

        // ===== THUMBNAILS (4 ảnh nhỏ cùng link) =====
        JPanel thumbsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        thumbsPanel.setBackground(Color.WHITE);
        for (int i = 0; i < 4; i++) {
            JPanel thumbWrapper = new JPanel(new BorderLayout());
            thumbWrapper.setBackground(new Color(230, 230, 230));
            thumbWrapper.setPreferredSize(new Dimension(80, 80));
            thumbWrapper.setBorder(new LineBorder(Color.LIGHT_GRAY));

            JLabel thumbImg = new JLabel("", SwingConstants.CENTER);
            thumbWrapper.add(thumbImg, BorderLayout.CENTER);

            if (!detailImgUrl.isEmpty()) {
                loadImageAsyncThumb(thumbImg, detailImgUrl);
            }
            thumbsPanel.add(thumbWrapper);
        }
        imageSection.add(thumbsPanel);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.5;
        gbc.insets = new Insets(0, 0, 0, 30);
        topSection.add(imageSection, gbc);

        JPanel infoSection = new JPanel();
        infoSection.setLayout(new BoxLayout(infoSection, BoxLayout.Y_AXIS));
        infoSection.setBackground(Color.WHITE);

        JLabel titleLbl = new JLabel("<html><div style='width: 400px; line-height: 1.2;'>" + productName + "</div></html>");
        titleLbl.setFont(new Font("Arial", Font.BOLD, 22));
        
        JLabel priceLbl = new JLabel("<html><span style='color: #E31C25; font-size: 26px; font-weight: bold;'>" + productPrice 
                + "</span> &nbsp; <span style='text-decoration: line-through; color: #999999; font-size: 16px;'>33.090.000đ</span> &nbsp; <span style='color: red; border: 1px solid red; font-size: 12px; padding: 2px;'>-4%</span></html>");
        
        JButton buyBtn = new JButton("<html><center><b style='font-size: 18px;'>MUA NGAY</b><br><span style='font-size: 11px; font-weight: normal;'>Giao tận nơi/Nhận tại cửa hàng</span></center></html>");
        buyBtn.setBackground(new Color(227, 28, 37));
        buyBtn.setForeground(Color.WHITE);
        buyBtn.setFocusPainted(false);
        buyBtn.setMaximumSize(new Dimension(400, 60));
        buyBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        buyBtn.setBorder(new RoundedBorder(8, new Color(227, 28, 37)));

        // --- THÊM SỰ KIỆN CLICK ĐỂ CHUYỂN SANG GIỎ HÀNG ---
        buyBtn.addActionListener(e -> addToCartAndShow(productName, productPrice));

        JPanel policyPanel = new JPanel();
        policyPanel.setLayout(new BoxLayout(policyPanel, BoxLayout.Y_AXIS));
        policyPanel.setBackground(Color.WHITE);
        String[] policies = {
            "√ Bảo hành chính hãng 24 tháng.",
            "√ Hỗ trợ đổi mới trong 7 ngày.",
            "√ Miễn phí giao hàng toàn quốc."
        };
        for (String p : policies) {
            JLabel pLbl = new JLabel(p);
            pLbl.setFont(new Font("Arial", Font.PLAIN, 15));
            policyPanel.add(pLbl);
            policyPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        infoSection.add(titleLbl);
        infoSection.add(Box.createRigidArea(new Dimension(0, 15)));
        infoSection.add(priceLbl);
        infoSection.add(Box.createRigidArea(new Dimension(0, 20)));
        infoSection.add(buyBtn);
        infoSection.add(Box.createRigidArea(new Dimension(0, 25)));
        infoSection.add(policyPanel);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.5;
        gbc.insets = new Insets(0, 0, 0, 0);
        topSection.add(infoSection, gbc);

        contentPanel.add(topSection);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        
        JPanel bottomSection = new JPanel(new GridBagLayout());
        bottomSection.setBackground(Color.WHITE);
        bottomSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        GridBagConstraints gbcBtm = new GridBagConstraints();
        gbcBtm.fill = GridBagConstraints.BOTH;
        gbcBtm.anchor = GridBagConstraints.NORTHWEST;
        gbcBtm.weighty = 1.0;

        JPanel specsWrapper = new JPanel(new BorderLayout(0, 10));
        specsWrapper.setBackground(Color.WHITE);
        JLabel specsTitle = new JLabel("Thông tin sản phẩm");
        specsTitle.setFont(new Font("Arial", Font.BOLD, 18));
        specsWrapper.add(specsTitle, BorderLayout.NORTH);

        JPanel specsTable = new JPanel(new GridLayout(0, 2, 0, 0));
        specsTable.setBorder(new LineBorder(new Color(220, 220, 220)));
        String[][] specsData = {
            {"CPU", "AMD Ryzen™ 7 8845HS"},
            {"Card đồ họa", "NVIDIA® GeForce RTX™ 3050"},
            {"RAM", "32GB (2x16GB) DDR5 5600MHz"},
            {"SSD", "512GB PCIe NVMe SED SSD"},
            {"Kích thước màn hình", "16 inch"}
        };
        for (String[] rowData : specsData) {
            JPanel cell1 = new JPanel(new BorderLayout());
            cell1.setBackground(Color.WHITE);
            cell1.setBorder(BorderFactory.createCompoundBorder(new MatteBorder(0, 0, 1, 1, new Color(220,220,220)), new EmptyBorder(10, 10, 10, 10)));
            cell1.add(new JLabel("<html><b>" + rowData[0] + "</b></html>"));
            
            JPanel cell2 = new JPanel(new BorderLayout());
            cell2.setBackground(Color.WHITE);
            cell2.setBorder(BorderFactory.createCompoundBorder(new MatteBorder(0, 0, 1, 0, new Color(220,220,220)), new EmptyBorder(10, 10, 10, 10)));
            cell2.add(new JLabel("<html><div style='width: 180px;'>" + rowData[1] + "</div></html>"));

            specsTable.add(cell1);
            specsTable.add(cell2);
        }
        specsWrapper.add(specsTable, BorderLayout.CENTER);
        
        gbcBtm.gridx = 0; gbcBtm.gridy = 0; gbcBtm.weightx = 0.45;
        gbcBtm.insets = new Insets(0, 0, 0, 30);
        bottomSection.add(specsWrapper, gbcBtm);

        JPanel similarWrapper = new JPanel(new BorderLayout(0, 10));
        similarWrapper.setBackground(new Color(244, 244, 244));
        similarWrapper.setBorder(new EmptyBorder(15, 15, 15, 15));
        JLabel similarTitle = new JLabel("Sản phẩm tương tự");
        similarTitle.setFont(new Font("Arial", Font.BOLD, 16));
        similarWrapper.add(similarTitle, BorderLayout.NORTH);

        JPanel similarGrid = new JPanel(new GridLayout(1, 3, 10, 0));
        similarGrid.setBackground(new Color(244, 244, 244));
        similarGrid.add(createProductCard("Laptop gaming Acer Nitro...", "33.290.000đ"));
        similarGrid.add(createProductCard("Laptop gaming ASUS ROG...", "48.490.000đ"));
        similarGrid.add(createProductCard("Laptop gaming ASUS ROG...", "50.990.000đ"));
        similarWrapper.add(similarGrid, BorderLayout.CENTER);

        gbcBtm.gridx = 1; gbcBtm.gridy = 0; gbcBtm.weightx = 0.55;
        gbcBtm.insets = new Insets(0, 0, 0, 0);
        bottomSection.add(similarWrapper, gbcBtm);

        contentPanel.add(bottomSection);

        contentPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        // =====================================================================
        // PHẦN ĐÁNH GIÁ SẢN PHẨM
        // =====================================================================
        JPanel reviewSection = buildReviewSection(productName);
        reviewSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(reviewSection);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        wrapperPanel.add(scrollPane, BorderLayout.CENTER);
        return wrapperPanel;
    }


    // =========================================================================
    // PHẦN ĐÁNH GIÁ SẢN PHẨM — REVIEW SECTION
    // =========================================================================
    private JPanel buildReviewSection(String productName) {
        Color RED   = new Color(227, 28, 37);
        Color GOLD  = new Color(255, 184, 0);
        Color LGREY = new Color(245, 245, 245);

        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(Color.WHITE);
        section.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(25, 30, 30, 30)
        ));

        // ── Tiêu đề ─────────────────────────────────────────────────────────
        JLabel sectionTitle = new JLabel("Đánh giá & Nhận xét " + productName);
        sectionTitle.setFont(new Font("Arial", Font.BOLD, 20));
        sectionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.add(sectionTitle);
        section.add(Box.createRigidArea(new Dimension(0, 20)));

        // ── Tổng quan điểm ─────────────────────────────────────────────────
        JPanel overviewRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 0));
        overviewRow.setBackground(Color.WHITE);
        overviewRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        overviewRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel scoreLabel = new JLabel("0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 54));
        scoreLabel.setForeground(new Color(40, 40, 40));

        JPanel scoreRight = new JPanel();
        scoreRight.setLayout(new BoxLayout(scoreRight, BoxLayout.Y_AXIS));
        scoreRight.setBackground(Color.WHITE);

        JPanel starsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        starsRow.setBackground(Color.WHITE);
        for (int i = 0; i < 5; i++) {
            JLabel star = new JLabel(i < 4 ? "★" : "☆");
            star.setFont(new Font("Arial", Font.PLAIN, 22));
            star.setForeground(i < 4 ? GOLD : new Color(200, 200, 200));
            starsRow.add(star);
        }

        JPanel countRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        countRow.setBackground(Color.WHITE);
        JLabel countLabel = new JLabel("(0 đánh giá)");
        countLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        countLabel.setForeground(Color.GRAY);
        JLabel helpIcon = createHelpIcon();
        countRow.add(countLabel);
        countRow.add(helpIcon);

        scoreRight.add(starsRow);
        scoreRight.add(Box.createRigidArea(new Dimension(0, 4)));
        scoreRight.add(countRow);

        overviewRow.add(scoreLabel);
        overviewRow.add(scoreRight);
        section.add(overviewRow);
        section.add(Box.createRigidArea(new Dimension(0, 24)));
        section.add(new JSeparator());
        section.add(Box.createRigidArea(new Dimension(0, 24)));

        // ── Form viết đánh giá ───────────────────────────────────────────────
        JPanel formBox = new JPanel();
        formBox.setLayout(new BoxLayout(formBox, BoxLayout.Y_AXIS));
        formBox.setBackground(LGREY);
        formBox.setBorder(new EmptyBorder(20, 20, 20, 20));
        formBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        formBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 999));

        JLabel formTitle = new JLabel("Viết đánh giá của bạn");
        formTitle.setFont(new Font("Arial", Font.BOLD, 15));
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        formBox.add(formTitle);
        formBox.add(Box.createRigidArea(new Dimension(0, 12)));

        // Chọn sao tương tác
        JPanel starPickRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        starPickRow.setBackground(LGREY);
        starPickRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel starPickLabel = new JLabel("Xếp hạng của bạn:");
        starPickLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        starPickRow.add(starPickLabel);
        starPickRow.add(Box.createHorizontalStrut(8));

        JLabel[] starBtns = new JLabel[5];
        int[] selectedStar = {0};

        for (int i = 0; i < 5; i++) {
            final int idx = i + 1;
            JLabel s = new JLabel("☆");
            s.setFont(new Font("Arial", Font.PLAIN, 26));
            s.setForeground(new Color(200, 200, 200));
            s.setCursor(new Cursor(Cursor.HAND_CURSOR));
            starBtns[i] = s;
            s.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    selectedStar[0] = idx;
                    for (int j = 0; j < 5; j++) {
                        starBtns[j].setText(j < idx ? "★" : "☆");
                        starBtns[j].setForeground(j < idx ? GOLD : new Color(200, 200, 200));
                    }
                }
                public void mouseEntered(MouseEvent e) {
                    for (int j = 0; j < 5; j++) {
                        starBtns[j].setText(j < idx ? "★" : "☆");
                        starBtns[j].setForeground(j < idx ? GOLD : new Color(200, 200, 200));
                    }
                }
                public void mouseExited(MouseEvent e) {
                    int cur = selectedStar[0];
                    for (int j = 0; j < 5; j++) {
                        starBtns[j].setText(j < cur ? "★" : "☆");
                        starBtns[j].setForeground(j < cur ? GOLD : new Color(200, 200, 200));
                    }
                }
            });
            starPickRow.add(s);
        }
        formBox.add(starPickRow);
        formBox.add(Box.createRigidArea(new Dimension(0, 14)));

        // Tên người đánh giá
        JTextField reviewerName = new JTextField("Họ và tên");
        reviewerName.setFont(new Font("Arial", Font.PLAIN, 14));
        reviewerName.setForeground(Color.GRAY);
        reviewerName.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        reviewerName.setAlignmentX(Component.LEFT_ALIGNMENT);
        reviewerName.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(5, 10, 5, 10)
        ));
        reviewerName.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (reviewerName.getText().equals("Họ và tên")) {
                    reviewerName.setText(""); reviewerName.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent e) {
                if (reviewerName.getText().isEmpty()) {
                    reviewerName.setText("Họ và tên"); reviewerName.setForeground(Color.GRAY);
                }
            }
        });
        formBox.add(reviewerName);
        formBox.add(Box.createRigidArea(new Dimension(0, 10)));

        // Nội dung đánh giá
        JTextArea reviewText = new JTextArea(4, 0);
        reviewText.setFont(new Font("Arial", Font.PLAIN, 14));
        reviewText.setForeground(Color.GRAY);
        reviewText.setText("Chia sẻ trải nghiệm của bạn về sản phẩm...");
        reviewText.setLineWrap(true);
        reviewText.setWrapStyleWord(true);
        reviewText.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(8, 10, 8, 10)
        ));
        reviewText.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (reviewText.getText().startsWith("Chia sẻ")) {
                    reviewText.setText(""); reviewText.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent e) {
                if (reviewText.getText().isEmpty()) {
                    reviewText.setText("Chia sẻ trải nghiệm của bạn về sản phẩm...");
                    reviewText.setForeground(Color.GRAY);
                }
            }
        });
        JScrollPane reviewScroll = new JScrollPane(reviewText);
        reviewScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        reviewScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        reviewScroll.setBorder(null);
        formBox.add(reviewScroll);
        formBox.add(Box.createRigidArea(new Dimension(0, 14)));

        // Danh sách đánh giá — khai báo ở đây để listener submit truy cập được
        JPanel reviewListPanel = new JPanel();
        reviewListPanel.setLayout(new BoxLayout(reviewListPanel, BoxLayout.Y_AXIS));
        reviewListPanel.setBackground(Color.WHITE);
        reviewListPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel emptyReviewLbl = new JLabel("Chưa có đánh giá nào. Hãy là người đầu tiên đánh giá!");
        emptyReviewLbl.setFont(new Font("Arial", Font.ITALIC, 14));
        emptyReviewLbl.setForeground(Color.GRAY);
        emptyReviewLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        reviewListPanel.add(emptyReviewLbl);

        // Nút gửi đánh giá
        JButton submitBtn = new JButton("GỬI ĐÁNH GIÁ");
        submitBtn.setBackground(RED);
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFont(new Font("Arial", Font.BOLD, 14));
        submitBtn.setFocusPainted(false);
        submitBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        submitBtn.setBorder(new RoundedBorder(6, RED));
        submitBtn.setPreferredSize(new Dimension(180, 42));
        submitBtn.setMaximumSize(new Dimension(180, 42));
        submitBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        submitBtn.addActionListener(e -> {
            String name   = reviewerName.getText().trim();
            String body   = reviewText.getText().trim();
            int    rating = selectedStar[0];
            if (name.isEmpty() || name.equals("Họ và tên")) {
                JOptionPane.showMessageDialog(GearVNApp.this,
                    "Vui lòng nhập họ và tên.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE); return;
            }
            if (rating == 0) {
                JOptionPane.showMessageDialog(GearVNApp.this,
                    "Vui lòng chọn số sao đánh giá.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE); return;
            }
            if (body.isEmpty() || body.startsWith("Chia sẻ")) {
                JOptionPane.showMessageDialog(GearVNApp.this,
                    "Vui lòng nhập nội dung đánh giá.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE); return;
            }
            String json = String.format(
                "{\"name\":\"%s\",\"rating\":%d,\"comment\":\"%s\"}",
                name, rating, body
            );
            System.out.println(json);
            reviewListPanel.remove(emptyReviewLbl);
            JPanel newCard = buildReviewCard(name, rating, body, GOLD);
            reviewListPanel.add(newCard);
            reviewListPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            reviewListPanel.revalidate();
            reviewListPanel.repaint();
            // Reset form
            reviewerName.setText("Họ và tên"); reviewerName.setForeground(Color.GRAY);
            reviewText.setText("Chia sẻ trải nghiệm của bạn về sản phẩm..."); reviewText.setForeground(Color.GRAY);
            selectedStar[0] = 0;
            for (JLabel st : starBtns) { st.setText("☆"); st.setForeground(new Color(200, 200, 200)); }
            JOptionPane.showMessageDialog(GearVNApp.this,
                "Cảm ơn bạn đã đánh giá sản phẩm!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        });
        formBox.add(submitBtn);
        section.add(formBox);
        section.add(Box.createRigidArea(new Dimension(0, 24)));

        // ── Danh sách nhận xét ───────────────────────────────────────────────
        JLabel listTitle = new JLabel("Nhận xét từ khách hàng");
        listTitle.setFont(new Font("Arial", Font.BOLD, 15));
        listTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.add(listTitle);
        section.add(Box.createRigidArea(new Dimension(0, 12)));
        section.add(reviewListPanel);

        return section;
    }

    /** Tạo card hiển thị 1 đánh giá của khách hàng */
    private JPanel buildReviewCard(String author, int rating, String body, Color gold) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(250, 250, 250));
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(14, 16, 14, 16)
        ));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 9999));

        // Avatar vòng tròn chữ cái đầu + tên + sao
        JPanel headerRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        headerRow.setBackground(new Color(250, 250, 250));
        headerRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel avatar = new JLabel(String.valueOf(author.charAt(0)).toUpperCase()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(227, 28, 37));
                g2.fillOval(0, 0, getWidth()-1, getHeight()-1);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatar.setPreferredSize(new Dimension(36, 36));
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setFont(new Font("Arial", Font.BOLD, 16));
        avatar.setForeground(Color.WHITE);
        avatar.setOpaque(false);

        JPanel nameStarCol = new JPanel();
        nameStarCol.setLayout(new BoxLayout(nameStarCol, BoxLayout.Y_AXIS));
        nameStarCol.setBackground(new Color(250, 250, 250));

        JLabel nameLbl = new JLabel(author);
        nameLbl.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel starsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 0));
        starsPanel.setBackground(new Color(250, 250, 250));
        for (int i = 0; i < 5; i++) {
            JLabel st = new JLabel(i < rating ? "★" : "☆");
            st.setFont(new Font("Arial", Font.PLAIN, 14));
            st.setForeground(i < rating ? gold : new Color(200, 200, 200));
            starsPanel.add(st);
        }
        nameStarCol.add(nameLbl);
        nameStarCol.add(starsPanel);
        headerRow.add(avatar);
        headerRow.add(nameStarCol);
        card.add(headerRow);
        card.add(Box.createRigidArea(new Dimension(0, 10)));

        JTextArea bodyArea = new JTextArea(body);
        bodyArea.setWrapStyleWord(true); bodyArea.setLineWrap(true);
        bodyArea.setEditable(false);    bodyArea.setFocusable(false);
        bodyArea.setFont(new Font("Arial", Font.PLAIN, 14));
        bodyArea.setBackground(new Color(250, 250, 250));
        bodyArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(bodyArea);
        return card;
    }

    /** Icon dấu chấm hỏi nhỏ bên cạnh số đánh giá */
    private JLabel createHelpIcon() {
        JLabel icon = new JLabel("?") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(150, 150, 150));
                g2.fillOval(0, 0, getWidth()-1, getHeight()-1);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        icon.setFont(new Font("Arial", Font.BOLD, 10));
        icon.setForeground(Color.WHITE);
        icon.setOpaque(false);
        icon.setPreferredSize(new Dimension(16, 16));
        icon.setHorizontalAlignment(SwingConstants.CENTER);
        icon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        icon.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(null,
                    "Điểm đánh giá được tổng hợp từ tất cả\ncác nhận xét xác thực của khách hàng.",
                    "Thông tin đánh giá", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        return icon;
    }

    // =========================================================================
    // CÁC HÀM UI HELPER (Tạo Thẻ SP, Tạo Form Login...)
    // =========================================================================
    private JPanel createProductSection(String titleText, String mockName, String mockPrice) {
        JPanel section = new JPanel(new BorderLayout(0, 10));
        section.setBackground(new Color(244, 244, 244));
        section.setMaximumSize(new Dimension(2000, 350));

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        section.add(title, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(1, 4, 15, 0));
        grid.setBackground(new Color(244, 244, 244));
        for (int i = 1; i <= 4; i++) {
            grid.add(createProductCard(mockName + " V" + i, mockPrice));
        }
        section.add(grid, BorderLayout.CENTER);
        return section;
    }

    // ===== MAP ẢNH SẢN PHẨM =====
    private static final Map<String, String> PRODUCT_IMAGES = new HashMap<>();
    private static final Map<String, String> CUSTOM_IMAGES = new HashMap<>();
    private static final String IMAGE_PROPS_FILE = "product_images.properties";

    // ===== ĐỌC ẢNH CUSTOM TỪ FILE KHI KHỞI ĐỘNG =====
    static {
        Properties props = new Properties();
        File f = new File(IMAGE_PROPS_FILE);
        if (f.exists()) {
            try (FileInputStream fis = new FileInputStream(f)) {
                props.load(fis);
                for (String key : props.stringPropertyNames()) {
                    CUSTOM_IMAGES.put(key, props.getProperty(key));
                }
            } catch (IOException ignored) {}
        }
    }

    // ===== LƯU ẢNH CUSTOM XUỐNG FILE =====
    private void saveCustomImages() {
        Properties props = new Properties();
        props.putAll(CUSTOM_IMAGES);
        try (FileOutputStream fos = new FileOutputStream(IMAGE_PROPS_FILE)) {
            props.store(fos, "GearVN Product Images");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ===== MỞ DIALOG NHẬP LINK ẢNH =====
    private void showSetImageDialog(String productName, JLabel imgLabel) {
        JDialog dialog = new JDialog(this, "Cập nhật ảnh sản phẩm", true);
        dialog.setSize(520, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        titlePanel.setBackground(new Color(227, 28, 37));
        JLabel titleLbl = new JLabel("📷  Nhập link ảnh cho: " + productName);
        titleLbl.setForeground(Color.WHITE);
        titleLbl.setFont(new Font("Arial", Font.BOLD, 13));
        titlePanel.add(titleLbl);
        dialog.add(titlePanel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        String current = CUSTOM_IMAGES.getOrDefault(productName, PRODUCT_IMAGES.getOrDefault(productName, ""));
        JTextField urlField = new JTextField(current.isEmpty() ? "https://..." : current);
        urlField.setFont(new Font("Arial", Font.PLAIN, 13));
        urlField.setPreferredSize(new Dimension(400, 35));
        urlField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (urlField.getText().equals("https://...")) urlField.setText("");
            }
        });
        inputPanel.add(new JLabel("Link PNG/JPG:"), BorderLayout.WEST);
        inputPanel.add(urlField, BorderLayout.CENTER);
        dialog.add(inputPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        JButton cancelBtn = new JButton("Hủy");
        cancelBtn.addActionListener(ev -> dialog.dispose());

        JButton saveBtn = new JButton("Lưu & Cập nhật");
        saveBtn.setBackground(new Color(227, 28, 37));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.addActionListener(ev -> {
            String url = urlField.getText().trim();
            if (url.isEmpty() || url.equals("https://...")) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập link ảnh!");
                return;
            }
            CUSTOM_IMAGES.put(productName, url);
            saveCustomImages();
            imgLabel.setIcon(null);
            imgLabel.setText("Đang tải...");
            // Tự phát hiện kích thước phù hợp theo component
            Dimension d = imgLabel.getPreferredSize();
            int w = (d != null && d.width > 200) ? 450 : 200;
            int h = (d != null && d.height > 150) ? 350 : 150;
            loadImageAsyncSized(imgLabel, url, w, h);
            dialog.dispose();
        });

        JButton clearBtn = new JButton("Xóa ảnh");
        clearBtn.addActionListener(ev -> {
            CUSTOM_IMAGES.remove(productName);
            saveCustomImages();
            imgLabel.setIcon(null);
            imgLabel.setText("Chưa có ảnh");
            dialog.dispose();
        });

        btnPanel.add(clearBtn);
        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    static {
        // LAPTOP
        PRODUCT_IMAGES.put("Laptop ASUS ROG Strix G15",    "https://dlcdnwebimgs.asus.com/gain/6A2ACDBD-DD00-4F52-97CA-14AC0462BF88/w800");
        PRODUCT_IMAGES.put("Laptop ASUS ROG Zephyrus G14", "https://dlcdnwebimgs.asus.com/gain/3A3B74A8-D4E8-4BB1-A935-E5C793A78C0D/w800");
        PRODUCT_IMAGES.put("Laptop MSI Katana 15",          "https://asset.msi.com/resize/image/global/product/product_1_20220722183528_62daa120c67b5.png62405b38c58fe0f07fcef2367d8a9ba1/600.png");
        PRODUCT_IMAGES.put("Laptop Acer Nitro 5",           "https://images.acer.com/is/image/acer/acer-nitro5-wallpaper-feature?$Product-Cards-XL$");
        PRODUCT_IMAGES.put("Laptop Lenovo Legion 5",        "https://p1-ofp.static.pub/fes/cms/2022/07/13/qlqo2yru3vwhljymfywtmbecyb6zmh622804.png");
        PRODUCT_IMAGES.put("Laptop ASUS TUF Gaming F15",   "https://dlcdnwebimgs.asus.com/gain/5C3ABDC5-6B1A-4A27-B5CF-1DE9EC8BF2CC/w800");
        // CHUỘT
        PRODUCT_IMAGES.put("Chuột Razer DeathAdder Essential", "https://assets2.razerzone.com/images/pnx.assets/cc4b5e04-b30e-499c-857a-a6e03cb5148b/razer-deathadder-essential-gallery-4.jpg");
        PRODUCT_IMAGES.put("Chuột Razer Basilisk Ultimate",    "https://assets2.razerzone.com/images/pnx.assets/a80d7f3b-0834-4cc1-9df0-5e8d19a42898/razer-basilisk-ultimate-gallery-2.jpg");
        PRODUCT_IMAGES.put("Chuột Logitech G102",              "https://resource.logitech.com/w_386,c_limit,q_auto,f_auto,dpr_1.0/d_transparent.gif/content/dam/logitech/en/products/mice/g102-lightsync/gallery/g102-lightsync-mouse-top-view-black.png");
        PRODUCT_IMAGES.put("Chuột Logitech G Pro X Superlight","https://resource.logitech.com/w_386,c_limit,q_auto,f_auto,dpr_1.0/d_transparent.gif/content/dam/logitech/en/products/mice/pro-x-superlight-2/gallery/pro-x-superlight2-mouse-top-view-white.png");
        PRODUCT_IMAGES.put("Chuột Corsair M65 RGB Elite",     "https://www.corsair.com/medias/sys_master/images/images/h38/hb1/9057255039006.png");
        PRODUCT_IMAGES.put("Chuột SteelSeries Rival 3",       "https://steelseries.com/static/img/rival-3/rival-3-primary-black.png");
        // BÀN PHÍM
        PRODUCT_IMAGES.put("Bàn phím cơ AULA F75",       "https://salt.tikicdn.com/cache/750x750/ts/product/3d/e8/d2/54b3b1a4de8ad28ab0c8c5bf72bda03a.jpg");
        PRODUCT_IMAGES.put("Bàn phím AKKO 3098",          "https://salt.tikicdn.com/cache/750x750/ts/product/49/4d/da/93d3de9f87c0fb64a7a5e0b94c0f9fd6.jpg");
        PRODUCT_IMAGES.put("Bàn phím Corsair K70 RGB",   "https://www.corsair.com/medias/sys_master/images/images/hd6/h1c/8843001659422.png");
        PRODUCT_IMAGES.put("Bàn phím Logitech G Pro X",  "https://resource.logitech.com/w_386,c_limit,q_auto,f_auto,dpr_1.0/d_transparent.gif/content/dam/logitech/en/products/keyboards/g-pro-x-keyboard/gallery/g-pro-x-keyboard-gallery-1-black.png");
        PRODUCT_IMAGES.put("Bàn phím Razer BlackWidow V3","https://assets2.razerzone.com/images/pnx.assets/b1f02f96-2d5a-4c97-8ea5-f8296ab487c5/razer-blackwidow-v3-gallery-1.jpg");
        PRODUCT_IMAGES.put("Bàn phím DareU EK87",        "https://salt.tikicdn.com/cache/750x750/ts/product/a5/5f/f8/94ea71e64b0e29a64a9e9440a2b34f4c.jpg");
        // TAI NGHE
        PRODUCT_IMAGES.put("Tai nghe Razer Barracuda X",    "https://assets2.razerzone.com/images/pnx.assets/4e2b2d04-ef09-481b-abf2-e4ace8b7cde0/razer-barracuda-x-gallery-1.jpg");
        PRODUCT_IMAGES.put("Tai nghe HyperX Cloud II",      "https://media.kingston.com/hyperx/product/hx-product-headset-cloud-ii-black-1-zm-lg.jpg");
        PRODUCT_IMAGES.put("Tai nghe Logitech G733",        "https://resource.logitech.com/w_386,c_limit,q_auto,f_auto,dpr_1.0/d_transparent.gif/content/dam/logitech/en/products/gaming-headsets/g733/gallery/g733-headset-gallery-white-top.png");
        PRODUCT_IMAGES.put("Tai nghe Corsair HS80 RGB",     "https://www.corsair.com/medias/sys_master/images/images/h04/hb5/9057254940702.png");
        PRODUCT_IMAGES.put("Tai nghe SteelSeries Arctis 5", "https://steelseries.com/static/img/arctis-5/arctis-5-black.png");
        PRODUCT_IMAGES.put("Tai nghe ASUS TUF H3",          "https://dlcdnwebimgs.asus.com/gain/9c4c2a50-a3de-4b5a-9a02-f0cc44f01c10/w800");
        // MÀN HÌNH
        PRODUCT_IMAGES.put("Màn hình ASUS TUF 24 inch 144Hz", "https://dlcdnwebimgs.asus.com/gain/42F1B5B1-4D45-4B2A-A9C0-2AFD4FF3B55E/w800");
        PRODUCT_IMAGES.put("Màn hình MSI 27 inch 165Hz",      "https://asset.msi.com/resize/image/global/product/product_1_20220407134524_6250050ccc37d.png62405b38c58fe0f07fcef2367d8a9ba1/600.png");
        PRODUCT_IMAGES.put("Màn hình LG UltraGear 27GL850",   "https://www.lg.com/us/images/monitors/md07534540/gallery/desktop-01.jpg");
        PRODUCT_IMAGES.put("Màn hình Samsung Odyssey G5",     "https://image-us.samsung.com/SamsungUS/home/computing/monitors/gaming/06122020/LC27G55TQWNXZA_001_Front_Black.jpg");
        // PC PART
        PRODUCT_IMAGES.put("CPU Intel Core i5 13400F", "https://www.intel.com/content/dam/www/central-libraries/us/en/images/2022-11/processors-core-i5-13th-gen-badge-rwd.png");
        PRODUCT_IMAGES.put("CPU AMD Ryzen 5 5600X",    "https://www.amd.com/system/files/2020-10/616607-amd-ryzen-5-5600x-pib-left-facing-1260x709_0.png");
        PRODUCT_IMAGES.put("GPU RTX 4060",             "https://www.nvidia.com/content/nvidiaGDC/us/en_US/geforce/graphics-cards/40-series/rtx-4060/_jcr_content/root/responsivegrid/nv_container_392921705/container/nv_image.coreimg.100.1070.png/1687463798289/rtx4060-product-photo-001-v2.png");
        PRODUCT_IMAGES.put("GPU RTX 4070",             "https://www.nvidia.com/content/nvidiaGDC/us/en_US/geforce/graphics-cards/40-series/rtx-4070/_jcr_content/root/responsivegrid/nv_container_392921705/container/nv_image.coreimg.100.1070.png/1680633402585/rtx4070-product-photo-001.png");
        PRODUCT_IMAGES.put("RAM Corsair 16GB DDR4",    "https://www.corsair.com/medias/sys_master/images/images/hf5/hcc/8803049947166.png");
        PRODUCT_IMAGES.put("SSD Samsung 980 1TB",      "https://image-us.samsung.com/SamsungUS/home/computing/memory-storage/solid-state-drives/10012021/MZ-V8V1T0B_001_Front_Black.jpg");
    }

    // ===== LẤY URL ẢNH THEO TÊN SẢN PHẨM =====
    private String getImageUrl(String name) {
        // Ưu tiên ảnh custom do người dùng nhập
        String baseName = name.split(" V")[0].split(" Phiên bản")[0].trim();
        if (CUSTOM_IMAGES.containsKey(name)) return CUSTOM_IMAGES.get(name);
        if (CUSTOM_IMAGES.containsKey(baseName)) return CUSTOM_IMAGES.get(baseName);
        // Tìm chính xác trong map mặc định
        for (Map.Entry<String, String> entry : PRODUCT_IMAGES.entrySet()) {
            if (name.toLowerCase().contains(entry.getKey().toLowerCase()) ||
                entry.getKey().toLowerCase().contains(baseName.toLowerCase())) {
                return entry.getValue();
            }
        }
        // Fallback theo danh mục
        String lower = name.toLowerCase();
        int seed = Math.abs(name.hashCode() % 1000);
        if (lower.contains("laptop"))           return "https://picsum.photos/seed/laptop" + seed + "/200/150";
        if (lower.contains("chuột"))            return "https://picsum.photos/seed/mouse" + seed + "/200/150";
        if (lower.contains("bàn phím"))         return "https://picsum.photos/seed/keyboard" + seed + "/200/150";
        if (lower.contains("tai nghe"))         return "https://picsum.photos/seed/headset" + seed + "/200/150";
        if (lower.contains("màn hình"))         return "https://picsum.photos/seed/monitor" + seed + "/200/150";
        if (lower.contains("cpu") || lower.contains("gpu") || 
            lower.contains("ram") || lower.contains("ssd")) return "https://picsum.photos/seed/pcpart" + seed + "/200/150";
        return "https://picsum.photos/seed/" + seed + "/200/150";
    }

    // ===== LOAD ẢNH ASYNC (dùng chung, tham số width/height) =====
    private void loadImageAsyncSized(JLabel imgLabel, String imageUrl, int w, int h) {
        SwingWorker<ImageIcon, Void> worker = new SwingWorker<>() {
            @Override
            protected ImageIcon doInBackground() {
                try {
                    URL url = new URL(imageUrl);
                    java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(8000);
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                    BufferedImage img = ImageIO.read(conn.getInputStream());
                    if (img != null) {
                        Image scaled = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
                        return new ImageIcon(scaled);
                    }
                } catch (Exception ignored) {}
                return null;
            }
            @Override
            protected void done() {
                try {
                    ImageIcon icon = get();
                    if (icon != null) {
                        imgLabel.setIcon(icon);
                        imgLabel.setText("");
                        imgLabel.revalidate();
                        imgLabel.repaint();
                    }
                } catch (Exception ignored) {}
            }
        };
        worker.execute();
    }

    private void loadImageAsync(JLabel imgLabel, String imageUrl) {
        loadImageAsyncSized(imgLabel, imageUrl, 200, 150);
    }

    private void loadImageAsync450(JLabel imgLabel, String imageUrl) {
        loadImageAsyncSized(imgLabel, imageUrl, 450, 350);
    }

    private void loadImageAsyncThumb(JLabel imgLabel, String imageUrl) {
        loadImageAsyncSized(imgLabel, imageUrl, 80, 80);
    }

    private JPanel createProductCard(String name, String price) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new LineBorder(new Color(220, 220, 220)));
        card.setPreferredSize(new Dimension(200, 280));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        card.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                showProductDetail(name, price);
            }
        });

        // Panel chứa ảnh + nút 📷
        JPanel imageWrapper = new JPanel(new BorderLayout());
        imageWrapper.setBackground(new Color(240, 240, 240));
        imageWrapper.setPreferredSize(new Dimension(200, 150));
        imageWrapper.setMaximumSize(new Dimension(300, 150));

        JLabel imgLabel = new JLabel("Chưa có ảnh", SwingConstants.CENTER);
        imgLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        imgLabel.setForeground(new Color(150, 150, 150));
        imageWrapper.add(imgLabel, BorderLayout.CENTER);

        // Nút 📷 góc trên phải
        JButton camBtn = new JButton("📷");
        camBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
        camBtn.setPreferredSize(new Dimension(32, 24));
        camBtn.setMargin(new Insets(0, 0, 0, 0));
        camBtn.setFocusPainted(false);
        camBtn.setBackground(new Color(255, 255, 255, 200));
        camBtn.setBorder(new LineBorder(new Color(200, 200, 200)));
        camBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        camBtn.setToolTipText("Nhập link ảnh cho sản phẩm này");
        camBtn.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                e.consume(); // không trigger click card
                showSetImageDialog(name, imgLabel);
            }
        });

        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 2));
        topRight.setOpaque(false);
        topRight.add(camBtn);
        imageWrapper.add(topRight, BorderLayout.NORTH);

        // Load ảnh ban đầu
        String initUrl = getImageUrl(name);
        if (!initUrl.isEmpty()) {
            imgLabel.setText("Đang tải...");
            loadImageAsync(imgLabel, initUrl);
        }

        JTextArea nameLabel = new JTextArea(name);
        nameLabel.setWrapStyleWord(true);
        nameLabel.setLineWrap(true);
        nameLabel.setEditable(false);
        nameLabel.setFocusable(false);
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        nameLabel.setBackground(Color.WHITE);
        nameLabel.setBorder(new EmptyBorder(10, 10, 5, 10));

        JLabel priceLabel = new JLabel(price);
        priceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        priceLabel.setForeground(new Color(227, 28, 37));
        priceLabel.setBorder(new EmptyBorder(0, 10, 15, 10));

        card.add(imageWrapper);
        card.add(nameLabel);
        card.add(Box.createVerticalGlue());
        card.add(priceLabel);

        return card;
    }

    private JPanel createLoginPanel() {
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(new Color(244, 244, 244));
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(Color.WHITE);
        box.setBorder(new LineBorder(new Color(220, 220, 220), 1));
        box.setPreferredSize(new Dimension(450, 460));

        JPanel tabPanel = createTabPanel(true);
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JTextField emailField = createStyledTextField("E-mail");
        JPasswordField passField = new JPasswordField("Password");
        passField.setPreferredSize(new Dimension(370, 40));
        passField.setMaximumSize(new Dimension(370, 40));
        passField.setFont(new Font("Arial", Font.PLAIN, 14));
        passField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        passField.setForeground(Color.GRAY);
        passField.setEchoChar((char) 0); // Hiển thị placeholder dạng text
        passField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (String.valueOf(passField.getPassword()).equals("Password")) {
                    passField.setText("");
                    passField.setForeground(Color.BLACK);
                    passField.setEchoChar('●'); // Bật ẩn ký tự khi nhập thật
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (passField.getPassword().length == 0) {
                    passField.setText("Password");
                    passField.setForeground(Color.GRAY);
                    passField.setEchoChar((char) 0); // Hiện lại placeholder
                }
            }
        });

        JPanel forgotPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        forgotPanel.setBackground(Color.WHITE);
        forgotPanel.setPreferredSize(new Dimension(370, 25));
        forgotPanel.setMaximumSize(new Dimension(370, 25));
        forgotPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel forgotLabel = new JLabel("<html><u>Quên mật khẩu</u></html>");
        forgotLabel.setForeground(Color.DARK_GRAY);
        forgotLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { cardLayout.show(mainContentPanel, "FORGOT_PASS"); }
        });
        forgotPanel.add(forgotLabel);
        JButton loginBtn = createStyledButton("Đăng nhập", new Color(227, 28, 37));
        loginBtn.addActionListener(e -> {

            String email = emailField.getText().trim();
            String pass  = String.valueOf(passField.getPassword()).trim();

            if (email.isEmpty() || email.equals("E-mail") ||
                pass.isEmpty() || pass.equals("Password")) {
                JOptionPane.showMessageDialog(this, "Nhập email và password");
                return;
            }

            String json = String.format(
                "{\"email\":\"%s\",\"password\":\"%s\"}",
                email.replace("\"", ""),
                pass.replace("\"", "")
            );

            new Thread(() -> {
                try {
                    String res = ApiClient.post("/api/auth/login", json);

                    System.out.println("RESPONSE = " + res); // 🔥 debug

                    SwingUtilities.invokeLater(() -> {

                        if (res == null || res.trim().isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Không nhận được phản hồi từ server");
                            return;
                        }

                        String cleanRes = res.trim();

                        // ✅ nếu trả JSON → login thành công
                        if (cleanRes.startsWith("{") && cleanRes.endsWith("}")) {
                            JOptionPane.showMessageDialog(null, "Đăng nhập thành công!");
                        }
                        // ❌ backend trả lỗi
                        else if (cleanRes.contains("User not found") || cleanRes.contains("Wrong password")) {
                            JOptionPane.showMessageDialog(null, "Sai tài khoản hoặc mật khẩu!");
                        }
                        // ❌ lỗi khác
                        else {
                            JOptionPane.showMessageDialog(null, "Lỗi server: " + cleanRes);
                        }

                    });

                } catch (Exception ex) {
                    ex.printStackTrace();

                    SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(null, "Không kết nối được backend!")
                    );
                }
            }).start();
        });
        JLabel orLabel = new JLabel("hoặc đăng nhập bằng");
        orLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        orLabel.setForeground(Color.GRAY);
        
        JPanel socialPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        socialPanel.setBackground(Color.WHITE);
        socialPanel.setPreferredSize(new Dimension(370, 42));
        socialPanel.setMaximumSize(new Dimension(370, 42));
        socialPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        socialPanel.add(createStyledButton("Google", new Color(219, 68, 55)));
        socialPanel.add(createStyledButton("Facebook", new Color(66, 103, 178)));

        form.add(emailField); form.add(Box.createRigidArea(new Dimension(0, 15)));
        form.add(passField); form.add(Box.createRigidArea(new Dimension(0, 5)));
        form.add(forgotPanel); form.add(Box.createRigidArea(new Dimension(0, 20)));
        form.add(loginBtn); form.add(Box.createRigidArea(new Dimension(0, 15)));
        form.add(orLabel); form.add(Box.createRigidArea(new Dimension(0, 15)));
        form.add(socialPanel);

        box.add(tabPanel); box.add(form); centerWrapper.add(box);
        return centerWrapper;
    }

    private JPanel createRegisterPanel() {
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(new Color(244, 244, 244));
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(Color.WHITE);
        box.setBorder(new LineBorder(new Color(220, 220, 220), 1));
        box.setPreferredSize(new Dimension(450, 560)); 

        JPanel tabPanel = createTabPanel(false);
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JTextField emailField = createStyledTextField("E-mail");
        JTextField hoField = createStyledTextField("Họ");
        JTextField tenField = createStyledTextField("Tên");
        JPasswordField passField = new JPasswordField("Mật khẩu");
        passField.setPreferredSize(new Dimension(370, 40));
        passField.setMaximumSize(new Dimension(370, 40));
        passField.setFont(new Font("Arial", Font.PLAIN, 14));
        passField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        passField.setForeground(Color.GRAY);
        passField.setEchoChar((char) 0);
        passField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (String.valueOf(passField.getPassword()).equals("Mật khẩu")) {
                    passField.setText("");
                    passField.setForeground(Color.BLACK);
                    passField.setEchoChar('●');
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (passField.getPassword().length == 0) {
                    passField.setText("Mật khẩu");
                    passField.setForeground(Color.GRAY);
                    passField.setEchoChar((char) 0);
                }
            }
        });

        JButton registerBtn = createStyledButton("Tạo tài khoản", new Color(227, 28, 37));
        registerBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String ho    = hoField.getText().trim();
            String ten   = tenField.getText().trim();
            String pass  = String.valueOf(passField.getPassword()).trim();

            if (email.equals("E-mail") || email.isEmpty() ||
                ho.equals("Họ") || ho.isEmpty() ||
                ten.equals("Tên") || ten.isEmpty() ||
                pass.equals("Mật khẩu") || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin");
                return;
            }

            String json = String.format(
                "{\"email\":\"%s\",\"name\":\"%s %s\",\"password\":\"%s\"}",
                email.replace("\"", ""),
                ho.replace("\"", ""),
                ten.replace("\"", ""),
                pass.replace("\"", "")
            );

            new Thread(() -> {
                String res = ApiClient.post("/api/auth/register", json);
                SwingUtilities.invokeLater(() -> {
                    if (res != null && !res.isEmpty() && !res.equals("null")) {
                        JOptionPane.showMessageDialog(this, "Đăng ký thành công! Mời đăng nhập.");
                        cardLayout.show(mainContentPanel, "LOGIN");
                    } else {
                        JOptionPane.showMessageDialog(this, "Đăng ký thất bại (email đã tồn tại?)");
                    }
                });
            }).start();
        });

        JLabel orLabel = new JLabel("hoặc đăng ký bằng");
        orLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        orLabel.setForeground(Color.GRAY);
        
        JPanel socialPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        socialPanel.setBackground(Color.WHITE);
        socialPanel.setPreferredSize(new Dimension(370, 42));
        socialPanel.setMaximumSize(new Dimension(370, 42));
        socialPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        socialPanel.add(createStyledButton("Google", new Color(219, 68, 55)));
        socialPanel.add(createStyledButton("Facebook", new Color(66, 103, 178)));

        form.add(emailField); form.add(Box.createRigidArea(new Dimension(0, 15)));
        form.add(hoField);    form.add(Box.createRigidArea(new Dimension(0, 15)));
        form.add(tenField);   form.add(Box.createRigidArea(new Dimension(0, 15)));
        form.add(passField);  form.add(Box.createRigidArea(new Dimension(0, 25)));
        form.add(registerBtn);form.add(Box.createRigidArea(new Dimension(0, 15)));
        form.add(orLabel);    form.add(Box.createRigidArea(new Dimension(0, 15)));
        form.add(socialPanel);

        box.add(tabPanel); box.add(form); centerWrapper.add(box);
        return centerWrapper;
    }

    private JPanel createForgotPasswordPanel() {
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(new Color(244, 244, 244));
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(Color.WHITE);
        box.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(227, 28, 37, 120), 1),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));
        box.setPreferredSize(new Dimension(450, 260));

        JLabel titleLabel = new JLabel("Quên mật khẩu");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(227, 28, 37));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField inputField = createStyledTextField("E-mail / SĐT");
        JButton submitBtn = createStyledButton("Gửi mã xác minh", new Color(227, 28, 37));
        Dimension btnSize = new Dimension(200, 42);
        submitBtn.setPreferredSize(btnSize); submitBtn.setMaximumSize(btnSize);

        JLabel backLabel = new JLabel("<html><u>Quay lại Đăng nhập</u></html>");
        backLabel.setForeground(Color.GRAY);
        backLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        backLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { cardLayout.show(mainContentPanel, "LOGIN"); }
        });

        box.add(titleLabel); box.add(Box.createRigidArea(new Dimension(0, 25)));
        box.add(inputField); box.add(Box.createRigidArea(new Dimension(0, 20)));
        box.add(submitBtn); box.add(Box.createRigidArea(new Dimension(0, 15)));
        box.add(backLabel);

        centerWrapper.add(box);
        return centerWrapper;
    }

    // --- Các hàm hỗ trợ do đoạn code bị thiếu phần cuối ---
    
    private JPanel createTabPanel(boolean isLoginActive) {
        JPanel tabPanel = new JPanel(new GridLayout(1, 2));
        tabPanel.setMaximumSize(new Dimension(450, 50));
        tabPanel.setPreferredSize(new Dimension(450, 50));
        tabPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel tabLogin = new JLabel("Đăng nhập", SwingConstants.CENTER);
        tabLogin.setFont(new Font("Arial", Font.BOLD, 16));
        tabLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tabLogin.setOpaque(true);
        tabLogin.setBackground(isLoginActive ? Color.WHITE : new Color(240, 240, 240));
        tabLogin.setBorder(isLoginActive ? new MatteBorder(3, 0, 0, 0, new Color(227, 28, 37)) : new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        tabLogin.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { cardLayout.show(mainContentPanel, "LOGIN"); }
        });

        JLabel tabReg = new JLabel("Đăng ký", SwingConstants.CENTER);
        tabReg.setFont(new Font("Arial", Font.BOLD, 16));
        tabReg.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tabReg.setOpaque(true);
        tabReg.setBackground(!isLoginActive ? Color.WHITE : new Color(240, 240, 240));
        tabReg.setBorder(!isLoginActive ? new MatteBorder(3, 0, 0, 0, new Color(227, 28, 37)) : new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        tabReg.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { cardLayout.show(mainContentPanel, "REGISTER"); }
        });

        tabPanel.add(tabLogin);
        tabPanel.add(tabReg);
        return tabPanel;
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField(placeholder);
        field.setPreferredSize(new Dimension(370, 40));
        field.setMaximumSize(new Dimension(370, 40));
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.setForeground(Color.GRAY);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
            }
        });
        return field;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(370, 42));
        btn.setMaximumSize(new Dimension(370, 42));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new RoundedBorder(5, bgColor));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }

    // --- Lớp hỗ trợ vẽ viền bo tròn (Rounded Border) ---
    class RoundedBorder implements Border {
        private int radius;
        private Color color;

        RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius + 1, this.radius + 1, this.radius + 2, this.radius);
        }

        public boolean isBorderOpaque() {
            return true;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GearVNApp().setVisible(true);
        });
    }
}