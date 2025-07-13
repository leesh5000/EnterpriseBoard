create table comment (
    comment_id bigint not null primary key comment '댓글 ID',
    content varchar(1000) not null comment '댓글 내용',
    parent_comment_id bigint not null comment '부모 댓글 ID',
    article_id bigint not null comment '게시글 ID',
    writer_id bigint not null comment '작성자 ID',
    deleted boolean not null default false comment '삭제 여부',
    created_at datetime not null comment '작성일'
);

create index idx_article_id_comment_id on comment(article_id asc, comment_id desc);
create index idx_parent_comment_id_comment_id on comment(parent_comment_id asc, comment_id desc);