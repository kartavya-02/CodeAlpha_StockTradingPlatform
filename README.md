# CodeAlpha Stock Trading Platform

## Project Description

The Stock Trading Platform is a Java-based desktop application developed as part of the CodeAlpha Java Programming Internship.

The project simulates a basic stock trading environment where users can explore available stocks, monitor changing stock prices, buy and sell shares using virtual money, and track their investment portfolios.

The application provides an interactive graphical user interface using Java Swing. It uses Object-Oriented Programming (OOP) principles to manage stocks, users, portfolios, and transactions. File handling is implemented to store user balances, stock prices, portfolio details, and transaction history so that information is preserved even after closing the application.

This project is designed for educational purposes to demonstrate the basic working of a stock trading system without involving real money or actual stock market transactions.

## Features

- **Dashboard:** Displays available cash, total invested amount, portfolio value, and unrealized profit or loss.
- **Market Watch:** Displays available stocks, company names, current prices, and percentage price changes.
- **Buy Stocks:** Allows users to purchase shares using virtual money.
- **Sell Stocks:** Allows users to sell shares from their existing portfolio.
- **Portfolio Management:** Tracks owned stocks, quantities, average purchase prices, current market values, and profit or loss.
- **Transaction History:** Maintains records of all buying and selling activities.
- **Simulated Stock Prices:** Automatically updates stock prices at regular intervals to simulate market movements.
- **File Handling:** Saves and loads user details, stock prices, portfolio information, and transaction records using CSV files.
- **Input Validation:** Handles invalid quantities, insufficient balance, and attempts to sell unavailable shares.

## Technologies Used

- **Programming Language:** Java
- **GUI Framework:** Java Swing
- **Concepts:** Object-Oriented Programming (OOP)
- **Data Storage:** File Handling
- **File Format:** CSV
- **IDE:** Visual Studio Code

## Object-Oriented Programming Concepts

The project demonstrates the following OOP concepts:

1. **Classes and Objects:** Separate classes represent stocks, users, portfolios, transactions, file management, and the trading platform.
2. **Encapsulation:** Data and related operations are organized within their respective classes.
3. **Abstraction:** Trading operations and file management are handled through dedicated classes.
4. **Collections:** ArrayList and LinkedHashMap are used to manage stocks, holdings, and transaction records.
5. **Exception Handling:** Invalid operations and file-related errors are handled using exception handling.

## Available Stocks

The application initially includes the following simulated stocks:

| Stock Symbol | Company Name |
|---|---|
| RELIANCE | Reliance Industries |
| TCS | Tata Consultancy Services |
| INFY | Infosys |
| HDFCBANK | HDFC Bank |
| ICICIBANK | ICICI Bank |
| ITC | ITC Limited |
| SBIN | State Bank of India |
| WIPRO | Wipro Limited |

## Project Structure

```text
CodeAlpha_StockTradingPlatform/
│
├── Main.java
├── README.md
│
└── data/
    ├── stocks.csv
    ├── portfolio.csv
    ├── transactions.csv
    └── user.csv
```

The data directory and CSV files are automatically generated when the application runs for the first time.

## Installation and Setup

### Prerequisites

- Java Development Kit (JDK 17 or newer)
- Visual Studio Code or any Java-supported IDE

### Steps to Run

1. Download or clone the project repository.
2. Open the project folder in Visual Studio Code.
3. Make sure Java JDK is installed and configured.
4. Open the integrated terminal.
5. Compile the Java source file:

```bash
javac Main.java
```

6. Run the application:

```bash
java Main
```

7. The Stock Trading Platform graphical interface will open.

## How to Use

1. Launch the application.
2. View the dashboard to check your starting virtual balance.
3. Navigate to the Market tab to explore available stocks.
4. Select a stock and click the Buy Stock button.
5. Enter the quantity and confirm the transaction.
6. Visit the Portfolio tab to monitor your investments.
7. Select an owned stock and use the Sell Stock button to sell shares.
8. Check the History tab to view previous transactions.
9. Monitor the dashboard to track your portfolio value and unrealized profit or loss.

## Data Storage

The application uses CSV files for persistent data storage:

- `stocks.csv` – Stores stock symbols, company names, and current simulated prices.
- `portfolio.csv` – Stores stock holdings, quantities, and average purchase prices.
- `transactions.csv` – Stores transaction dates, types, quantities, prices, and total amounts.
- `user.csv` – Stores the username and available virtual cash balance.

The application automatically loads saved information when restarted.

## Future Improvements

The project can be extended with additional features such as:

- User registration and secure login.
- Live stock market data integration through APIs.
- Interactive stock price charts.
- Multiple user accounts.
- MySQL database integration.
- Advanced portfolio analytics.
- Daily and historical profit/loss tracking.
- Improved dashboard design and navigation.

## Disclaimer

This project is developed strictly for educational and demonstration purposes. It uses simulated stock prices and virtual money. It does not connect to an actual stock exchange, execute real trades, or provide financial advice.

## Internship Information

**Organization:** CodeAlpha  
**Project Name:** Stock Trading Platform  
**Domain:** Java Programming  
**Repository Name:** CodeAlpha_StockTradingPlatform

---

Developed as part of the CodeAlpha Java Programming Internship.
