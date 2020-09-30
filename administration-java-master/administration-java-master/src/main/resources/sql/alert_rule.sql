create table alert_rule
(
    id              bigint auto_increment comment '信息警报的ID'
        primary key,
    alert_rule_id   varchar(255) not null comment '信息警报的规则ID集合',
    alert_machines  varchar(100) not null comment '规则的机器集合',
    alert_rule_note varchar(255) not null comment '信息警报ID的注释规则',
    alert_rule_time datetime     not null comment '组合规则的插入时间'
);
