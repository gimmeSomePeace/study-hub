-- =====================================================================================
-- TABLE: Users
-- =====================================================================================

CREATE TABLE app_users (
    id            UUID         PRIMARY KEY,
    login         VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    display_name  VARCHAR(255) NOT NULL,
    role          VARCHAR(20)  NOT NULL,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_app_users_login UNIQUE (login)
);

CREATE INDEX idx_app_users_login ON app_users (login);

-- =====================================================================================
-- TABLE: Semesters
-- =====================================================================================

CREATE TABLE semesters (
    id         UUID         PRIMARY KEY,
    owner_id   UUID         NOT NULL,
    name       VARCHAR(80)  NOT NULL,
    starts_at  TIMESTAMPTZ  NOT NULL,
    ends_at    TIMESTAMPTZ  NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_semesters_app_users
        FOREIGN KEY (owner_id) REFERENCES app_users (id),

    CONSTRAINT chk_semesters_dates
        CHECK (ends_at > starts_at)
);

CREATE INDEX idx_semesters_owner_id ON semesters (owner_id);

-- =====================================================================================
-- TABLE: Subjects
-- =====================================================================================

CREATE TABLE subjects (
    id          UUID         PRIMARY KEY,
    semester_id UUID         NOT NULL,
    name        VARCHAR(200) NOT NULL,
    code        VARCHAR(50),
    teacher     VARCHAR(200),
    color       VARCHAR(7),
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_subjects_semesters_id
        FOREIGN KEY (semester_id) REFERENCES semesters (id),

    CONSTRAINT chk_subjects_color
        CHECK (color IS NULL OR color ~ '^#[0-9A-Fa-f]{6}$')
);

CREATE INDEX idx_subjects_semester_id ON subjects (semester_id);

COMMENT ON TABLE  subjects       IS 'Предметы в семестре';
COMMENT ON COLUMN subjects.code  IS 'Код предмета (например, CS101)';
COMMENT ON COLUMN subjects.color IS 'Цвет в формате #RRGGBB';

-- =====================================================================================
-- TABLE: Subject Components
-- =====================================================================================

CREATE TABLE subject_components (
    id         UUID         PRIMARY KEY,
    subject_id UUID         NOT NULL,
    type       VARCHAR(50)  NOT NULL,
    title      VARCHAR(200) NOT NULL,
    priority   INT          NOT NULL DEFAULT 3,
    notes      VARCHAR(2000),
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_components_subject
        FOREIGN KEY (subject_id) REFERENCES subjects (id),

    CONSTRAINT chk_components_type
        CHECK (type IN ('LECTURE', 'LAB', 'PRACTICE', 'SEMINAR', 'OTHER')),

    CONSTRAINT chk_components_priority
        CHECK (priority BETWEEN 1 AND 5)
);

CREATE INDEX idx_components_subject_id ON subject_components (subject_id);

COMMENT ON TABLE  subject_components          IS 'Компоненты предметов: лекции, лабы, практики';
COMMENT ON COLUMN subject_components.type     IS 'Тип компонента: LECTURE, LAB, PRACTICE, SEMINAR, OTHER';
COMMENT ON COLUMN subject_components.priority IS 'Приоритет от 1 до 5';

-- =====================================================================================
-- TABLE: Deadlines
-- =====================================================================================

CREATE TABLE deadlines (
    id           UUID         PRIMARY KEY,
    subject_id   UUID         NOT NULL,
    component_id UUID,
    title        VARCHAR(200) NOT NULL,
    due_at       TIMESTAMPTZ  NOT NULL,
    type         VARCHAR(50)  NOT NULL,
    status       VARCHAR(20)  NOT NULL DEFAULT 'OPEN',
    notes        VARCHAR(2000),
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_deadline_subjects
        FOREIGN KEY (subject_id) REFERENCES subjects (id),

    CONSTRAINT fk_deadline_components
        FOREIGN KEY (component_id) REFERENCES subject_components (id),

    CONSTRAINT chk_deadlines_type
        CHECK (type IN ('EXAM', 'LAB', 'CONTROL', 'PROJECT', 'OTHER')),

    CONSTRAINT chk_deadlines_status
        CHECK (status IN ('OPEN', 'CLOSED', 'CANCELLED'))
);

CREATE INDEX idx_deadlines_subjects_id   ON deadlines (subject_id);
CREATE INDEX idx_deadlines_components_id ON deadlines (component_id);

COMMENT ON TABLE  deadlines              IS 'Дедлайны по предметам';
COMMENT ON COLUMN deadlines.component_id IS 'Опциональная привязка к компоненту предмета';
COMMENT ON COLUMN deadlines.status       IS 'Статус: OPEN, CLOSED, CANCELLED';
