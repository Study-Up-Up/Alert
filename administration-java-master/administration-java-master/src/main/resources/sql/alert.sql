create table alert
(
    id              bigint auto_increment comment '警报的主键'
        primary key,
    title           varchar(255) charset utf8mb4 default '' null comment '规则的标题',
    leftexpression  varchar(255) charset utf8mb4 default '' null comment '规则的左表达式',
    midexpression   varchar(255) charset utf8mb4 default '' null comment '规则的中间运算符',
    rightexpression varchar(255) charset utf8mb4 default '' null comment '规则的右表达式',
    note            varchar(255) charset utf8mb4 default '' null comment '规则的注释',
    time            datetime                                null comment '规则的插入时间'
)
    collate = utf8mb4_vi_0900_ai_ci;
