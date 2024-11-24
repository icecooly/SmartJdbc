CREATE TABLE "public"."t_article" (
  "id" int4,
  "title" varchar(32) COLLATE "pg_catalog"."default",
  "content" text COLLATE "pg_catalog"."default",
  "status" int4,
  "create_user_id" int4,
  "update_user_id" int4,
  "favoriteUserList" text COLLATE "pg_catalog"."default",
  "create_time" timestamp(6),
  "update_time" timestamp(6)
)
;
