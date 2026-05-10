insert into china_order_entity
	(id, supplier, order_date, is_delivered, delivery_cost, delivery_mass, delivery_volume, stock_cost, number)
values
	(1, 'supplier-init', '2026-05-07', false, 0.0, 0.0, 0.0, 15000.0, 'init-order-1');

insert into china_stock_entity
    (id, name, quantity, ozon_id, artikul, price_rub, price, delivery_costs, delivery_usd, stock_entity_id)
values
    (1, 'Мини зонт серый', 32, '1134733705', '0000012', 200.0, 0.0, 0.0, 0.0, 1);

insert into china_stock_entity
	(id, name, quantity, ozon_id, artikul, price_rub, price, delivery_costs, delivery_usd, stock_entity_id)
values
	(2, 'Мини зонт черный', 10, '3389954573', '0000063', 200.0, 0.0, 0.0, 0.0, 1);

insert into china_stock_entity
	(id, name, quantity, ozon_id, artikul, price_rub, price, delivery_costs, delivery_usd, stock_entity_id)
values
	(3, 'Мини зонт розовый', 21, '1134731178', '0000011', 200.0, 0.0, 0.0, 0.0, 1);

insert into china_stock_entity
	(id, name, quantity, ozon_id, artikul, price_rub, price, delivery_costs, delivery_usd, stock_entity_id)
values
	(4, 'Мини зонт бежевый', 75, '1134715033', '0000010', 200.0, 0.0, 0.0, 0.0, 1);


insert into china_order_entity
    (id, supplier, order_date, is_delivered, delivery_cost, delivery_mass, delivery_volume, stock_cost, number)
values
    (2, 'supplier-init-2', '2026-05-08', false, 0.0, 0.0, 0.0, 10000.0, 'init-order-2');

insert into china_stock_entity
    (id, name, quantity, ozon_id, artikul, price_rub, price, delivery_costs, delivery_usd, stock_entity_id)
values
    (5, 'Кофр серый', 20, '3269311329', '0000059', 200.0, 0.0, 0.0, 0.0, 2);
