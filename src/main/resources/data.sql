insert into policy (id, policy_tier) values (1, 'SILVER'), (2, 'GOLD'), (3, 'PLATINUM');

insert into persons (id, name, surname, age, country, city, street, role, policy_id) values
(1, 'V', 'Unknown', 27, 'NUSA', 'Night City', 'H10 Megabuilding, Little China', 'CLIENT', 1),
(2, 'Jackie', 'Welles', 31, 'NUSA', 'Night City', 'The Glen, Heywood', 'CLIENT', 1),
(3, 'Judy', 'Alvarez', 26, 'NUSA', 'Night City', 'Lizzie''s Bar, Kabuki', 'CLIENT', null),
(4, 'Panam', 'Palmer', 29, 'NUSA', 'Badlands', 'Rocky Ridge camp', 'CLIENT', null),
(5, 'Johnny', 'Silverhand', 89, 'NUSA', 'Night City', 'Mikoshi, sublevel 7', 'CLIENT', null),
(6, 'Hanako', 'Arasaka', 66, 'Japan', 'Tokyo', 'Arasaka Estate', 'CLIENT', 2),
(7, 'Rogue', 'Amendiares', 87, 'NUSA', 'Night City', 'Afterlife, Watson', 'CLIENT', null),
(8, 'River', 'Ward', 41, 'NUSA', 'Night City', 'Vista Del Rey, Heywood', 'CLIENT', null),
(9, 'Goro', 'Takemura', 49, 'Japan', 'Tokyo', 'Shinjuku', 'CLIENT', null),
(10, 'Misty', 'Olszewski', 26, 'NUSA', 'Night City', 'Misty''s Esoterica, Watson', 'CLIENT', null),
(11, 'Meredith', 'Stout', 38, 'NUSA', 'Night City', 'Corpo Plaza, City Center', 'CLIENT', 2),
(12, 'Dexter', 'DeShawn', 52, 'NUSA', 'Night City', 'a landfill in Rancho Coronado', 'CLIENT', null),
(13, 'Evelyn', 'Parker', 30, 'NUSA', 'Night City', 'Clouds, Jig-Jig Street', 'CLIENT', null),
(14, 'Sandra', 'Dorsett', 34, 'NUSA', 'Night City', 'Charter Hill, Westbrook', 'CLIENT', 3),
(15, 'Wakako', 'Okada', 68, 'NUSA', 'Night City', 'Japantown, Westbrook', 'CLIENT', null),
(16, 'Regina', 'Jones', 45, 'NUSA', 'Night City', 'Little China, Watson', 'CLIENT', null),
(17, 'Dakota', 'Smith', 54, 'NUSA', 'Badlands', 'Jackson Plains', 'CLIENT', null),
(18, 'Claire', 'Russell', 40, 'NUSA', 'Night City', 'Afterlife, Watson', 'CLIENT', null),
(19, 'Saul', 'Bright', 55, 'NUSA', 'Badlands', 'Rocky Ridge camp', 'CLIENT', null),
(20, 'Kerry', 'Eurodyne', 84, 'NUSA', 'Night City', 'North Oak, Westbrook', 'CLIENT', null),
(21, 'Mitch', 'Anderson', 42, 'NUSA', 'Night City', 'Pacifica Pier', 'PILOT', null),
(22, 'Viktor', 'Vektor', 58, 'NUSA', 'Night City', 'Kabuki Market', 'MEDIC', null);

insert into clients (person_id, email, join_date) values
(1, 'v.merc@nightcity.example', '2077-01-12'),
(2, 'jackie_w@heywood.example', '2076-11-03'),
(3, 'judy@lizzies.example', '2076-02-28'),
(4, 'panam@aldecaldos.example', '2075-07-19'),
(5, 'rockerboy@samurai.example', '2023-08-20'),
(6, 'h.arasaka@arasaka.example', '2061-04-01'),
(7, 'queen@afterlife.example', '2049-10-30'),
(8, 'rward@ncpd.example', '2070-05-14'),
(9, 'takemura.g@arasaka.example', '2068-09-09'),
(10, 'misty.o@esoterica.example', '2076-12-24'),
(11, 'mstout@militech.example', '2074-03-16'),
(12, 'dex@afterlife.example', '2069-06-06'),
(13, 'evelyn@clouds.example', '2076-08-21'),
(14, 'sandra.dorsett@biotechnica.example', '2072-01-30'),
(15, 'okada@tygerclaws.example', '2058-11-11'),
(16, 'regina@n54.example', '2071-02-02'),
(17, 'dakota@jackson.example', '2067-05-25'),
(18, 'claire@afterlife.example', '2073-10-10'),
(19, 'saul@aldecaldos.example', '2066-07-07'),
(20, 'kerry@usrockers.example', '2031-12-31');

insert into trauma_team (id, team_codename, rank) values (1, 'Team Kilo-6', 'ADVANCED');

insert into pilots (person_id, nickname, team_id) values (21, 'Tin Man', 1);

insert into medics (person_id, specialisation, team_id) values (22, 'Ripperdoc', 1);
insert into medic_weapon_cert (medic_id, weapon_certifications) values (22, 'Pistol'), (22, 'Shotgun');

insert into chrome (id, name, implant_tier, owner_id) values
(1, 'Kiroshi optics', 'TIER_2', 1),
(2, 'Gorilla Arms', 'TIER_3', 2);

insert into aerial_vehicle (id, vehicle_type, serial_number, home_base, vehicle_state, horse_power, range_km, max_altitudem, max_speed_kmh, trauma_team_id) values
(1, 'HELI', 'TT-HX-2077', 'Corpo Plaza pad', 'IN_SERVICE', 1800, 650, null, null, 1),
(2, 'ION', 'TT-AV-0451', 'Pacifica pad', 'IN_SERVICE', null, null, 9000, 420, 1);

insert into combat (vehicle_id) values (1);
insert into combat_armament (vehicle_id, armament) values (1, 'Door gun'), (1, 'Smart missiles');

insert into medical (vehicle_id, client_capacity) values (2, 2);

insert into mission (id, name, description, mission_status) values
(1, 'Konpeki Plaza', 'Arasaka closed the airspace, AV turned back. Client lost.', 'FAILED'),
(2, 'Dorsett pickup', 'Platinum client, Maelstrom scav haunt in Watson. Sub-zero bath, vitals dropping.', 'IN_PROGRESS'),
(3, 'All Foods plant', 'Shots fired at the plant in Northside. Expect Maelstrom on site.', 'READY'),
(4, 'Afterlife call', 'Someone flatlined in the back room again. Rogue wants it quiet.', 'UNASSIGNED'),
(5, 'Pacifica welfare check', 'NCPD won''t go in. Voodoo Boys territory, stay in the AV.', 'UNSPECIFIED'),
(6, 'Badlands run', 'Dust storm over Rocky Ridge, wait for it to clear.', 'UNSPECIFIED');

insert into objective (id, description, status, mission_id, team_id) values
(1, 'Land on the Konpeki roof and get the client out', 'FAILED', 1, 1),
(2, 'Stabilise client, then fly to Corpo Plaza', 'IN_PROGRESS', 2, 1),
(3, 'Secure the loading dock', 'READY', 3, 1),
(4, 'Find out who actually called it in', 'UNASSIGNED', 4, null);

insert into mission_client (mission_id, client_id) values
(1, 1), (1, 2), (1, 12), (1, 13),
(2, 14),
(3, 4), (3, 17), (3, 19),
(4, 3), (4, 7), (4, 18),
(5, 10);

-- ids above are hardcoded so hibernate has to start later
alter sequence policy_seq restart with 1000;
alter sequence persons_seq restart with 1000;
alter sequence trauma_team_seq restart with 1000;
alter sequence chrome_seq restart with 1000;
alter sequence aerial_vehicle_seq restart with 1000;
alter sequence mission_seq restart with 1000;
alter sequence objective_seq restart with 1000;
