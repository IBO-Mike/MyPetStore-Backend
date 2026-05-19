-- 收藏表
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userid` varchar(80) NOT NULL,
  `productid` varchar(10) NOT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_favorite_user_product` (`userid`,`productid`),
  KEY `idx_favorite_userid` (`userid`),
  KEY `idx_favorite_productid` (`productid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 商品对比表
DROP TABLE IF EXISTS `compare`;
CREATE TABLE `compare` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userid` varchar(80) NOT NULL,
  `productid` varchar(10) NOT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_compare_user_product` (`userid`,`productid`),
  KEY `idx_compare_userid` (`userid`),
  KEY `idx_compare_productid` (`productid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
