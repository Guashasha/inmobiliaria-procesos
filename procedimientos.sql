DELIMITER //
CREATE PROCEDURE InsertAgent(
    IN accountName VARCHAR(50),
    IN p_lastname VARCHAR(50),
    IN accountEmail VARCHAR(70),
    IN accountPhone CHAR(10),
    IN accountPassword VARCHAR(300),
    IN agentPersonelNumber VARCHAR(20)
)
BEGIN
    DECLARE accountId INT;
    INSERT INTO account (name, lastname, type, email, phone, password)
    VALUES (accountName, p_lastname, 'agent', accountEmail, accountPhone, accountPassword);
    SET accountId = LAST_INSERT_ID();
    INSERT INTO agent (accountId, personelNumber)
    VALUES (accountId, agentPersonelNumber);
END//
DELIMITER ;

CALL InsertAgent('Fernando', 'Martinez', 'correo@ejemplo.com', '1234567890', 'contraseña', 'porqueesvarchar');
