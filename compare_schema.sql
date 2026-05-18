-- 商品对比表
DROP TABLE IF EXISTS `compare`;
CREATE TABLE `compare` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userid` varchar(80) NOT NULL,
  `productid` varchar(10) NOT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
