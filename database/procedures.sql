DELIMITER //

CREATE PROCEDURE GetOrdersByStatus(IN p_status VARCHAR(15))
BEGIN
    SELECT CO.id AS orderID,
           RT.tableNumber,
           CO.orderDateTime,
           CO.orderStatus
    FROM CustomerOrder AS CO
    LEFT JOIN RestaurantTable AS RT
    ON CO.tableID = RT.id
    WHERE CO.orderStatus = p_status
    ORDER BY CO.orderDateTime ASC;
END //

DELIMITER ;

DELIMITER //

CREATE PROCEDURE GetDailySales(IN p_date DATE)
BEGIN
    SELECT p_date AS salesDate,
           COALESCE(SUM(OI.quantity * OI.unitPrice), 0) AS totalSales
    FROM CustomerOrder AS CO
    INNER JOIN OrderItem AS OI
    ON CO.id = OI.orderID
    WHERE DATE(CO.orderDateTime) = p_date
      AND CO.orderStatus <> 'cancelled';
END //

DELIMITER ;