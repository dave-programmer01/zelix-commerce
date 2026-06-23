-- Seed data for categories table.
-- Source: Zelix MVP Specification v1.0, Section 8 (Category System).
-- Only Tier 1 (Physical Goods) and Tier 2 (Services) are seeded - Tier 3 (Digital)
-- and Tier 4 (Financial) are explicitly excluded from MVP per the spec.
-- All rows are top-level (parent_id = NULL) - the source doc lists tiers as flat
-- lists with no explicit parent/child mapping between them.
-- ON CONFLICT DO NOTHING makes this safe to re-run on every app startup.

-- Tier 1 - Physical Goods
INSERT INTO categories (id, name, category_type, parent_id, created_at, updated_at) VALUES
                                                                                        ('3d321f47-4b8f-40bf-8b11-3d927162708e', 'Food and Drinks', 'PHYSICAL', NULL, now(), now()),
                                                                                        ('312fa9b5-8bc9-4717-bac3-1b6a4d91df8b', 'Fashion and Thrift', 'PHYSICAL', NULL, now(), now()),
                                                                                        ('723e366a-274b-4b4f-a368-a794f16011c9', 'Electronics and Gadgets', 'PHYSICAL', NULL, now(), now()),
                                                                                        ('2508a702-548e-478f-b271-4e6835e5fcc2', 'Beauty and Personal Care', 'PHYSICAL', NULL, now(), now()),
                                                                                        ('1a20dbf7-eadc-4f9b-8cc3-5d98470d8431', 'Home and Decor', 'PHYSICAL', NULL, now(), now()),
                                                                                        ('8e9ceab4-75eb-45d2-8d23-add6d3f25a35', 'Books and Stationery', 'PHYSICAL', NULL, now(), now()),
                                                                                        ('c7c26773-5ee1-4219-bd18-99bf76d1b43f', 'Baby and Kids', 'PHYSICAL', NULL, now(), now()),
                                                                                        ('8151d879-3a89-4b5b-aed3-76714273edc8', 'Health and Wellness', 'PHYSICAL', NULL, now(), now()),
                                                                                        ('495dcfef-fdb3-437f-a31c-980022314bb2', 'Sporting Goods', 'PHYSICAL', NULL, now(), now()),
                                                                                        ('d3b852ee-2697-4163-8787-696dd199cc0a', 'Auto Parts and Accessories', 'PHYSICAL', NULL, now(), now()),
                                                                                        ('de91d34d-1604-4d0f-87ed-2c1d85a1b23a', 'Agricultural Produce', 'PHYSICAL', NULL, now(), now()),
                                                                                        ('63b8f4a1-5ec7-4dfa-9442-4a6eb037f6c5', 'Arts and Crafts', 'PHYSICAL', NULL, now(), now()),
                                                                                        ('45f941bd-506a-46b8-84bb-2dd00dcaf291', 'Phones and Accessories', 'PHYSICAL', NULL, now(), now())
ON CONFLICT (id) DO NOTHING;

-- Tier 2 - Services
INSERT INTO categories (id, name, category_type, parent_id, created_at, updated_at) VALUES
                                                                                        ('fbfd56d1-69e7-4303-a9b9-f3116fdb265b', 'Food and Catering', 'SERVICE', NULL, now(), now()),
                                                                                        ('62a755d3-dd42-4e18-81e5-0ba1d5bd1746', 'Hair and Beauty Services', 'SERVICE', NULL, now(), now()),
                                                                                        ('bab76dba-b097-4988-bc85-119d58083ecc', 'Laundry and Cleaning', 'SERVICE', NULL, now(), now()),
                                                                                        ('5b0a1c82-184c-4576-b80c-8eee854039ac', 'Repairs and Maintenance', 'SERVICE', NULL, now(), now()),
                                                                                        ('6ae084c8-02ed-4b33-b942-b052b2f618a0', 'Logistics and Delivery', 'SERVICE', NULL, now(), now()),
                                                                                        ('9bb25e5c-ec4b-443f-b3bd-dd3ab977694f', 'Photography and Videography', 'SERVICE', NULL, now(), now()),
                                                                                        ('d7f3ab3e-3ebb-46c1-a816-c0bd2c3885b7', 'Tutoring and Education', 'SERVICE', NULL, now(), now()),
                                                                                        ('1c6ef18b-635b-47e1-ac1d-1935596a6c20', 'Legal and Business Services', 'SERVICE', NULL, now(), now()),
                                                                                        ('3a645686-cb65-4517-878d-1d65253be448', 'Event Planning', 'SERVICE', NULL, now(), now()),
                                                                                        ('9ff4670a-19e3-45e2-aa25-d2e865dc6b8d', 'Fitness and Wellness Coaching', 'SERVICE', NULL, now(), now())
ON CONFLICT (id) DO NOTHING;