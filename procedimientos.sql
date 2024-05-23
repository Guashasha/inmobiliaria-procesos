DELIMITER //
CREATE PROCEDURE InsertAgent(IN accountName VARCHAR(50), IN accountEmail VARCHAR(70), IN accountPhone CHAR(10), IN accountPassword VARCHAR(300), IN agentPersonelNumber VARCHAR(20))
BEGIN
    DECLARE accountId INT;
    INSERT INTO account (name, type, email, phone, password) VALUES (accountName, 'agent', accountEmail, accountPhone, accountPassword);
        SET accountId = LAST_INSERT_ID();
        INSERT INTO agent (accountId, personelNumber) VALUES (accountId, agentPersonelNumber);
END//

DELIMITER ;

CALL InsertAgent('Padalustro3000', 'correo@ejemplo.com', '1234567890', 'contraseña', 'porqueesvarchar');
