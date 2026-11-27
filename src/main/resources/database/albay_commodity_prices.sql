-- PresyoTrack Lite | SQLite 3.25+ | PHP
-- Report date: 2026-11-09. Secondary transcription of DA Bantay Presyo.
-- Regional reference is the same-day Bicol modal price, NOT a previous price.
-- No verified earlier local observations or household cleaning-product prices supplied.
PRAGMA foreign_keys = ON;
BEGIN;
CREATE TABLE IF NOT EXISTS commodities (id INTEGER PRIMARY KEY, name TEXT NOT NULL UNIQUE, category TEXT NOT NULL, unit TEXT NOT NULL);
CREATE TABLE IF NOT EXISTS market_prices (commodity_id INTEGER NOT NULL REFERENCES commodities(id), market TEXT NOT NULL, observed_on TEXT NOT NULL, price NUMERIC NOT NULL CHECK(price > 0), source_url TEXT NOT NULL, PRIMARY KEY(commodity_id,market,observed_on));
CREATE TABLE IF NOT EXISTS regional_reference (commodity_id INTEGER NOT NULL REFERENCES commodities(id), observed_on TEXT NOT NULL, price NUMERIC NOT NULL CHECK(price > 0), PRIMARY KEY(commodity_id,observed_on));
INSERT OR IGNORE INTO commodities VALUES
(1,'Regular milled local rice','Rice','kg'),(2,'Well-milled local rice','Rice','kg'),(3,'Premium local rice','Rice','kg'),(4,'Pork belly','Meat','kg'),(5,'Pork shoulder','Meat','kg'),(6,'Dressed whole chicken','Poultry','kg'),(7,'Medium white egg','Eggs','piece'),(8,'Cabbage (Scorpio)','Vegetables','kg'),(9,'Potato','Vegetables','kg'),(10,'Carrots','Vegetables','kg'),(11,'Chayote','Vegetables','kg'),(12,'Baguio beans','Vegetables','kg'),(13,'Baguio pechay','Vegetables','kg'),(14,'Saba banana','Fruit','kg'),(15,'Calamansi','Fruit','kg'),(16,'Refined sugar','Pantry essentials','kg'),(17,'Washed sugar','Pantry essentials','kg'),(18,'Brown sugar','Pantry essentials','kg'),(19,'Palm cooking oil','Pantry essentials','liter'),(20,'Special local rice','Rice','kg'),(21,'Large white egg','Eggs','piece'),(22,'Local round scad','Fish','kg'),(23,'Medium tilapia','Fish','kg');
INSERT OR IGNORE INTO regional_reference SELECT id,'2026-11-09',CASE id WHEN 1 THEN 45 WHEN 2 THEN 52 WHEN 3 THEN 58 WHEN 4 THEN 250 WHEN 5 THEN 250 WHEN 6 THEN 200 WHEN 7 THEN 8 WHEN 8 THEN 180 WHEN 9 THEN 160 WHEN 10 THEN 160 WHEN 11 THEN 60 WHEN 12 THEN 100 WHEN 13 THEN 100 WHEN 14 THEN 45 WHEN 15 THEN 80 WHEN 16 THEN 85 WHEN 17 THEN 75 WHEN 18 THEN 70 WHEN 19 THEN 110 WHEN 20 THEN 60 WHEN 21 THEN 9 WHEN 22 THEN 240 WHEN 23 THEN 180 END FROM commodities WHERE id BETWEEN 1 AND 23;
INSERT OR IGNORE INTO market_prices VALUES
(1,'Legazpi City Public Market, Albay','2026-11-09',42,'https://anomura.today/palengke/bicol/legazpi-city-public-market/'),
(2,'Legazpi City Public Market, Albay','2026-11-09',47,'https://anomura.today/palengke/bicol/legazpi-city-public-market/'),
(3,'Legazpi City Public Market, Albay','2026-11-09',59,'https://anomura.today/palengke/bicol/legazpi-city-public-market/'),
(4,'Legazpi City Public Market, Albay','2026-11-09',260,'https://anomura.today/palengke/bicol/legazpi-city-public-market/'),
(5,'Legazpi City Public Market, Albay','2026-11-09',250,'https://anomura.today/palengke/bicol/legazpi-city-public-market/'),
(6,'Legazpi City Public Market, Albay','2026-11-09',200,'https://anomura.today/palengke/bicol/legazpi-city-public-market/'),
(7,'Legazpi City Public Market, Albay','2026-11-09',8,'https://anomura.today/palengke/bicol/legazpi-city-public-market/'),
(8,'Legazpi City Public Market, Albay','2026-11-09',160,'https://anomura.today/palengke/bicol/legazpi-city-public-market/'),
(9,'Legazpi City Public Market, Albay','2026-11-09',160,'https://anomura.today/palengke/bicol/legazpi-city-public-market/'),
(10,'Legazpi City Public Market, Albay','2026-11-09',160,'https://anomura.today/palengke/bicol/legazpi-city-public-market/'),
(11,'Legazpi City Public Market, Albay','2026-11-09',40,'https://anomura.today/palengke/bicol/legazpi-city-public-market/'),
(12,'Legazpi City Public Market, Albay','2026-11-09',100,'https://anomura.today/palengke/bicol/legazpi-city-public-market/'),
(13,'Legazpi City Public Market, Albay','2026-11-09',140,'https://anomura.today/palengke/bicol/legazpi-city-public-market/'),
(14,'Legazpi City Public Market, Albay','2026-11-09',45,'https://anomura.today/palengke/bicol/legazpi-city-public-market/'),
(15,'Legazpi City Public Market, Albay','2026-11-09',80,'https://anomura.today/palengke/bicol/legazpi-city-public-market/'),
(16,'Legazpi City Public Market, Albay','2026-11-09',100,'https://anomura.today/palengke/bicol/legazpi-city-public-market/'),
(17,'Legazpi City Public Market, Albay','2026-11-09',80,'https://anomura.today/palengke/bicol/legazpi-city-public-market/'),
(18,'Legazpi City Public Market, Albay','2026-11-09',70,'https://anomura.today/palengke/bicol/legazpi-city-public-market/'),
(19,'Legazpi City Public Market, Albay','2026-11-09',120,'https://anomura.today/palengke/bicol/legazpi-city-public-market/');
INSERT OR IGNORE INTO market_prices VALUES
(1,'Ligao City Public Market, Albay','2026-11-09',45,'https://anomura.today/palengke/bicol/ligao-city-public-market/'),
(2,'Ligao City Public Market, Albay','2026-11-09',48,'https://anomura.today/palengke/bicol/ligao-city-public-market/'),
(3,'Ligao City Public Market, Albay','2026-11-09',54,'https://anomura.today/palengke/bicol/ligao-city-public-market/'),
(4,'Ligao City Public Market, Albay','2026-11-09',230,'https://anomura.today/palengke/bicol/ligao-city-public-market/'),
(5,'Ligao City Public Market, Albay','2026-11-09',230,'https://anomura.today/palengke/bicol/ligao-city-public-market/'),
(6,'Ligao City Public Market, Albay','2026-11-09',200,'https://anomura.today/palengke/bicol/ligao-city-public-market/'),
(7,'Ligao City Public Market, Albay','2026-11-09',8,'https://anomura.today/palengke/bicol/ligao-city-public-market/'),
(20,'Ligao City Public Market, Albay','2026-11-09',57,'https://anomura.today/palengke/bicol/ligao-city-public-market/'),
(21,'Ligao City Public Market, Albay','2026-11-09',9,'https://anomura.today/palengke/bicol/ligao-city-public-market/'),
(22,'Ligao City Public Market, Albay','2026-11-09',240,'https://anomura.today/palengke/bicol/ligao-city-public-market/'),
(23,'Ligao City Public Market, Albay','2026-11-09',180,'https://anomura.today/palengke/bicol/ligao-city-public-market/');
CREATE VIEW IF NOT EXISTS price_trends AS
WITH history AS (SELECT *, LAG(price) OVER (PARTITION BY commodity_id,market ORDER BY observed_on) AS previous_price FROM market_prices)
SELECT h.*, c.name,c.category,c.unit, h.price-h.previous_price AS price_difference, ROUND(100.0*(h.price-h.previous_price)/NULLIF(h.previous_price,0),2) AS percentage_change,
CASE WHEN previous_price IS NULL THEN 'NO HISTORY' WHEN price>previous_price THEN 'MARKUP' WHEN price<previous_price THEN 'MARKDOWN' ELSE 'NO CHANGE' END AS status FROM history h JOIN commodities c ON c.id=h.commodity_id;
CREATE VIEW IF NOT EXISTS regional_comparison AS
SELECT p.*,c.name,c.category,c.unit,r.price AS benchmark_price,p.price-r.price AS price_difference,ROUND(100.0*(p.price-r.price)/NULLIF(r.price,0),2) AS percentage_difference,
CASE WHEN r.price IS NULL THEN 'NO BENCHMARK' WHEN p.price>r.price THEN 'ABOVE BENCHMARK' WHEN p.price<r.price THEN 'BELOW BENCHMARK' ELSE 'AT BENCHMARK' END AS status
FROM market_prices p JOIN commodities c ON c.id=p.commodity_id LEFT JOIN regional_reference r ON r.commodity_id=p.commodity_id AND r.observed_on=p.observed_on;
COMMIT;
-- SELECT * FROM price_trends;
-- SELECT * FROM regional_comparison;
-- Add later verified observations to market_prices to calculate real temporal changes.
