# Chow Time – Restaurant Management System

## Project Overview

Chow Time is a restaurant management system designed to make restaurant operations easier and more organized. The system allows customers to view the menu and place orders, while restaurant employees can manage tables, process orders, and handle customer requests.

The main goal of this project is to reduce customer waiting times, prevent order mistakes, and make it easier for restaurant employees and managers to manage daily restaurant activities.

## Main Users

The system has four main types of users:

- **Customers:** View the menu, check food details and allergens, customize food items, place orders, and request assistance.
- **Waiters:** View restaurant tables, assign themselves to tables, manage customer orders, and check order statuses.
- **Cooks:** View incoming orders and update their status as they are being prepared.
- **Managers:** Manage menu items and employees, and view stored orders and sales information.

## Main Features

- View menu items, descriptions, prices, and allergen information.
- Customize food orders by selecting available options.
- Add, modify, or remove order items before submitting an order.
- View restaurant tables and assign waiters.
- Allow cooks to view incoming orders and update their status.
- Allow customers to request assistance from restaurant staff.
- Manage menu items, categories, and allergens.
- Manage employee information.
- View order history and sales information.

## Database Design

The project uses a relational database to store and organize restaurant information.

The database consists of 14 tables:

1. Roles
2. Employee
3. RestaurantTable
4. MenuCategory
5. MenuItem
6. Allergen
7. MenuItemAllergen
8. OptionGroup
9. ItemOptionGroup
10. CustomOption
11. OrderItemOption
12. CustomerOrder
13. OrderItem
14. HelpRequest

The database uses primary keys and foreign keys to maintain relationships between tables and keep the stored information organized.

The database design also considers normalization up to Third Normal Form (3NF) to reduce unnecessary data duplication and improve data consistency.

## Technologies Used

- Relational Database 
- SQL

## Project Structure

The project contains the database design, including the ER diagram, relational schema, and table definitions. Additional application files and source code can be added as development progresses.

## Project Goals

The goals of Chow Time are to:

- Improve communication between customers and restaurant employees.
- Make the ordering process easier.
- Reduce mistakes when handling customer orders.
- Help cooks organize and process incoming orders.
- Give managers a better way to manage restaurant information.
- Keep restaurant data organized and consistent.


## Project Information

**Course:** COMP 4150 – Database Systems

**Project Name:** Chow Time

**Description:** Restaurant Ordering and Management System