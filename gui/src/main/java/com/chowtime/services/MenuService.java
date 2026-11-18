package com.chowtime.services;

import com.chowtime.database.DatabaseConnection;
import com.chowtime.models.MenuItem;
import com.chowtime.models.MenuOption;
import com.chowtime.models.OptionGroup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MenuService {

    private static final String AVAILABLE_ITEMS_QUERY = """
        SELECT mi.id, mi.itemName, mi.description, mi.price,
               mi.isCustomizable, mc.categoryName,
               (
                   SELECT GROUP_CONCAT(a.allergenName SEPARATOR ', ')
                   FROM MenuItemAllergen mia
                   JOIN Allergen a ON mia.allergenID = a.id
                   WHERE mia.menuItemID = mi.id
               ) AS allergens
        FROM MenuItem mi
        LEFT JOIN MenuCategory mc ON mi.categoryID = mc.id
        WHERE mi.isAvailable = TRUE
        ORDER BY mc.categoryName, mi.itemName
        """;

    private static final String AVAILABLE_CATEGORIES_QUERY = """
            SELECT DISTINCT mc.categoryName
            FROM MenuCategory mc
            JOIN MenuItem mi ON mi.categoryID = mc.id
            WHERE mi.isAvailable = TRUE
            ORDER BY mc.categoryName
            """;

    private static final String ITEMS_BY_CATEGORY_QUERY = """
        SELECT mi.id, mi.itemName, mi.description, mi.price,
               mi.isCustomizable, mc.categoryName,
               (
                   SELECT GROUP_CONCAT(a.allergenName SEPARATOR ', ')
                   FROM MenuItemAllergen mia
                   JOIN Allergen a ON mia.allergenID = a.id
                   WHERE mia.menuItemID = mi.id
               ) AS allergens
        FROM MenuItem mi
        LEFT JOIN MenuCategory mc ON mi.categoryID = mc.id
        WHERE mi.isAvailable = TRUE
          AND mc.categoryName = ?
        ORDER BY mi.itemName
        """;

    private static final String ITEM_OPTIONS_QUERY = """
        SELECT og.id AS groupID, og.groupName,
               iog.minSelections, iog.maxSelections,
               mo.id AS optionID, mo.optionName, mo.price
        FROM ItemOptionGroup iog
        JOIN OptionGroup og ON iog.optionGroupID = og.id
        JOIN MenuOption mo ON mo.optionGroupID = og.id
        WHERE iog.menuItemID = ?
          AND mo.isAvailable = TRUE
        ORDER BY og.groupName, mo.optionName
        """;

    // get all the available menu items
    public List<MenuItem> getAvailableItems() throws SQLException{
        List<MenuItem> items = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getDatabaseConnection();
             PreparedStatement statement = connection.prepareStatement(AVAILABLE_ITEMS_QUERY);
             ResultSet result = statement.executeQuery()){
            while (result.next()){
                items.add(createMenuItem(result));
            }
        }
        return items;
    }

    // get categories that have available food items
    public List<String> getAvailableCategories() throws SQLException{
        List<String> categories = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getDatabaseConnection();
             PreparedStatement statement = connection.prepareStatement(AVAILABLE_CATEGORIES_QUERY);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                categories.add(result.getString("categoryName"));
            }
        }

        return categories;
    }

    // get available food items from one category
    public List<MenuItem> getAvailableItemsByCategory(String categoryName) throws SQLException {
        List<MenuItem> items = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getDatabaseConnection();
             PreparedStatement statement = connection.prepareStatement(ITEMS_BY_CATEGORY_QUERY)){
            statement.setString(1, categoryName);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    items.add(createMenuItem(result));
                }
            }
        }

        return items;
    }

    // get available options for a menu item
    public List<OptionGroup> getOptionGroupsForMenuItem(int menuItemID) throws SQLException{
        Map<Integer, OptionGroup> groups = new LinkedHashMap<>();

        try (Connection connection = DatabaseConnection.getDatabaseConnection();
             PreparedStatement statement = connection.prepareStatement(ITEM_OPTIONS_QUERY)) {
            statement.setInt(1, menuItemID);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()){
                    int groupID = result.getInt("groupID");
                    OptionGroup group = groups.get(groupID);
                    if (group == null){
                        group = new OptionGroup(
                                groupID,
                                result.getString("groupName"),
                                result.getInt("minSelections"),
                                result.getInt("maxSelections")
                        );
                        groups.put(groupID, group);
                    }

                    MenuOption option = new MenuOption(
                            result.getInt("optionID"),
                            result.getString("optionName"),
                            result.getBigDecimal("price")
                    );
                    group.addOption(option);
                }
            }
        }
        return new ArrayList<>(groups.values());
    }

    // convert a database row into a MenuItem object
    private MenuItem createMenuItem(ResultSet result) throws SQLException {
        MenuItem menuItem = new MenuItem(
                result.getInt("id"),
                result.getString("itemName"),
                result.getString("description"),
                result.getString("categoryName"),
                result.getBigDecimal("price"),
                result.getBoolean("isCustomizable")
        );

        String allergenText = result.getString("allergens");
        if (allergenText != null && !allergenText.isBlank()){
            menuItem.setAllergens(new ArrayList<>(List.of(allergenText.split(", "))));
        } else {
            menuItem.setAllergens(new ArrayList<>());
        }
        return menuItem;
    }

}
