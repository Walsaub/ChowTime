USE chow_time;

DROP TRIGGER IF EXISTS CheckMenuItemBeforeInsert;
DROP TRIGGER IF EXISTS CheckMenuItemBeforeUpdate;

DELIMITER //

-- check menu item availability before inserting an order item
CREATE TRIGGER CheckMenuItemBeforeInsert
BEFORE INSERT ON OrderItem
FOR EACH ROW
BEGIN
    DECLARE itemExists INT DEFAULT 0;
    DECLARE itemAvailability BOOLEAN DEFAULT FALSE;

    SELECT COUNT(*), MAX(isAvailable)
    INTO itemExists, itemAvailability
    FROM MenuItem
    WHERE id = NEW.menuItemID;

    IF itemExists = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Menu item does not exist';

    ELSEIF itemAvailability = FALSE THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'This menu item is currently unavailable';
    END IF;
END //

-- check menu item availability before updating an order item
CREATE TRIGGER CheckMenuItemBeforeUpdate
BEFORE UPDATE ON OrderItem
FOR EACH ROW
BEGIN
    DECLARE itemExists INT DEFAULT 0;
    DECLARE itemAvailability BOOLEAN DEFAULT FALSE;

    SELECT COUNT(*), MAX(isAvailable)
    INTO itemExists, itemAvailability
    FROM MenuItem
    WHERE id = NEW.menuItemID;

    IF itemExists = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Menu item does not exist';

    ELSEIF itemAvailability = FALSE THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'This menu item is currently unavailable';
    END IF;
END //

DELIMITER ;