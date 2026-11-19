
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

// ==========================================
// MAIN CLASS
// ==========================================

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                TradingPlatform platform = new TradingPlatform();
                new TradingGUI(platform);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                    null, "Application Error: " + e.getMessage()
                );
            }
        });
    }

    // ==========================================
    // STOCK CLASS
    // ==========================================

    static class Stock {
        String symbol;
        String name;
        double price;
        double change;

        Stock(String symbol, String name, double price) {
            this.symbol = symbol;
            this.name = name;
            this.price = price;
            this.change = 0;
        }

        void updatePrice() {
            double oldPrice = price;
            double movement = (Math.random() * 4) - 2;

            price = Math.max(1, price * (1 + movement / 100));
            change = ((price - oldPrice) / oldPrice) * 100;
        }
    }

    // ==========================================
    // USER CLASS
    // ==========================================

    static class User {
        String username;
        double balance;

        User(String username, double balance) {
            this.username = username;
            this.balance = balance;
        }
    }

    // ==========================================
    // PORTFOLIO CLASS
    // ==========================================

    static class Portfolio {

        static class Holding {
            int quantity;
            double averagePrice;

            Holding(int quantity, double averagePrice) {
                this.quantity = quantity;
                this.averagePrice = averagePrice;
            }
        }

        LinkedHashMap<String, Holding> holdings = new LinkedHashMap<>();

        void buy(String symbol, int quantity, double price) {
            Holding h = holdings.get(symbol);

            if (h == null) {
                holdings.put(symbol, new Holding(quantity, price));
            } else {
                double totalCost =
                    (h.quantity * h.averagePrice) + (quantity * price);

                h.quantity += quantity;
                h.averagePrice = totalCost / h.quantity;
            }
        }

        boolean sell(String symbol, int quantity) {
            Holding h = holdings.get(symbol);

            if (h == null || h.quantity < quantity) {
                return false;
            }

            h.quantity -= quantity;

            if (h.quantity == 0) {
                holdings.remove(symbol);
            }

            return true;
        }

        double getInvestedValue() {
            double total = 0;

            for (Holding h : holdings.values()) {
                total += h.quantity * h.averagePrice;
            }

            return total;
        }

        double getMarketValue(
            LinkedHashMap<String, Stock> stocks
        ) {
            double total = 0;

            for (String symbol : holdings.keySet()) {
                Holding h = holdings.get(symbol);
                Stock s = stocks.get(symbol);

                if (s != null) {
                    total += h.quantity * s.price;
                }
            }

            return total;
        }
    }

    // ==========================================
    // TRANSACTION CLASS
    // ==========================================

    static class Transaction {
        String date;
        String type;
        String symbol;
        int quantity;
        double price;
        double total;

        Transaction(String type, String symbol,
                    int quantity, double price) {

            this.date = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            );

            this.type = type;
            this.symbol = symbol;
            this.quantity = quantity;
            this.price = price;
            this.total = quantity * price;
        }
    }

    // ==========================================
    // FILE MANAGER CLASS
    // ==========================================

    static class FileManager {

        static final String DIR = "data";

        static void initialize() throws IOException {
            File folder = new File(DIR);
            if (!folder.exists()) {
                folder.mkdirs();
            }
        }

        static void saveStocks(
            LinkedHashMap<String, Stock> stocks
        ) throws IOException {

            try (PrintWriter out = new PrintWriter(
                new FileWriter(DIR + "/stocks.csv"))) {

                for (Stock s : stocks.values()) {
                    out.println(s.symbol + "," + s.name + "," + s.price);
                }
            }
        }

        static LinkedHashMap<String, Stock> loadStocks()
            throws IOException {

            LinkedHashMap<String, Stock> stocks = new LinkedHashMap<>();

            File file = new File(DIR + "/stocks.csv");

            if (file.exists()) {
                try (BufferedReader br =
                    new BufferedReader(new FileReader(file))) {

                    String line;

                    while ((line = br.readLine()) != null) {
                        String[] p = line.split(",", -1);

                        if (p.length == 3) {
                            try {
                                Stock s = new Stock(
                                    p[0], p[1], Double.parseDouble(p[2])
                                );
                                stocks.put(s.symbol, s);
                            } catch (NumberFormatException ignored) {
                            }
                        }
                    }
                }
            }

            if (stocks.isEmpty()) {
                stocks.put("RELIANCE",
                    new Stock("RELIANCE", "Reliance Industries", 1450));
                stocks.put("TCS",
                    new Stock("TCS", "Tata Consultancy Services", 3200));
                stocks.put("INFY",
                    new Stock("INFY", "Infosys", 1500));
                stocks.put("HDFCBANK",
                    new Stock("HDFCBANK", "HDFC Bank", 1700));
                stocks.put("ICICIBANK",
                    new Stock("ICICIBANK", "ICICI Bank", 1250));
                stocks.put("ITC",
                    new Stock("ITC", "ITC Limited", 450));
                stocks.put("SBIN",
                    new Stock("SBIN", "State Bank of India", 800));
                stocks.put("WIPRO",
                    new Stock("WIPRO", "Wipro Limited", 550));

                saveStocks(stocks);
            }

            return stocks;
        }

        static void saveUser(User user) throws IOException {
            try (PrintWriter out = new PrintWriter(
                new FileWriter(DIR + "/user.csv"))) {

                out.println(user.username + "," + user.balance);
            }
        }

        static User loadUser() throws IOException {
            File file = new File(DIR + "/user.csv");

            if (!file.exists()) {
                User user = new User("Trader", 100000);
                saveUser(user);
                return user;
            }

            try (BufferedReader br =
                new BufferedReader(new FileReader(file))) {

                String line = br.readLine();

                if (line != null) {
                    String[] p = line.split(",", -1);

                    if (p.length == 2) {
                        try {
                            return new User(
                                p[0], Double.parseDouble(p[1])
                            );
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            }

            User user = new User("Trader", 100000);
            saveUser(user);
            return user;
        }

        static void savePortfolio(Portfolio portfolio)
            throws IOException {

            try (PrintWriter out = new PrintWriter(
                new FileWriter(DIR + "/portfolio.csv"))) {

                for (String symbol : portfolio.holdings.keySet()) {
                    Portfolio.Holding h =
                        portfolio.holdings.get(symbol);

                    out.println(
                        symbol + "," + h.quantity + "," + h.averagePrice
                    );
                }
            }
        }

        static Portfolio loadPortfolio() throws IOException {
            Portfolio portfolio = new Portfolio();

            File file = new File(DIR + "/portfolio.csv");

            if (!file.exists()) {
                return portfolio;
            }

            try (BufferedReader br =
                new BufferedReader(new FileReader(file))) {

                String line;

                while ((line = br.readLine()) != null) {
                    String[] p = line.split(",", -1);

                    if (p.length == 3) {
                        try {
                            int quantity = Integer.parseInt(p[1]);
                            double average = Double.parseDouble(p[2]);

                            if (quantity > 0 && average > 0) {
                                portfolio.holdings.put(
                                    p[0],
                                    new Portfolio.Holding(quantity, average)
                                );
                            }
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            }

            return portfolio;
        }

        static void saveTransactions(
            ArrayList<Transaction> transactions
        ) throws IOException {

            try (PrintWriter out = new PrintWriter(
                new FileWriter(DIR + "/transactions.csv"))) {

                for (Transaction t : transactions) {
                    out.println(
                        t.date + "," + t.type + "," + t.symbol + ","
                        + t.quantity + "," + t.price + "," + t.total
                    );
                }
            }
        }

        static ArrayList<Transaction> loadTransactions()
            throws IOException {

            ArrayList<Transaction> transactions = new ArrayList<>();
            File file = new File(DIR + "/transactions.csv");

            if (!file.exists()) {
                return transactions;
            }

            try (BufferedReader br =
                new BufferedReader(new FileReader(file))) {

                String line;

                while ((line = br.readLine()) != null) {
                    String[] p = line.split(",", -1);

                    if (p.length == 6) {
                        try {
                            Transaction t = new Transaction(
                                p[1], p[2],
                                Integer.parseInt(p[3]),
                                Double.parseDouble(p[4])
                            );

                            t.date = p[0];
                            transactions.add(t);

                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            }

            return transactions;
        }
    }

    // ==========================================
    // TRADING PLATFORM CLASS
    // ==========================================

    static class TradingPlatform {

        User user;
        Portfolio portfolio;
        LinkedHashMap<String, Stock> stocks;
        ArrayList<Transaction> transactions;

        TradingPlatform() throws IOException {
            FileManager.initialize();

            user = FileManager.loadUser();
            portfolio = FileManager.loadPortfolio();
            stocks = FileManager.loadStocks();
            transactions = FileManager.loadTransactions();
        }

        void saveAll() throws IOException {
            FileManager.saveUser(user);
            FileManager.savePortfolio(portfolio);
            FileManager.saveStocks(stocks);
            FileManager.saveTransactions(transactions);
        }

        void buyStock(String symbol, int quantity)
            throws Exception {

            Stock stock = stocks.get(symbol);

            if (stock == null || quantity <= 0) {
                throw new Exception("Invalid stock or quantity.");
            }

            double total = stock.price * quantity;

            if (total > user.balance) {
                throw new Exception("Insufficient balance!");
            }

            user.balance -= total;

            portfolio.buy(symbol, quantity, stock.price);

            transactions.add(
                new Transaction("BUY", symbol, quantity, stock.price)
            );

            saveAll();
        }

        void sellStock(String symbol, int quantity)
            throws Exception {

            Stock stock = stocks.get(symbol);

            if (stock == null || quantity <= 0) {
                throw new Exception("Invalid stock or quantity.");
            }

            if (!portfolio.sell(symbol, quantity)) {
                throw new Exception("You don't own enough shares!");
            }

            double total = stock.price * quantity;

            user.balance += total;

            transactions.add(
                new Transaction("SELL", symbol, quantity, stock.price)
            );

            saveAll();
        }

        void updateMarket() throws IOException {
            for (Stock s : stocks.values()) {
                s.updatePrice();
            }

            FileManager.saveStocks(stocks);
        }

        double getPortfolioValue() {
            return portfolio.getMarketValue(stocks);
        }

        double getProfitLoss() {
            return getPortfolioValue() - portfolio.getInvestedValue();
        }
    }

    // ==========================================
    // TRADING GUI CLASS
    // ==========================================

    static class TradingGUI extends JFrame {

        TradingPlatform platform;

        Color background = new Color(17, 24, 39);
        Color panelColor = new Color(31, 41, 55);
        Color accent = new Color(59, 130, 246);
        Color green = new Color(34, 197, 94);
        Color red = new Color(239, 68, 68);
        Color white = Color.WHITE;

        JLabel balanceLabel;
        JLabel portfolioLabel;
        JLabel investedLabel;
        JLabel profitLabel;

        DefaultTableModel marketModel;
        DefaultTableModel portfolioModel;
        DefaultTableModel historyModel;

        JTable marketTable;
        JTable portfolioTable;
        JTable historyTable;

        TradingGUI(TradingPlatform platform) {
            this.platform = platform;

            setTitle("Stock Trading Platform");
            setSize(1100, 720);
            setMinimumSize(new Dimension(850, 600));
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            getContentPane().setBackground(background);

            createInterface();
            refreshAll();

            // Update simulated prices every 3 seconds.
            new javax.swing.Timer(3000, e -> {
                try {
                    platform.updateMarket();
                    refreshAll();
                } catch (IOException ex) {
                    showError(ex.getMessage());
                }
            }).start();

            setVisible(true);
        }

        JLabel createLabel(String text, int size) {
            JLabel label = new JLabel(text);
            label.setForeground(white);
            label.setFont(new Font("Arial", Font.BOLD, size));
            return label;
        }

        JPanel createCard(String title, JLabel value) {
            JPanel card = new JPanel(new BorderLayout(5, 12));
            card.setBackground(panelColor);
            card.setBorder(
                BorderFactory.createEmptyBorder(18, 20, 18, 20)
            );

            JLabel heading = createLabel(title, 14);
            value.setFont(new Font("Arial", Font.BOLD, 23));
            value.setForeground(white);

            card.add(heading, BorderLayout.NORTH);
            card.add(value, BorderLayout.CENTER);

            return card;
        }

        void createInterface() {

            setLayout(new BorderLayout());

            // HEADER
            JPanel header = new JPanel(new BorderLayout());
            header.setBackground(background);
            header.setBorder(
                BorderFactory.createEmptyBorder(18, 22, 18, 22)
            );

            JLabel title = createLabel("STOCK TRADING PLATFORM", 23);
            JLabel subtitle = new JLabel("Virtual Trading Simulator");
            subtitle.setForeground(Color.LIGHT_GRAY);

            JPanel heading = new JPanel(new GridLayout(2, 1));
            heading.setOpaque(false);
            heading.add(title);
            heading.add(subtitle);

            header.add(heading, BorderLayout.WEST);
            add(header, BorderLayout.NORTH);

            // MAIN CONTENT
            JTabbedPane tabs = new JTabbedPane();
            tabs.setFont(new Font("Arial", Font.BOLD, 14));

            tabs.addTab("Dashboard", createDashboard());
            tabs.addTab("Market", createMarket());
            tabs.addTab("Portfolio", createPortfolio());
            tabs.addTab("History", createHistory());

            add(tabs, BorderLayout.CENTER);
        }

        JPanel createDashboard() {

            JPanel main = new JPanel(new BorderLayout(15, 20));
            main.setBackground(background);
            main.setBorder(
                BorderFactory.createEmptyBorder(22, 22, 22, 22)
            );

            JPanel cards = new JPanel(new GridLayout(2, 2, 15, 15));
            cards.setBackground(background);

            balanceLabel = createLabel("₹0", 23);
            portfolioLabel = createLabel("₹0", 23);
            investedLabel = createLabel("₹0", 23);
            profitLabel = createLabel("₹0", 23);

            cards.add(createCard("AVAILABLE CASH", balanceLabel));
            cards.add(createCard("PORTFOLIO VALUE", portfolioLabel));
            cards.add(createCard("TOTAL INVESTED", investedLabel));
            cards.add(createCard("UNREALIZED PROFIT / LOSS", profitLabel));

            JPanel info = new JPanel(new BorderLayout(10, 10));
            info.setBackground(panelColor);
            info.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
            );

            JLabel welcome = createLabel(
                "Welcome, " + platform.user.username + "!", 21
            );

            JLabel description = new JLabel(
                "<html><div style='color:#D1D5DB;width:500px'>"
                + "Practice stock trading with virtual money. "
                + "Monitor market prices, buy and sell shares, "
                + "and track your portfolio performance.</div></html>"
            );

            info.add(welcome, BorderLayout.NORTH);
            info.add(description, BorderLayout.CENTER);

            main.add(cards, BorderLayout.NORTH);
            main.add(info, BorderLayout.CENTER);

            return main;
        }

        JPanel createMarket() {

            JPanel panel = new JPanel(new BorderLayout(10, 12));
            panel.setBackground(background);
            panel.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
            );

            JLabel heading = createLabel("Market Watch", 21);

            marketModel = new DefaultTableModel(
                new String[]{"Symbol", "Company", "Price (₹)", "Change (%)"},
                0
            ) {
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };

            marketTable = new JTable(marketModel);
            styleTable(marketTable);

            JButton buy = createButton("BUY STOCK", green);
            JButton sell = createButton("SELL STOCK", red);

            buy.addActionListener(e -> tradeFromMarket("BUY"));
            sell.addActionListener(e -> tradeFromMarket("SELL"));

            JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
            actions.setOpaque(false);
            actions.add(buy);
            actions.add(sell);

            panel.add(heading, BorderLayout.NORTH);
            panel.add(new JScrollPane(marketTable), BorderLayout.CENTER);
            panel.add(actions, BorderLayout.SOUTH);

            return panel;
        }

        JPanel createPortfolio() {

            JPanel panel = new JPanel(new BorderLayout(10, 12));
            panel.setBackground(background);
            panel.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
            );

            JLabel heading = createLabel("My Portfolio", 21);

            portfolioModel = new DefaultTableModel(
                new String[]{
                    "Symbol", "Company", "Quantity",
                    "Average Price", "Current Price",
                    "Market Value", "Profit / Loss"
                }, 0
            ) {
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };

            portfolioTable = new JTable(portfolioModel);
            styleTable(portfolioTable);

            JButton sell = createButton("SELL SELECTED STOCK", red);
            sell.addActionListener(e -> tradeFromPortfolio());

            JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
            bottom.setOpaque(false);
            bottom.add(sell);

            panel.add(heading, BorderLayout.NORTH);
            panel.add(new JScrollPane(portfolioTable), BorderLayout.CENTER);
            panel.add(bottom, BorderLayout.SOUTH);

            return panel;
        }

        JPanel createHistory() {

            JPanel panel = new JPanel(new BorderLayout(10, 12));
            panel.setBackground(background);
            panel.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
            );

            JLabel heading = createLabel("Transaction History", 21);

            historyModel = new DefaultTableModel(
                new String[]{
                    "Date & Time", "Type", "Symbol",
                    "Quantity", "Price (₹)", "Total (₹)"
                }, 0
            ) {
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };

            historyTable = new JTable(historyModel);
            styleTable(historyTable);

            panel.add(heading, BorderLayout.NORTH);
            panel.add(new JScrollPane(historyTable), BorderLayout.CENTER);

            return panel;
        }

        JButton createButton(String text, Color color) {
            JButton button = new JButton(text);
            button.setBackground(color);
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setFont(new Font("Arial", Font.BOLD, 13));
            button.setBorder(
                BorderFactory.createEmptyBorder(12, 20, 12, 20)
            );
            return button;
        }

        void styleTable(JTable table) {
            table.setRowHeight(32);
            table.setFont(new Font("Arial", Font.PLAIN, 13));
            table.setBackground(panelColor);
            table.setForeground(white);
            table.setGridColor(new Color(55, 65, 81));
            table.setSelectionBackground(accent);
            table.setSelectionForeground(white);
            table.setShowGrid(true);

            table.getTableHeader().setBackground(
                new Color(55, 65, 81)
            );
            table.getTableHeader().setForeground(white);
            table.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 13)
            );
        }

        String money(double amount) {
            return String.format(Locale.US, "₹%,.2f", amount);
        }

        void refreshAll() {

            // Dashboard
            balanceLabel.setText(money(platform.user.balance));

            portfolioLabel.setText(
                money(platform.getPortfolioValue())
            );

            investedLabel.setText(
                money(platform.portfolio.getInvestedValue())
            );

            double profit = platform.getProfitLoss();

            profitLabel.setText(money(profit));
            profitLabel.setForeground(profit >= 0 ? green : red);

            // Market table
            marketModel.setRowCount(0);

            for (Stock s : platform.stocks.values()) {
                marketModel.addRow(new Object[]{
                    s.symbol,
                    s.name,
                    money(s.price),
                    String.format(Locale.US, "%+.2f%%", s.change)
                });
            }

            // Portfolio table
            portfolioModel.setRowCount(0);

            for (String symbol : platform.portfolio.holdings.keySet()) {

                Portfolio.Holding h =
                    platform.portfolio.holdings.get(symbol);

                Stock s = platform.stocks.get(symbol);

                if (s != null) {
                    double value = h.quantity * s.price;
                    double pl = value -
                        (h.quantity * h.averagePrice);

                    portfolioModel.addRow(new Object[]{
                        symbol,
                        s.name,
                        h.quantity,
                        money(h.averagePrice),
                        money(s.price),
                        money(value),
                        money(pl)
                    });
                }
            }

            // History table
            historyModel.setRowCount(0);

            for (int i = platform.transactions.size() - 1;
                 i >= 0; i--) {

                Transaction t = platform.transactions.get(i);

                historyModel.addRow(new Object[]{
                    t.date,
                    t.type,
                    t.symbol,
                    t.quantity,
                    money(t.price),
                    money(t.total)
                });
            }
        }

        void tradeFromMarket(String type) {

            int row = marketTable.getSelectedRow();

            if (row == -1) {
                showError("Please select a stock first.");
                return;
            }

            String symbol = marketModel.getValueAt(row, 0).toString();
            performTrade(type, symbol);
        }

        void tradeFromPortfolio() {

            int row = portfolioTable.getSelectedRow();

            if (row == -1) {
                showError("Please select a stock from your portfolio.");
                return;
            }

            String symbol =
                portfolioModel.getValueAt(row, 0).toString();

            performTrade("SELL", symbol);
        }

        void performTrade(String type, String symbol) {

            Stock stock = platform.stocks.get(symbol);

            String message =
                type + " " + symbol + "\n\n"
                + "Current Price: " + money(stock.price)
                + "\nAvailable Cash: " + money(platform.user.balance)
                + "\n\nEnter quantity:";

            String input = JOptionPane.showInputDialog(
                this, message, type + " STOCK",
                JOptionPane.QUESTION_MESSAGE
            );

            if (input == null) return;

            try {
                int quantity = Integer.parseInt(input.trim());

                if (quantity <= 0) {
                    throw new Exception("Quantity must be greater than zero.");
                }

                int choice = JOptionPane.showConfirmDialog(
                    this,
                    type + " " + quantity + " shares of " + symbol
                    + " at " + money(stock.price)
                    + " each?\nTotal: "
                    + money(quantity * stock.price),
                    "Confirm Transaction",
                    JOptionPane.YES_NO_OPTION
                );

                if (choice != JOptionPane.YES_OPTION) return;

                if (type.equals("BUY")) {
                    platform.buyStock(symbol, quantity);
                } else {
                    platform.sellStock(symbol, quantity);
                }

                refreshAll();

                JOptionPane.showMessageDialog(
                    this,
                    type + " transaction completed successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );

            } catch (NumberFormatException e) {
                showError("Please enter a valid whole number.");
            } catch (Exception e) {
                showError(e.getMessage());
            }
        }

        void showError(String message) {
            JOptionPane.showMessageDialog(
                this, message, "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
