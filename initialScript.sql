INSERT INTO `matefunds`.`LICEO` (MOODLEAPIUSERTOKEN, MOODLEURI,MOODLEWSSERVICE,NOMBRE)
VALUES('b3ddd49822a81a429a45afbcd14bb0ed', 'https://matefun.moodlecloud.com', 'sample', 'Liceo Moodlecloud');

INSERT INTO `matefunds`.`USUARIO` (`CEDULA`, `DTYPE`, `APELLIDO`, `COURSEID`, `NOMBRE`, `PASSWORD`, `ROLEID`)
VALUES ('matefunadmin', 'Admin', 'Matefun', NULL, 'Admin', 'dd9048d29561d898bd75276923990ab8ea01bb73', NULL);

INSERT INTO `matefunds`.`USUARIO` (`CEDULA`, `DTYPE`, `APELLIDO`, `COURSEID`, `NOMBRE`, `PASSWORD`, `ROLEID`)
VALUES ('admin', 'AdminLiceo', 'Liceo 1', NULL, 'Admin', 'NO-PASSWORD', NULL);

INSERT INTO `matefunds`.`ADMINISTRADORES_LICEO` (`LICEOID`, `administradores_CEDULA`) VALUES ('1', 'admin');

