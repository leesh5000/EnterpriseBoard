create table article (
                        article_id bigint not null primary key comment '게시글 ID',
                        title varchar(100) not null comment '제목',
                        content varchar(3000) not null comment '내용',
                        board_id bigint not null comment '게시판 ID',
                        writer_id bigint not null comment '작성자 ID',
                        created_at datetime not null comment '작성일',
                        modified_at datetime not null comment '수정일'
);

create index idx_board_id_article_id on article(board_id asc, article_id desc);

create table board_article_count (
                                    board_id bigint not null primary key comment '게시판 ID',
                                    article_count bigint not null comment '게시글 수'
);

create table outbox (
                       outbox_id bigint not null primary key comment 'Outbox ID',
                       shard_key bigint not null comment '샤드 키',
                       event_type varchar(100) not null comment '이벤트 타입',
                       payload varchar(5000) not null comment '페이로드',
                       created_at datetime not null comment '생성일'
);

create index idx_shard_key_created_at on outbox(shard_key asc, created_at asc);