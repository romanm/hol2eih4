ALTER TABLE procedure_moz CHANGE procedure_id procedure_moz_id INT;
ALTER TABLE procedure_moz CHANGE procedure_parent_id procedure_moz_parent_id INT;
ALTER TABLE procedure_moz CHANGE procedure_code procedure_moz_code VARCHAR(10);
ALTER TABLE procedure_moz CHANGE procedure_name procedure_moz_name VARCHAR(150);
ALTER TABLE procedure_moz CHANGE procedure_parent_name procedure_moz_parent_name VARCHAR(150);

drop TABLE operation_history_moz IF EXISTS;
CREATE TABLE operation_history_moz(
    operation_history_moz_id INT UNSIGNED NOT NULL,
    procedure_moz_id INT NOT NULL,
    UNIQUE (operation_history_moz_id),
    FOREIGN KEY (operation_history_moz_id)
            REFERENCES operation_history(operation_history_id),
    FOREIGN KEY (procedure_moz_id)
            REFERENCES procedure_moz(procedure_moz_id)
);

INSERT INTO operation_history_moz SELECT operation_history_id, procedure_id FROM operation_history;

ALTER TABLE operation_history DROP FOREIGN KEY procedure_id;
ALTER TABLE operation_history DROP COLUMN procedure_id;