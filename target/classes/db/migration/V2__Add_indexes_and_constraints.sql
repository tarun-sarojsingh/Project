-- ============================================================
-- V2__Add_indexes_and_constraints.sql
-- Performance indexes and additional constraints
-- Compatible with both H2 (dev) and PostgreSQL (prod)
-- ============================================================

-- Index on authors.email for fast lookup during login/validation
CREATE INDEX IF NOT EXISTS idx_authors_email
    ON authors (email);

-- Index on authors.last_name for search queries
CREATE INDEX IF NOT EXISTS idx_authors_last_name
    ON authors (last_name);

-- Index on books.isbn for fast duplicate checks
CREATE INDEX IF NOT EXISTS idx_books_isbn
    ON books (isbn);

-- Index on books.author_id for fast join queries
CREATE INDEX IF NOT EXISTS idx_books_author_id
    ON books (author_id);

-- Index on books.genre for filtering
CREATE INDEX IF NOT EXISTS idx_books_genre
    ON books (genre);

-- Index on users.username for fast authentication lookups
CREATE INDEX IF NOT EXISTS idx_users_username
    ON users (username);

-- Index on user_roles.user_id for fast role resolution
CREATE INDEX IF NOT EXISTS idx_user_roles_user_id
    ON user_roles (user_id);
