CREATE TABLE users (
                       id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,  -- same ID as Supabase Auth user
                       username VARCHAR(100) UNIQUE NOT NULL,
                       full_name VARCHAR(255),
                       created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE notes (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       user_id UUID REFERENCES users(id) ON DELETE CASCADE,
                       title VARCHAR(255) NOT NULL,
                       content TEXT,
                       image_urls JSONB DEFAULT '[]'::jsonb, -- store multiple Supabase Storage URLs
                       created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
                       updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE reminders (
                           id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                           note_id UUID NOT NULL REFERENCES notes(id) ON DELETE CASCADE,
                           reminder_at TIMESTAMP WITH TIME ZONE NOT NULL,
                           is_triggered BOOLEAN DEFAULT FALSE,
                           created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);