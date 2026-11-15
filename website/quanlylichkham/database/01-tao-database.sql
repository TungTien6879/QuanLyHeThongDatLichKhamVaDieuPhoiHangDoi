IF DB_ID(N'QuanLyLichKham') IS NULL
    CREATE DATABASE QuanLyLichKham;
GO
-- Cac bang duoc Hibernate tao qua spring.jpa.hibernate.ddl-auto=update
-- Chi su dung cho do an/development. Trien khai that nen dung Flyway/Liquibase.
SELECT name FROM sys.databases WHERE name = N'QuanLyLichKham';
