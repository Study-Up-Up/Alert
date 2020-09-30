create table alert_msg
(
    msg_id          bigint auto_increment comment '消息的主键'
        primary key,
    project         varchar(255) not null comment '项目',
    project_machine varchar(255) not null comment '项目的机器',
    rule            varchar(255) not null comment '规则',
    rule_number     varchar(255) not null comment '规则的数字',
    msg             varchar(255) not null comment '消息',
    msg_code        varchar(255) not null comment '消息的code',
    msg_time        datetime     not null comment '消息的时间'
);