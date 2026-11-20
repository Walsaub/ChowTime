USE chow_time;

DROP PROCEDURE IF EXISTS GetOrdersByStatus;
DROP PROCEDURE IF EXISTS GetDailySales;

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
    SELECT
        p_date AS salesDate,
        COALESCE(SUM(
                        OI.quantity *
                        (MI.price + COALESCE(OptionsTotal.optionTotal, 0))
                ), 0
        ) AS totalSales
    FROM CustomerOrder AS CO
    INNER JOIN OrderItem AS OI
        ON CO.id = OI.orderID
    INNER JOIN MenuItem AS MI
        ON OI.menuItemID = MI.id
    LEFT JOIN (
        SELECT
            OIO.orderItemID,
            SUM(MO.price * OIO.quantity) AS optionTotal
        FROM OrderItemOption AS OIO
        INNER JOIN MenuOption AS MO
            ON OIO.optionID = MO.id
        GROUP BY OIO.orderItemID
    ) AS OptionsTotal
        ON OI.id = OptionsTotal.orderItemID
    WHERE DATE(CO.orderDateTime) = p_date
      AND CO.orderStatus <> 'cancelled';
END //

DELIMITER ;