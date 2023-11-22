DROP SCHEMA IF EXISTS "genai" CASCADE;

CREATE SCHEMA "genai";
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
DROP TYPE IF EXISTS review_status;
CREATE TYPE review_status as ENUM ('LABELED', 'NOT_LABELED');

DROP TABLE IF EXISTS "genai".review CASCADE;
CREATE TABLE "genai".review
(
    id uuid NOT NULL,
    content character varying COLLATE pg_catalog."default" NOT NULL,
    rate integer NOT NULL,
    project_id character varying COLLATE pg_catalog."default" NOT NULL,
    status review_status NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    labels text[] NOT NULL,
    review_id character varying COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT review_key PRIMARY KEY (id)
)