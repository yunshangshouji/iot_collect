-- meter_kind
INSERT INTO zhuboss_tx.meter_kind(app_id,kind_code,kind_name,plc_flag,persist_flag,create_time)
SELECT 603,kind_code,kind_name,0,0,NOW() FROM test.`meter_kind`;

-- meter_kind_read (遥测)
INSERT INTO zhuboss_tx.meter_kind_read(app_id,meter_kind_id,target_code,target_name,signal_flag,scale,unit,persist_flag,create_time)
SELECT 603,(SELECT id FROM zhuboss_tx.meter_kind WHERE app_id = 603 AND kind_code = test.`meter_kind_target`.`meter_kind`) AS meter_kind_id,
target_code,target_name,0,scale,unit,0,NOW()
 FROM test.`meter_kind_target`;
 
 -- meter_kind_read (遥信)
 INSERT INTO zhuboss_tx.meter_kind_read(app_id,meter_kind_id,target_code,target_name,signal_flag,scale,persist_flag,create_time)
SELECT 603,(SELECT id FROM zhuboss_tx.meter_kind WHERE app_id = 603 AND kind_code = test.`meter_kind_signal`.`meter_kind`) AS meter_kind_id,
target_code,target_name,1,0,0,NOW()
 FROM test.`meter_kind_signal` WHERE target_name <> '烟感报警';
 
 -- meter_type
 INSERT INTO zhuboss_tx.`meter_type`(app_id,type_name,meter_kind_id,protocol,baud_rate,parity,byte_size,stop_bits,read_mill_seconds,create_time,alive_flag)
 SELECT 603,type_name,(SELECT id FROM zhuboss_tx.meter_kind WHERE app_id = 603 AND kind_code = test.`meter_type`.`meter_type`) AS meter_kind_id ,'MODBUS',9600,'N',8,1,500,NOW(),1
 FROM test.`meter_type` WHERE protocol = 'modbus';
 
 -- meter_type_read【no loop】
 INSERT INTO zhuboss_tx.meter_type_read(app_id,meter_type_id,cmd,seq,start_addr,len,end_addr,create_time)
 SELECT 603,id AS meter_type_id,3 AS cmd,1 AS seq,0 AS start_addr,NULL AS len,0 AS end_addr,NOW() FROM zhuboss_tx.`meter_type` WHERE app_id = 603 ;
 
 -- meter_type_read_target
 INSERT INTO zhuboss_tx.`meter_type_read_target`(app_id,meter_type_id,meter_kind_read_id,value_type,read_id,addr,addr_hex,ratiovar,create_time)
 SELECT 603,
	(SELECT zhuboss_tx.`meter_type`.id FROM zhuboss_tx.`meter_type` WHERE app_id=603 AND type_name = test.`meter_type`.`type_name`) AS meter_type_id,
	
	(SELECT zhuboss_tx.`meter_kind_read`.id FROM zhuboss_tx.`meter_kind_read` JOIN zhuboss_tx.`meter_kind` ON meter_kind_id = zhuboss_tx.`meter_kind`.id
	 WHERE  zhuboss_tx.`meter_kind_read`.app_id=603 AND zhuboss_tx.`meter_kind`.app_id = 603 AND zhuboss_tx.`meter_kind`.`kind_code` = test.`meter_type`.`meter_type` AND type_name = test.`meter_type`.`type_name` AND zhuboss_tx.`meter_kind_read`.`target_code`=test.`meter_type_target`.`target_code`
	 ) AS meter_kind_read_id,
	 UPPER(test.`meter_type_target`.func) AS value_type,
	 (SELECT zhuboss_tx.`meter_type_read`.id FROM zhuboss_tx.meter_kind,zhuboss_tx.`meter_type`,zhuboss_tx.`meter_type_read` WHERE zhuboss_tx.meter_kind.`app_id` = 603 AND zhuboss_tx.`meter_type`.`app_id`=603 AND zhuboss_tx.`meter_type_read`.`app_id` = 603 AND 
		zhuboss_tx.`meter_type_read`.`meter_type_id` =zhuboss_tx.`meter_type`.id AND zhuboss_tx.meter_kind.id = zhuboss_tx.`meter_type`.`meter_kind_id` AND kind_code = test.`meter_type`.`meter_type` AND type_name = test.`meter_type`.`type_name`
		) AS read_id,
	CAST(CONV(args,16 ,10) AS DECIMAL) AS addr,
	SUBSTR(CONCAT('0000',args),-4) AS addr_hex,
	test.`meter_type_target`.`ratiovar`,
	NOW()
 FROM test.`meter_type_target` JOIN test.`meter_type` ON meter_type_id=test.`meter_type`.id
WHERE (meter_kind  NOT IN ('dc_panel','cap')  OR (meter_kind = 'cap' AND new_loop=0) OR (meter_kind = 'dc_panel' AND test.`meter_type_target`.`id`<318))
AND EXISTS(SELECT 1 FROM test.`meter_kind_target` WHERE test.`meter_kind_target`.meter_kind = test.`meter_type`.`meter_type` AND test.`meter_kind_target`.target_code = test.`meter_type_target`.`target_code`)
 ORDER BY meter_kind,CAST(CONV(args,16 ,10) AS DECIMAL)
 
 -- update  meter_type_read
 UPDATE zhuboss_tx.`meter_type_read`
 SET start_addr = (SELECT MIN(addr) FROM `meter_type_read_target` WHERE  zhuboss_tx.`meter_type_read_target`.`read_id` = meter_type_read.`id`),
 end_addr = (SELECT IF((value_type = 'IEEE754' OR value_type= 'INT32_CDAB' OR value_type='INT32_ABCD'), addr+1,addr) FROM  `meter_type_read_target` WHERE  `meter_type_read_target`.`read_id` = meter_type_read.`id` ORDER BY addr DESC LIMIT 1)
 WHERE app_id = 603;
 
  UPDATE zhuboss_tx.`meter_type_read`
  SET len=(end_addr - start_addr ) + 1
 WHERE app_id = 603 ; 

 
-- <<< API 部分>>>
   
 -- 站点
 TRUNCATE TABLE test_station;
 INSERT INTO test_station SELECT id ,station_name ,lng ,lat FROM station;
 
 UPDATE zhuboss_tx.station SET station_no = (SELECT id FROM test.station  WHERE station_name = zhuboss_tx.station.`text`) WHERE app_id = 603;
   
-- 网关注册
TRUNCATE TABLE test_collector;
INSERT INTO `test`.`test_collector` (`id`, `station_id`,  `collector_type`,  collector_no,  `dev_secret`)  SELECT id ,station_id,collect_type,con_no,dev_secret FROM dev_collector;

-- 设备注册
TRUNCATE TABLE test.test_meter;
INSERT INTO `test`.`test_meter` (  `id`,  `collector_id`,  `interface_type`,   `com_port`,  `addr`,  `meter_type_id`,  `dev_name`) 
SELECT
  `test`.`dev_collector_link`.`id`           AS `id`,
  `test`.`dev_collector_link`.`collector_id` AS `collectorId`,
  'tcp'                                      AS `interfaceType`,
   `test`.`dev_collector_link`.`com_port` AS com_port,
  `test`.`dev_collector_link`.`con_no`       AS `addr`,
  (SELECT
     `zhuboss_tx`.`meter_type`.`id`
   FROM (`zhuboss_tx`.`meter_type`
      JOIN `zhuboss_tx`.`meter_kind`)
   WHERE ((`zhuboss_tx`.`meter_type`.`app_id` = 603)
          AND (`zhuboss_tx`.`meter_kind`.`app_id` = 603)
          AND (`zhuboss_tx`.`meter_type`.`meter_kind_id` = `zhuboss_tx`.`meter_kind`.`id`)
          AND (`zhuboss_tx`.`meter_kind`.`kind_code` = `test`.`dev_collector_link`.`meter_kind`)
          AND (`zhuboss_tx`.`meter_type`.`type_name` = `test`.`meter_type`.`type_name`))) AS `meterTypeId`,
  (CASE `test`.`dev_collector_link`.`bind_target` WHEN 'CTW' THEN (SELECT `test`.`dev_ctw`.`dev_name` FROM `test`.`dev_ctw` WHERE (`test`.`dev_ctw`.`id` = `test`.`dev_collector_link`.`bind_target_id`)) WHEN 'cap' THEN (SELECT `test`.`dev_cap`.`dev_name` FROM `test`.`dev_cap` WHERE (`test`.`dev_cap`.`id` = `test`.`dev_collector_link`.`bind_target_id`)) WHEN 'dc_panel' THEN (SELECT `test`.`dev_dc_panel`.`dev_name` FROM `test`.`dev_dc_panel` WHERE (`test`.`dev_dc_panel`.`id` = `test`.`dev_collector_link`.`bind_target_id`)) WHEN 'DEV_ORG' THEN (SELECT `test`.`dev_org`.`dev_name` FROM `test`.`dev_org` WHERE (`test`.`dev_org`.`id` = `test`.`dev_collector_link`.`bind_target_id`)) WHEN 'TRANSFORMER' THEN (SELECT `test`.`dev_transformer`.`dev_name` FROM `test`.`dev_transformer` WHERE (`test`.`dev_transformer`.`id` = `test`.`dev_collector_link`.`bind_target_id`)) WHEN 'BUSBAR' THEN (SELECT `test`.`dev_transformer`.`dev_name` FROM `test`.`dev_transformer` WHERE (`test`.`dev_transformer`.`id` = `test`.`dev_collector_link`.`bind_target_id`)) WHEN 'TRANSFORMER' THEN (SELECT `test`.`dev_busbar`.`dev_name` FROM `test`.`dev_busbar` WHERE (`test`.`dev_busbar`.`id` = `test`.`dev_collector_link`.`bind_target_id`)) WHEN 'ZB' THEN (SELECT `test`.`dev_zb`.`dev_name` FROM `test`.`dev_zb` WHERE (`test`.`dev_zb`.`id` = `test`.`dev_collector_link`.`bind_target_id`)) WHEN 'WO' THEN (SELECT `test`.`env_point`.`dev_name` FROM `test`.`env_point` WHERE (`test`.`env_point`.`id` = `test`.`dev_collector_link`.`bind_target_id`)) WHEN 'TH' THEN (SELECT `test`.`env_point`.`dev_name` FROM `test`.`env_point` WHERE (`test`.`env_point`.`id` = `test`.`dev_collector_link`.`bind_target_id`)) WHEN 'SM' THEN (SELECT `test`.`env_point`.`dev_name` FROM `test`.`env_point` WHERE (`test`.`env_point`.`id` = `test`.`dev_collector_link`.`bind_target_id`)) ELSE NULL END) AS `devName`
FROM ((`test`.`dev_collector_link`
    JOIN `test`.`dev_collector`)
   JOIN `test`.`meter_type`)
WHERE ((`test`.`dev_collector_link`.`collector_id` = `test`.`dev_collector`.`id`)
       AND (`test`.`dev_collector_link`.`meter_type_id` = `test`.`meter_type`.`id`))
       
AND EXISTS(  SELECT      `zhuboss_tx`.`meter_type`.`id`   FROM (`zhuboss_tx`.`meter_type`      JOIN `zhuboss_tx`.`meter_kind`)   WHERE ((`zhuboss_tx`.`meter_type`.`app_id` = 603)          AND (`zhuboss_tx`.`meter_kind`.`app_id` = 603)          AND (`zhuboss_tx`.`meter_type`.`meter_kind_id` = `zhuboss_tx`.`meter_kind`.`id`)          AND (`zhuboss_tx`.`meter_kind`.`kind_code` = `test`.`dev_collector_link`.`meter_kind`)          AND (`zhuboss_tx`.`meter_type`.`type_name` = `test`.`meter_type`.`type_name`)))     
       ;

-- 生成tcp端口号
UPDATE test.test_meter , (SELECT t.*,@rownum := @rownum +1 AS rownum FROM (SELECT DISTINCT `collector_id`,com_port FROM test.`test_meter`) t,(SELECT @rownum := 0) r ORDER BY t.collector_id ,t.com_port) temp
SET 
	test.test_meter.host='modbus_tcp.zutai.cn',
	test.test_meter.port = 55000+ temp.rownum
WHERE 	test.test_meter.`collector_id` = temp.collector_id AND test.test_meter.`com_port` = temp.com_port;

-- 越限告警
CREATE TABLE test_meter_alarm AS SELECT id ,station_id ,title, (SELECT id FROM zhuboss_tx.`meter_kind` WHERE kind_code = test.dev_alarm.meter_kind AND app_id = 603) AS meter_kind_id,from_value,to_value
FROM test.dev_alarm;

CREATE TABLE test_meter_alarm_data AS SELECT dev_alarm_data.id ,dev_alarm_id,
 (SELECT zhuboss_tx.`meter_kind_read`.id FROM zhuboss_tx.`meter_kind`,zhuboss_tx.`meter_kind_read` 
	WHERE kind_code = dev_alarm.meter_kind AND zhuboss_tx.`meter_kind_read`.`meter_kind_id` = zhuboss_tx.`meter_kind`.`id` AND zhuboss_tx.`meter_kind`.app_id = 603
	AND zhuboss_tx.`meter_kind_read`.`target_code` = dev_alarm_data.`data_id`) AS meter_kind_read_id
FROM test.dev_alarm_data,test.dev_alarm 
WHERE dev_alarm_data.`dev_alarm_id` = dev_alarm.`id`;

CREATE TABLE test_meter_alarm_dev
SELECT *FROM (
SELECT dev_alarm_dev.id, dev_alarm_dev.dev_alarm_id AS meter_alarm_id,
(SELECT id FROM `dev_collector_link` WHERE meter_kind = (SELECT zhuboss_tx.`meter_kind`.kind_code FROM zhuboss_tx.`meter_kind` WHERE kind_code = dev_alarm.meter_kind  AND zhuboss_tx.`meter_kind`.app_id = 603) AND bind_target_id = test.`dev_alarm_dev`.`dev_id`) AS meter_id
FROM test.`dev_alarm_dev`,test.`dev_alarm`
WHERE dev_alarm_dev.`dev_alarm_id` = dev_alarm.`id`) t
WHERE meter_id IS NOT NULL;



