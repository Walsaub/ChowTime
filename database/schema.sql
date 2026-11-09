-- Database creation and selection
CREATE Database IF NOT EXISTS chow_time;

USE chow_time;

-- Tables creation
CREATE TABLE IF NOT EXISTS Role(
    id INT AUTO_INCREMENT PRIMARY KEY,
    roleName VARCHAR(30) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS Employee(
    id INT AUTO_INCREMENT PRIMARY KEY,
    roleID INT,
    firstName VARCHAR(30) NOT NULL,
    lastname VARCHAR(30) NOT NULL,
    username VARCHAR(30) UNIQUE NOT NULL,
    passwordHash VARCHAR(255) NOT NULL,
    FOREIGN KEY (roleID) REFERENCES Role(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS RestaurantTable(
    id INT AUTO_INCREMENT PRIMARY KEY,
    tableNumber INT UNSIGNED UNIQUE NOT NULL,
    waiterID INT,
    capacity TINYINT UNSIGNED NOT NULL,
    status VARCHAR(15) NOT NULL DEFAULT 'available',
    CHECK (capacity > 0),
    CHECK (status IN ('available', 'occupied', 'reserved')),
    FOREIGN KEY (waiterID) REFERENCES Employee(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS MenuCategory(
    id INT AUTO_INCREMENT PRIMARY KEY,
    categoryName VARCHAR(20) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS MenuItem(
    id INT AUTO_INCREMENT PRIMARY KEY,
    categoryID INT,
    itemName VARCHAR(30) NOT NULL,
    description VARCHAR(100),
    price DECIMAL(5,2) NOT NULL,
    isAvailable BOOLEAN NOT NULL,
    isCustomizable BOOLEAN NOT NULL,
    CHECK (price >= 0),
    FOREIGN KEY (categoryID) REFERENCES MenuCategory(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS Allergen(
    id INT AUTO_INCREMENT PRIMARY KEY,
    allergenName VARCHAR(20) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS MenuItemAllergen(
    menuItemID INT,
    allergenID INT,
    PRIMARY KEY (menuItemID, allergenID),
    FOREIGN KEY (menuItemID) REFERENCES MenuItem(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (allergenID) REFERENCES Allergen(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS OptionGroup(
    id INT AUTO_INCREMENT PRIMARY KEY,
    groupName VARCHAR(20) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS ItemOptionGroup(
    menuItemID INT,
    optionGroupID INT,
    minSelections TINYINT UNSIGNED NOT NULL,
    maxSelections TINYINT UNSIGNED NOT NULL,
    PRIMARY KEY (menuItemID, optionGroupID),
    CHECK (minSelections <= maxSelections AND maxSelections > 0),
    FOREIGN KEY (menuItemID) REFERENCES MenuItem(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (optionGroupID) REFERENCES OptionGroup(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS MenuOption(
    id INT AUTO_INCREMENT PRIMARY KEY,
    optionGroupID INT NOT NULL,
    optionName VARCHAR(20) NOT NULL,
    price DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    isAvailable BOOLEAN NOT NULL DEFAULT TRUE,
    CHECK (price >= 0),
    FOREIGN KEY (optionGroupID) REFERENCES OptionGroup(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS CustomerOrder(
    id INT AUTO_INCREMENT PRIMARY KEY,
    tableID INT,
    enteredByEmployeeID INT,
    orderDateTime DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    orderStatus VARCHAR(15) NOT NULL DEFAULT 'received',
    CHECK (orderStatus IN ('received', 'preparing', 'ready', 'completed', 'cancelled')),
    FOREIGN KEY (tableID) REFERENCES RestaurantTable(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE,
    FOREIGN KEY (enteredByEmployeeID) REFERENCES Employee(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS OrderItem(
    id INT AUTO_INCREMENT PRIMARY KEY,
    orderID INT NOT NULL,
    menuItemID INT NOT NULL,
    quantity TINYINT UNSIGNED NOT NULL,
    CHECK (quantity > 0),
    FOREIGN KEY (orderID) REFERENCES CustomerOrder(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (menuItemID) REFERENCES MenuItem(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS OrderItemOption(
    orderItemID INT,
    optionID INT,
    quantity TINYINT UNSIGNED NOT NULL,
    CHECK (quantity > 0),
    PRIMARY KEY (orderItemID, optionID),
    FOREIGN KEY (orderItemID) REFERENCES OrderItem(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (optionID) REFERENCES MenuOption(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS HelpRequest(
    id INT AUTO_INCREMENT PRIMARY KEY,
    tableID INT,
    handledByEmployeeID INT,
    requestTime DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    requestStatus VARCHAR(15) NOT NULL DEFAULT 'pending',
    CHECK (requestStatus IN ('pending', 'in_progress', 'completed')),
    FOREIGN KEY (tableID) REFERENCES RestaurantTable(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (handledByEmployeeID) REFERENCES Employee(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);