rootProject.name = "enterprise-board"

include(
    "common",
    "common:snowflake",
    "service",
    "service:article",
    "service:comment",
    "service:view",
    "service:like",
    "service:hot-article",
    "service:article-read"
)
