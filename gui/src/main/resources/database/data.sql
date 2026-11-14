USE chow_time;

-- insert employee roles
INSERT INTO Role (id, roleName)
VALUES
(1, 'Waiter'),
(2, 'Cook'),
(3, 'Manager');

-- insert employees
-- These are placeholder password values for database testing only.
-- Replace them with real password hashes if testing login functionality.
INSERT INTO Employee (id, roleID, firstName, lastname, username, passwordHash)
VALUES
(1, 1, 'John', 'Smith', 'johnsmith', 'test_hash_1'),
(2, 2, 'David', 'Brown', 'davidbrown', 'test_hash_2'),
(3, 3, 'Sarah', 'Wilson', 'sarahwilson', 'test_hash_3');

-- insert restaurant tables
INSERT INTO RestaurantTable (id, tableNumber, waiterID, capacity, status)
VALUES
(1, 1, 1, 4, 'occupied'),
(2, 2, 1, 2, 'occupied'),
(3, 3, NULL, 6, 'available'),
(4, 4, NULL, 4, 'reserved');

-- insert menu categories
INSERT INTO MenuCategory (id, categoryName)
VALUES
(1, 'Appetizers'),
(2, 'Main Course'),
(3, 'Desserts'),
(4, 'Drinks');

-- insert menu items
INSERT INTO MenuItem
(id, categoryID, itemName, description, price, isAvailable, isCustomizable)
VALUES
(1, 1, 'French Fries', 'Crispy golden fries', 5.99, TRUE, TRUE),
(2, 2, 'Cheeseburger', 'Beef burger with cheese', 12.99, TRUE, TRUE),
(3, 2, 'Chicken Sandwich', 'Grilled chicken sandwich', 10.99, TRUE, FALSE),
(4, 3, 'Chocolate Cake', 'Chocolate cake slice', 6.99, TRUE, FALSE),
(5, 4, 'Lemonade', 'Fresh lemonade', 3.99, TRUE, FALSE);

-- insert allergens
INSERT INTO Allergen (id, allergenName)
VALUES
(1, 'Milk'),
(2, 'Eggs'),
(3, 'Wheat'),
(4, 'Peanuts');

-- connect menu items with their allergens
INSERT INTO MenuItemAllergen (menuItemID, allergenID)
VALUES
(2, 1),
(2, 3),
(3, 3),
(4, 1),
(4, 2),
(4, 3);

-- insert option groups
INSERT INTO OptionGroup (id, groupName)
VALUES
(1, 'Dipping Sauce'),
(2, 'Burger Extras'),
(3, 'Side Choice');

-- connect menu items with their option groups
INSERT INTO ItemOptionGroup
(menuItemID, optionGroupID, minSelections, maxSelections)
VALUES
(1, 1, 0, 2),
(2, 2, 0, 2),
(2, 3, 0, 1);

-- insert menu options
INSERT INTO MenuOption
(id, optionGroupID, optionName, price, isAvailable)
VALUES
(1, 1, 'Ketchup', 0.00, TRUE),
(2, 1, 'Mayo', 0.50, TRUE),
(3, 2, 'Extra Cheese', 1.25, TRUE),
(4, 2, 'Bacon', 2.00, TRUE),
(5, 3, 'Side Salad', 2.50, TRUE),
(6, 3, 'Onion Rings', 3.00, TRUE);

-- insert customer orders
INSERT INTO CustomerOrder
(id, tableID, enteredByEmployeeID, orderDateTime, orderStatus)
VALUES
(1, 2, 1, CURRENT_TIMESTAMP, 'received'),
(2, 4, 1, CURRENT_TIMESTAMP, 'preparing'),
(3, 1, 1, CURRENT_TIMESTAMP, 'completed'),
(4, 3, 1, CURRENT_TIMESTAMP, 'cancelled');

-- insert order items
INSERT INTO OrderItem (id, orderID, menuItemID, quantity)
VALUES
(1, 1, 1, 2),
(2, 1, 2, 1),
(3, 2, 3, 1),
(4, 2, 5, 2),
(5, 3, 4, 1),
(6, 4, 2, 1);

-- insert selected customization options
INSERT INTO OrderItemOption (orderItemID, optionID, quantity)
VALUES
(1, 1, 1),
(2, 3, 1),
(2, 4, 1);

-- insert help requests
INSERT INTO HelpRequest
(id, tableID, handledByEmployeeID, requestTime, requestStatus)
VALUES
(1, 2, NULL, CURRENT_TIMESTAMP, 'pending'),
(2, 4, 1, CURRENT_TIMESTAMP, 'in_progress');