-- 1. Insert Users (Using plain text passwords for testing)
INSERT INTO users (id, name, password, authorities, created_at) VALUES
                                                                    (1, 'admin', 'Password@123', 'ROLE_ADMIN', CURRENT_TIMESTAMP),
                                                                    (2, 'abinash', 'Password@123', 'ROLE_STUDENT', CURRENT_TIMESTAMP),
                                                                    (3, 'jane', 'Password@123', 'ROLE_STUDENT', CURRENT_TIMESTAMP);

-- 2. Insert Students
INSERT INTO students (id, user_id, roll_number, name, email, department, joining_year) VALUES
                                                                                           (1, 2, '21CS104', 'Abinash Dwibedi', 'abinash@campus.edu', 'Computer Science', 2021),
                                                                                           (2, 3, '21EE055', 'Jane Smith', 'jane@campus.edu', 'Electrical Engineering', 2022);

-- 3. Insert Clubs
INSERT INTO clubs (id, club_code, name, category, contact_email, is_active) VALUES
                                                                                (1, 'CTECH', 'CodeCrafters', 'TECHNICAL', 'codecrafters@campus.edu', TRUE),
                                                                                (2, 'CVFX', 'Cinematic Media Society', 'CULTURAL', 'media@campus.edu', TRUE);

-- 4. Insert Club Memberships
INSERT INTO club_memberships (id, club_id, user_id, designation, has_edit_access, joined_at) VALUES
                                                                                                 (1, 1, 2, 'President', TRUE, CURRENT_TIMESTAMP),
                                                                                                 (2, 2, 2, 'Lead Editor', TRUE, CURRENT_TIMESTAMP),
                                                                                                 (3, 1, 3, 'Member', FALSE, CURRENT_TIMESTAMP);

-- 5. Insert Events
INSERT INTO events (id, title, description, start_time, venue, max_capacity, status, club_id) VALUES
                                                                                                  (1, 'LeetCode Biweekly & DFS Algorithms', 'Competitive programming session focusing on graph traversals.', '2026-08-22 10:00:00', 'CS Lab 1', 50, 'UPCOMING', 1),
                                                                                                  (2, 'After Effects & Premiere Pro Bootcamp', 'Learn dynamic link workflows and how to build cyberpunk-style cinematic intros.', '2026-08-25 14:00:00', 'Media Center', 30, 'UPCOMING', 2),
                                                                                                  (3, 'Database Schema Architecture', 'Relational algebra and backend schema optimization marathon.', '2026-08-10 09:00:00', 'Auditorium', 100, 'COMPLETED', 1);

-- 6. Insert Event Registrations
INSERT INTO event_registrations (id, registered_at, event_id, student_id) VALUES
                                                                              (1, CURRENT_TIMESTAMP, 1, 1),
                                                                              (2, CURRENT_TIMESTAMP, 2, 1),
                                                                              (3, CURRENT_TIMESTAMP, 1, 2),
                                                                              (4, CURRENT_TIMESTAMP, 3, 2);