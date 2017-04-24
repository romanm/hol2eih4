DROP TABLE department_icd10_profile;
CREATE TABLE `department_icd10_profile` (
  `department_id` smallint(5) unsigned NOT NULL,
  `group_nr` smallint(6) NOT NULL,
  `item_nr` smallint(6) NOT NULL,
  `icd10` varchar(7) NOT NULL,
  KEY `department_id` (`department_id`),
  KEY `icd10` (`icd10`),
  CONSTRAINT Icd10ReportSort UNIQUE (department_id, group_nr, item_nr),
  CONSTRAINT `department_icd10_profile_ibfk_1` FOREIGN KEY (`department_id`) REFERENCES `department` (`department_id`),
  CONSTRAINT `department_icd10_profile_ibfk_2` FOREIGN KEY (`icd10`) REFERENCES `icd` (`icd_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO hol.department_icd10_profile
(department_id, group_nr, item_nr, icd10)
VALUES(8, 1, 1, 'E10');
INSERT INTO hol.department_icd10_profile
(department_id, group_nr, item_nr, icd10)
VALUES(8, 1, 2, 'N03');
INSERT INTO hol.department_icd10_profile
(department_id, group_nr, item_nr, icd10)
VALUES(8, 1, 3, 'N18');

