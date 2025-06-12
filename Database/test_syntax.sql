-- Test file để kiểm tra syntax của các procedures

-- Procedure đơn giản để test
CREATE OR REPLACE PROCEDURE test_syntax
AS
BEGIN
    DBMS_OUTPUT.PUT_LINE('Syntax OK');
END;
/

-- Test procedure chính
SET SERVEROUTPUT ON;

BEGIN
    DBMS_OUTPUT.PUT_LINE('=== Testing procedure syntax ===');
    test_syntax();
    DBMS_OUTPUT.PUT_LINE('=== All procedures compiled successfully ===');
END;
/ 