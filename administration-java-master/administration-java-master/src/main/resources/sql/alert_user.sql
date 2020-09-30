create table alert_user
(
    alert_id       bigint auto_increment comment '规则管理系统的用户主键Id'
        primary key,
    alert_username varchar(255) not null comment '规则管理系统的用户名',
    alert_password varchar(255) not null comment '规则管理系统的用户密码',
    alert_insert   datetime     not null comment '规则管理系统的用户创建时间'
);
