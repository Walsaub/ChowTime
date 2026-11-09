/*
    =========================================
    Roles QUERIES
    =========================================
*/

-- get all the roles
SELECT * FROM Role;

/*
    =========================================
    Employees QUERIES
    =========================================
*/

-- get all employees
SELECT * FROM Employee;

-- get all employees with their role names
SELECT E.id, E.firstName, E.lastname, E.username, R.roleName
FROM Employee AS E
INNER JOIN Role AS R
ON E.roleID = R.id;

/*
    =========================================
    Restaurant Table QUERIES
    =========================================
*/

-- get all the tables in the restaurant
SELECT * FROM RestaurantTable;

-- get available tables
SELECT * FROM RestaurantTable
WHERE status = 'available';

/*
    =========================================
    Menu Category QUERIES
    =========================================
*/

-- get all categories
SELECT * FROM MenuCategory;

/*
    =========================================
    Menu Item QUERIES
    =========================================
*/

-- get all menu items
SELECT * FROM MenuItem;

-- get the menu item name and category name
SELECT M.itemName, C.categoryName
FROM MenuItem AS M
INNER JOIN MenuCategory AS C
ON M.categoryID = C.id;

-- get menu item by category
SELECT * FROM MenuItem
WHERE categoryID = 1;

/*
    =========================================
    Allergen QUERIES
    =========================================
*/

-- get all allergen
SELECT * FROM Allergen;

/*
    =========================================
    Menu Item Allergen QUERIES
    =========================================
*/

-- get all menu items and its allergen if any
SELECT M.itemName, A.allergenName
FROM MenuItem AS M
LEFT JOIN MenuItemAllergen AS MA 
ON M.id = MA.menuItemID
LEFT JOIN Allergen AS A
ON MA.allergenID = A.id;

-- get all menu items that have allergen
SELECT DISTINCT M.itemName
FROM MenuItemAllergen AS MA
INNER JOIN MenuItem AS M
ON MA.menuItemID = M.id;

/*
    =========================================
    Option Group QUERIES
    =========================================
*/

-- get all the options group
SELECT * FROM OptionGroup;

/*
    =========================================
    Item Option Group QUERIES
    =========================================
*/

-- get all the menu items and its group optin if any
SELECT M.itemName, OG.groupName
FROM MenuItem AS M
LEFT JOIN ItemOptionGroup AS IOP
ON M.id = IOP.menuItemID
LEFT JOIN OptionGroup AS OG
ON IOP.optionGroupID = OG.id;

-- get all customizable menu items with their available options
SELECT M.itemName, OG.groupName, O.optionName, O.price
FROM MenuItem AS M
INNER JOIN ItemOptionGroup AS IOG
ON M.id = IOG.menuItemID
INNER JOIN OptionGroup AS OG
ON IOG.optionGroupID = OG.id
INNER JOIN MenuOption AS O
ON O.optionGroupID = OG.id
WHERE M.isCustomizable = TRUE
    AND M.isAvailable = TRUE
    AND O.isAvailable = TRUE;

/*
    =========================================
    Menu Option QUERIES
    =========================================
*/

-- get all options 
SELECT * FROM MenuOption;

-- get the option name with its group name
SELECT O.optionName, OG.groupName
FROM MenuOption AS O
INNER JOIN OptionGroup AS OG
ON O.optionGroupID = OG.id;

-- get the options by group
SELECT * FROM MenuOption
WHERE optionGroupID = 1;

/*
    =========================================
    Customer Order QUERIES
    =========================================
*/

-- get all the orders
SELECT * FROM CustomerOrder;

-- get the orders for a specific table
SELECT * FROM CustomerOrder
WHERE tableID = 2;

-- get all the orders that was entered by an employee
SELECT * FROM CustomerOrder
WHERE enteredByEmployeeID IS NOT NULL;

-- get all the orders in one day
SELECT * FROM CustomerOrder
WHERE orderDateTime >= '2026-11-07 00:00:00'
    AND orderDateTime < '2026-11-08 00:00:00';

-- get orders by status
SELECT * FROM CustomerOrder
WHERE orderStatus = 'ready';

-- get all orders that are waiting to be prepared
SELECT CO.id AS orderID, MI.itemName, OI.quantity, CO.orderDateTime
FROM CustomerOrder AS CO
INNER JOIN OrderItem AS OI
ON CO.id = OI.orderID
INNER JOIN MenuItem AS MI
ON OI.menuItemID = MI.id
WHERE CO.orderStatus = 'received'
ORDER BY CO.orderDateTime;

/*
    =========================================
    Order Item QUERIES
    =========================================
*/

-- get all the orders with the its items
SELECT OI.orderID, MI.itemName, OI.quantity
FROM OrderItem AS OI
INNER JOIN MenuItem AS MI
ON OI.menuItemID = MI.id;

-- get all the order items of a specific table at a specific time
SELECT MI.itemName, OI.quantity
FROM OrderItem AS OI
INNER JOIN MenuItem AS MI
ON OI.menuItemID = MI.id
WHERE OI.orderID IN (
    SELECT CO.id
    FROM CustomerOrder AS CO
    WHERE CO.tableID = 2 
        AND CO.orderDateTime >= '2026-11-07 00:00:00' 
        AND CO.orderDateTime < '2026-11-08 00:00:00'
);

-- get the sum of each item that have been orderd
SELECT MI.itemName, SUM(OI.quantity) AS total
FROM OrderItem AS OI
INNER JOIN MenuItem AS MI
ON OI.menuItemID = MI.id
GROUP BY OI.menuItemID
ORDER BY total DESC;

-- get the total sales for a specific day
SELECT DATE(CO.orderDateTime) AS orderDate, SUM(OI.quantity * MI.price) AS totalSales
FROM CustomerOrder AS CO
INNER JOIN OrderItem AS OI
ON CO.id = OI.orderID
INNER JOIN MenuItem AS MI
ON OI.menuItemID = MI.id
WHERE CO.orderDateTime >= '2026-11-07 00:00:00'
    AND CO.orderDateTime < '2026-11-08 00:00:00'
    AND CO.orderStatus <> 'cancelled'
GROUP BY DATE(CO.orderDateTime)

/*
    =========================================
    Order Item Option QUERIES
    =========================================
*/

-- get all customization options selected for a specific order
SELECT OI.orderID, MI.itemName, MO.optionName, OIO.quantity
FROM OrderItemOption AS OIO
INNER JOIN OrderItem AS OI
ON OIO.orderItemID = OI.id
INNER JOIN MenuItem AS MI
ON OI.menuItemID = MI.id
INNER JOIN MenuOption AS MO
ON OIO.optionID = MO.id
WHERE OI.orderID = 1;

/*
    =========================================
    Help Request QUERIES
    =========================================
*/

-- get all pending and in-progress help requests
SELECT HR.id, RT.tableNumber, HR.requestTime, HR.requestStatus, E.firstName, E.lastname
FROM HelpRequest AS HR
LEFT JOIN RestaurantTable AS RT
ON HR.tableID = RT.id
LEFT JOIN Employee AS E
ON HR.handledByEmployeeID = E.id
WHERE HR.requestStatus IN ('pending', 'in_progress')
ORDER BY HR.requestTime;