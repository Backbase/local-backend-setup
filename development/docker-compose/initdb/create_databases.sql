-- Check if the database 'access_control' exists
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'access_control')
BEGIN
CREATE DATABASE access_control;
PRINT 'Database access_control created.';
END
ELSE
BEGIN
    PRINT 'Database access_control already exists.';
END
GO

-- Check if the database 'arrangement_manager' exists
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'arrangement_manager')
BEGIN
CREATE DATABASE arrangement_manager;
PRINT 'Database arrangement_manager created.';
END
ELSE
BEGIN
    PRINT 'Database arrangement_manager already exists.';
END
GO

-- Check if the database 'user_manager' exists
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'user_manager')
BEGIN
CREATE DATABASE user_manager;
PRINT 'Database user_manager created.';
END
ELSE
BEGIN
    PRINT 'Database user_manager already exists.';
END
GO

-- Check if the database 'backbase_identity' exists
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'backbase_identity')
    BEGIN
        CREATE DATABASE backbase_identity;
        PRINT 'Database backbase_identity created.';
    END
ELSE
    BEGIN
        PRINT 'Database backbase_identity already exists.';
    END
GO