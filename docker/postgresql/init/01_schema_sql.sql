-- Crea schema si quieres uno distinto a "public"
 CREATE SCHEMA IF NOT EXISTS auth;

-- Extensiones útiles (opcional)
 CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Tabla mínima de ejemplo (ajústala a tus migraciones reales)
 CREATE TABLE IF NOT EXISTS public.roles (
   id BIGSERIAL PRIMARY KEY,
   name VARCHAR(100) UNIQUE NOT NULL,
   description VARCHAR(255)
 );

 CREATE TABLE IF NOT EXISTS public.users (
   id BIGSERIAL PRIMARY KEY,
   name VARCHAR(100) NOT NULL,
   lastname VARCHAR(100) NOT NULL,
   email VARCHAR(150) UNIQUE NOT NULL,
   identity_document VARCHAR(50) UNIQUE,
   phone VARCHAR(20),
   role_id BIGINT NOT NULL REFERENCES public.roles(id),
   base_salary INTEGER
 );
