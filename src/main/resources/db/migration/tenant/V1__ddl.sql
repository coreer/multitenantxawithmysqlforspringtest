CREATE TABLE `tenant_data` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tcolumn1` varchar(50) NOT NULL,
  `tcolumn2` varchar(50) NOT NULL,
  `tenant_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
